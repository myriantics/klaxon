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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.myriantics.klaxon.item.equipment.tools.HammerItem;
import net.myriantics.klaxon.mixin.minecraft.item_components.walljump_ability.ObserverBlockInvoker;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementTriggers;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import net.myriantics.klaxon.registry.misc.KlaxonSoundEvents;
import net.myriantics.klaxon.tag.klaxon.KlaxonEntityTypeTags;
import net.myriantics.klaxon.util.AbilityModifierCalculator;
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

    public void set(ItemStack stack) {
        stack.applyComponentsFrom(ComponentMap.builder().add(KlaxonDataComponentTypes.WALLJUMP_ABILITY, this).build());
    }

    // rising edge block hit - uses MinecraftClientMixin and HammerWalljumpTriggerPacket
    // called on both client and server
    public void processHammerWalljump(PlayerEntity player, World world, BlockPos pos, Direction direction) {
        BlockState targetBlockState = world.getBlockState(pos);

        // validate this to make sure a random block pos was not passed in
        if (player == null || player.getEyePos().distanceTo(pos.toCenterPos()) > player.getBlockInteractionRange() * 2) {
            return;
        }

        ItemStack walljumpStack = player.getMainHandStack();

        float attackCooldownProgress = player.getAttackCooldownProgress(0.5f);

        boolean canWalljumpWithMount = canWalljumpWithMount(player, walljumpStack, targetBlockState);

        // validate that player has sufficient attack cooldown and satisfies conditions for walljump
        if (attackCooldownProgress > 0.7 && (!world.isClient() || canWalljumpWithMount || canStandardWallJump(player, walljumpStack, targetBlockState))) {
            world.addBlockBreakParticles(pos, targetBlockState);

            Entity movedEntity = canWalljumpWithMount ? player.getVehicle() : player;

            float walljumpStrength = processWallJumpPhysics(player, movedEntity);

            world.playSound(player, pos, KlaxonSoundEvents.ITEM_HAMMER_WALLJUMP_SUCCESS, SoundCategory.PLAYERS, 2 * attackCooldownProgress, 2f * attackCooldownProgress);

            if (player instanceof ServerPlayerEntity serverPlayer) {
                // update observers monitoring target block - doesn't work in adventure
                if (PermissionsHelper.canModifyWorld(player) && shouldUpdateObservers) {
                    updateAdjacentMonitoringObservers(world, pos, targetBlockState);
                }

                // proc normal walljump advancement
                KlaxonAdvancementTriggers.triggerWalljumpAbility(serverPlayer, HammerItem.UsageType.NORMAL_WALLJUMP);
                // proc boosted walljump advancement
                if (walljumpStrength > 1f) {
                    KlaxonAdvancementTriggers.triggerWalljumpAbility(serverPlayer, HammerItem.UsageType.BOOSTED_WALLJUMP);
                }
                // proc minecart walljump advancement
                // this intentionally doesn't use the walljumpable entity tag because it's a specific easter egg to minecarts
                if (movedEntity != null && movedEntity.getType().isIn(ConventionalEntityTypeTags.MINECARTS)) {
                    KlaxonAdvancementTriggers.triggerWalljumpAbility(serverPlayer, HammerItem.UsageType.MINECART_WALLJUMP);
                }
            }

            // trip sculk sensors
            world.emitGameEvent(player, GameEvent.HIT_GROUND, pos);

            // player shenanigans
            player.onLanding();
            player.resetLastAttackedTicks();

            // damage both main hand walljump stack and offhand one if present
            walljumpStack.damage(1, player, EquipmentSlot.MAINHAND);
            if (get(player.getOffHandStack()) != null) player.getOffHandStack().damage(1, player, EquipmentSlot.OFFHAND);
        }
    }

    private float calculateWalljumpStrength(PlayerEntity sourcePlayer, Entity launchedEntity) {
        float walljumpStrength = 1f;

        walljumpStrength *= sourcePlayer.getAttackCooldownProgress(0.5f);
        walljumpStrength *= AbilityModifierCalculator.calculateHammerWalljumpMultiplier(sourcePlayer, launchedEntity);

        float totalMultiplier = this.velocityMultiplier;

        WalljumpAbilityComponent offhand = get(sourcePlayer.getOffHandStack());

        // if we've got a walljump component in offhand, add its velocity multiplier to base - divided by 2 so it's not crazy op.
        if (offhand != null) {
            totalMultiplier += offhand.velocityMultiplier / 2;
        }

        // commit total multiplier to walljump strength value
        walljumpStrength *= totalMultiplier;

        return walljumpStrength;
    }

    // yoinked from trident riptide physics - edited to suit my needs
    private float processWallJumpPhysics(PlayerEntity sourcePlayer, Entity launchedEntity) {

        float playerYaw = sourcePlayer.getYaw();
        float playerPitch = sourcePlayer.getPitch();
        float h = MathHelper.sin(playerYaw * 0.017453292F) * MathHelper.cos(playerPitch * 0.017453292F);
        float k = MathHelper.sin(playerPitch * 0.017453292F);
        float l = -MathHelper.cos(playerYaw * 0.017453292F) * MathHelper.cos(playerPitch * 0.017453292F);
        float m = MathHelper.sqrt(h * h + k * k + l * l);
        float walljumpStrength = calculateWalljumpStrength(sourcePlayer, launchedEntity);
        float n = walljumpStrength * 0.6f;

        h *= n / m;
        k *= n / m;
        l *= n / m;

        // this is needed because minecarts are wack and don't want to move horizontally as much
        if (launchedEntity instanceof MinecartEntity) {
            h *= 12;
            k *= 3;
            l *= 12;
        }

        // no-op early - we shouldn't give the player velocity from the server end
        if (launchedEntity.equals(sourcePlayer) && launchedEntity instanceof ServerPlayerEntity) {
            return walljumpStrength;
        }

        // add velocity to the entity
        launchedEntity.addVelocity(h, k, l);

        return walljumpStrength;
    }

    // called in ItemMixin
    // present to prevent you from demolishing your world when walljumping around in creative
    public static boolean allowsMining(PlayerEntity miner) {
        if (miner.isCreative()) {
            // mining is allowed if there's no walljump ability component
            return WalljumpAbilityComponent.get(miner.getWeaponStack()) == null;
        }

        return true;
    }

    public static boolean canWallJump(PlayerEntity player, ItemStack walljumpStack, BlockState state) {
        return canWalljumpWithMount(player, walljumpStack, state) || canStandardWallJump(player, walljumpStack, state);
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

    public static boolean canWalljumpWithMount(PlayerEntity player, ItemStack wallJumpStack, BlockState state) {
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