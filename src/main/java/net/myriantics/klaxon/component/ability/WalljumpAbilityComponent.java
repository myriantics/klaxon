package net.myriantics.klaxon.component.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalEntityTypeTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.ObserverBlock;
import net.minecraft.component.ComponentMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.item.equipment.tools.HammerItem;
import net.myriantics.klaxon.mixin.ObserverBlockInvoker;
import net.myriantics.klaxon.registry.minecraft.KlaxonAdvancementTriggers;
import net.myriantics.klaxon.registry.minecraft.KlaxonDataComponentTypes;
import net.myriantics.klaxon.tag.klaxon.KlaxonEntityTypeTags;
import net.myriantics.klaxon.util.AbilityModifierCalculator;
import net.myriantics.klaxon.util.EntityWeightHelper;
import net.myriantics.klaxon.util.PermissionsHelper;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.block.FacingBlock.FACING;

// When present on an item, allows it to be used to perform a walljump by attacking the ground with positive Y velocity
public record WalljumpAbilityComponent(float velocityMultiplier, boolean shouldUpdateObservers) {

    public static final Codec<WalljumpAbilityComponent> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                Codec.FLOAT.fieldOf("velocity_multiplier").forGetter(WalljumpAbilityComponent::velocityMultiplier),
                Codec.BOOL.fieldOf("should_update_observers").forGetter(WalljumpAbilityComponent::shouldUpdateObservers)
        ).apply(instance, WalljumpAbilityComponent::new);
    });

    public static final PacketCodec<ByteBuf, WalljumpAbilityComponent> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.FLOAT, WalljumpAbilityComponent::velocityMultiplier,
            PacketCodecs.BOOL, WalljumpAbilityComponent::shouldUpdateObservers,
            WalljumpAbilityComponent::new
    );

    public static @Nullable WalljumpAbilityComponent get(ItemStack stack) {
        return stack.getComponents().get(KlaxonDataComponentTypes.WALLJUMP_ABILITY);
    }

    public static void set(ItemStack stack, WalljumpAbilityComponent component) {
        stack.applyComponentsFrom(ComponentMap.builder().add(KlaxonDataComponentTypes.WALLJUMP_ABILITY, component).build());
    }

    // rising edge block hit - uses MinecraftClientMixin and HammerWalljumpTriggerPacket
    // called on both client and server
    public void processHammerWalljump(PlayerEntity player, World world, BlockPos pos, Direction direction) {

        BlockState targetBlockState = world.getBlockState(pos);

        if (player == null) {
            KlaxonCommon.LOGGER.error("Player is null when trying to do a Hammer Walljump. Why?");
            return;
        }

        ItemStack walljumpStack = player.getMainHandStack();

        float attackCooldownProgress = player.getAttackCooldownProgress(0.5f);

        boolean canWalljumpWithEntity = canWalljumpWithEntity(player, walljumpStack, targetBlockState);

        // validate that player has sufficient attack cooldown and satisfies conditions for walljump
        if (attackCooldownProgress > 0.8 && (canWalljumpWithEntity || canStandardWallJump(player, walljumpStack, targetBlockState))) {

            world.addBlockBreakParticles(pos, targetBlockState);

            Entity movedEntity = canWalljumpWithEntity ? player.getVehicle() : player;

            // velocity needs to be multiplied by 8 because minecarts don't play well with velocity
            // you know that one spongebob meme where they go over the little bump in the rollercoaster
            // thats this easter egg without this change
            boolean walljumpSucceeded = processWallJumpPhysics(player, movedEntity, velocityMultiplier);

            world.playSound(player, pos, SoundEvents.ENTITY_IRON_GOLEM_HURT, SoundCategory.PLAYERS, 2 * attackCooldownProgress, 2f * attackCooldownProgress);

            if (!world.isClient()) {
                // update observers monitoring target block - doesn't work in adventure
                if (PermissionsHelper.canModifyWorld(player) && shouldUpdateObservers) {
                    updateAdjacentMonitoringObservers(world, pos, targetBlockState);
                }

                KlaxonAdvancementTriggers.triggerHammerUse((ServerPlayerEntity) player, walljumpSucceeded ? HammerItem.UsageType.WALLJUMP_SUCCEEDED : HammerItem.UsageType.WALLJUMP_FAILED);
                // if player overpowered steel armor with strength proc this
                if (walljumpSucceeded && EntityWeightHelper.isHeavy(player)) {
                    KlaxonAdvancementTriggers.triggerHammerUse((ServerPlayerEntity) player, HammerItem.UsageType.STRENGTH_WALLJUMP_SUCCEEDED);
                }
                if (walljumpSucceeded && movedEntity.getType().isIn(ConventionalEntityTypeTags.MINECARTS)) {
                    KlaxonAdvancementTriggers.triggerHammerUse((ServerPlayerEntity) player, HammerItem.UsageType.MINECART_WALLJUMP_SUCCESS);
                }
            }

            // trip sculk sensors
            world.emitGameEvent(player, GameEvent.HIT_GROUND, pos);

            player.onLanding();

            player.resetLastAttackedTicks();

            // damage it wheee
            walljumpStack.damage(1, player, EquipmentSlot.MAINHAND);

        }
    }

    // yoinked from trident riptide physics - edited to suit my needs
    private static boolean processWallJumpPhysics(PlayerEntity sourcePlayer, Entity launchedEntity, float multiplier) {
        float playerYaw = sourcePlayer.getYaw();
        float playerPitch = sourcePlayer.getPitch();
        float h = MathHelper.sin(playerYaw * 0.017453292F) * MathHelper.cos(playerPitch * 0.017453292F);
        float k = MathHelper.sin(playerPitch * 0.017453292F);
        float l = -MathHelper.cos(playerYaw * 0.017453292F) * MathHelper.cos(playerPitch * 0.017453292F);
        float m = MathHelper.sqrt(h * h + k * k + l * l);
        float n = 0.6F * sourcePlayer.getAttackCooldownProgress(0.5f) * AbilityModifierCalculator.calculateHammerWalljumpMultiplier(sourcePlayer) * multiplier;
        h *= n / m;
        k *= n / m;
        l *= n / m;

        // this is needed because minecarts are wack and don't want to move horizontally as much
        if (launchedEntity instanceof MinecartEntity) {
            h *= 12;
            k *= 3;
            l *= 12;
        }

        launchedEntity.addVelocity(h, k, l);

        // returns false if failed, true if succeeded in moving player
        return n > 0;
    }

    // called in ItemMixin
    // present to prevent you from demolishing your world when walljumping around in creative
    public static boolean allowsMining(PlayerEntity miner) {
        KlaxonCommon.LOGGER.info("Walljump Ability Component: " + WalljumpAbilityComponent.get(miner.getWeaponStack()));
        if (miner.isCreative()) {
            // mining is allowed if there's no walljump ability component
            return WalljumpAbilityComponent.get(miner.getWeaponStack()) == null;
        }

        return true;
    }

    public static boolean canWallJump(PlayerEntity player, ItemStack walljumpStack, BlockState state) {
        return canWalljumpWithEntity(player, walljumpStack, state) || canStandardWallJump(player, walljumpStack, state);
    }

    public static boolean canStandardWallJump(PlayerEntity player, ItemStack wallJumpStack, BlockState state) {
        // originally you could use the hammer in spectator - funny, but not good.
        return !player.isSpectator()
                // prevents spammy bs when descending and unintentional hammer walljump procs
                && player.getVelocity().getY() > 0
                // make sure they're actually holding a walljumpable item
                && get(wallJumpStack) != null
                // allows players to not walljump if they don't want to
                && !player.isSneaking()
                // you cant walljump when you're in a boat or on a horse
                && player.getVehicle() == null
                // walljumping in water is janky
                && !player.isInFluid()
                // you can't walljump off of instabreakable blocks - in creative you can tho - also in adventure
                && (state.calcBlockBreakingDelta(player, null, null) < 1 || player.isCreative() || !player.getAbilities().allowModifyWorld);
    }

    public static boolean canWalljumpWithEntity(PlayerEntity player, ItemStack wallJumpStack, BlockState state) {
        // make sure there is a vehicle
        return player.getVehicle() != null
                // make sure vehicle is suitable for walljump
                && player.getVehicle().getType().isIn(KlaxonEntityTypeTags.WALLJUMP_MOVABLE_ENTITIES)
                // make sure you can actually walljump
                && get(wallJumpStack) != null
                // still can't walljump in spectator
                && !player.isSpectator()
                // block still has to be suitable
                && (state.calcBlockBreakingDelta(player, null, null) < 1 || player.isCreative() || !player.getAbilities().allowModifyWorld);
    }

    private void updateAdjacentMonitoringObservers(World world, BlockPos interactionPos, BlockState interactionState) {
        // block updating abilities
        // this quite literally allows you to hit something with a hammer to fix it
        world.updateNeighbor(interactionPos, interactionState.getBlock(), interactionPos);

        // trigger observers next to target block because its really funny
        for (Direction side : Direction.values()) {
            BlockPos observerPos = interactionPos.offset(side);
            BlockState observerState = world.getBlockState(observerPos);
            if (observerState.getBlock() instanceof ObserverBlock observerBlock) {
                if (observerPos.offset(observerState.get(FACING)).equals(interactionPos)) {
                    ((ObserverBlockInvoker) observerBlock).invokeScheduledTick(observerState, (ServerWorld) world, observerPos, world.getRandom());
                }
            }
        }
    }
}