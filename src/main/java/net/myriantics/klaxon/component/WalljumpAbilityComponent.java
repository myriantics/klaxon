package net.myriantics.klaxon.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.BlockState;
import net.minecraft.block.ObserverBlock;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
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
import net.myriantics.klaxon.util.AbilityModifierCalculator;
import net.myriantics.klaxon.util.EntityWeightHelper;
import net.myriantics.klaxon.util.PermissionsHelper;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.block.FacingBlock.FACING;

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

    public static @Nullable WalljumpAbilityComponent read(ItemStack stack) {
        return stack.getComponents().get(KlaxonDataComponentTypes.WALLJUMP_ABILITY);
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

        if (canWallJump(player, walljumpStack, targetBlockState) && attackCooldownProgress > 0.8) {

            // may remove, idk thought itd be a good tradeoff for the power of the hammer
            // keeps wind charges relevant and encourages going up
            // may need to decrease attack cooldown or at least its impact on walljump power scaling
            // if it's not an instabreak, process fall damage (instabreak = bbdelta < 1)
            boolean processFallDamage = targetBlockState.calcBlockBreakingDelta(player, null, null) < 1;

            world.addBlockBreakParticles(pos, targetBlockState);
            boolean walljumpSucceeded = processWallJumpPhysics(player, processFallDamage, velocityMultiplier);

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
    private static boolean processWallJumpPhysics(PlayerEntity player, boolean fallDamage, float multiplier) {
        float playerYaw = player.getYaw();
        float playerPitch = player.getPitch();
        float h = MathHelper.sin(playerYaw * 0.017453292F) * MathHelper.cos(playerPitch * 0.017453292F);
        float k = MathHelper.sin(playerPitch * 0.017453292F);
        float l = -MathHelper.cos(playerYaw * 0.017453292F) * MathHelper.cos(playerPitch * 0.017453292F);
        float m = MathHelper.sqrt(h * h + k * k + l * l);
        float n = 0.6F * player.getAttackCooldownProgress(0.5f) * AbilityModifierCalculator.calculateHammerWalljumpMultiplier(player) * multiplier;
        h *= n / m;
        k *= n / m;
        l *= n / m;

        if (fallDamage) {
            player.handleFallDamage(player.fallDistance, 1, player.getDamageSources().fall());
        }

        player.addVelocity(h, k, l);

        // returns false if failed, true if succeeded in moving player
        return n > 0;
    }

    public static boolean canWallJump(PlayerEntity player, ItemStack wallJumpStack, BlockState state) {
        // originally you could use the hammer in spectator - funny, but not good.
        return !player.isSpectator()
                // prevents spammy bs when descending and unintentional hammer walljump procs
                && player.getVelocity().getY() > 0
                // make sure they're actually holding a walljumpable item
                && wallJumpStack.getComponents().contains(KlaxonDataComponentTypes.WALLJUMP_ABILITY)
                // allows players to not walljump if they don't want to
                && !player.isSneaking()
                // you cant walljump when you're in a boat or on a horse
                && player.getVehicle() == null
                // walljumping in water is janky
                && !player.isInFluid()
                // you can't walljump off of instabreakable blocks - in creative you can tho - also in adventure
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