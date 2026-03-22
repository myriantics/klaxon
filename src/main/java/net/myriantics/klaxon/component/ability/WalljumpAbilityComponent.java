package net.myriantics.klaxon.component.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalEntityTypeTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ObserverBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.myriantics.klaxon.item.equipment.tools.HammerItem;
import net.myriantics.klaxon.mixin.minecraft.item_components.walljump_ability.ObserverBlockInvoker;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementTriggers;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import net.myriantics.klaxon.registry.misc.KlaxonSoundEvents;
import net.myriantics.klaxon.tag.klaxon.KlaxonEntityTypeTags;
import net.myriantics.klaxon.util.AbilityModifierCalculator;
import net.myriantics.klaxon.util.PermissionsHelper;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.level.block.DirectionalBlock.FACING;

// When present on an item, allows it to be used to perform a walljump by attacking the ground with positive Y velocity
public record WalljumpAbilityComponent(float velocityMultiplier, boolean shouldUpdateObservers) {

    public static final Codec<WalljumpAbilityComponent> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                Codec.FLOAT.fieldOf("velocity_multiplier").forGetter(WalljumpAbilityComponent::velocityMultiplier),
                Codec.BOOL.fieldOf("should_update_observers").forGetter(WalljumpAbilityComponent::shouldUpdateObservers)
        ).apply(instance, WalljumpAbilityComponent::new);
    });

    public static final StreamCodec<ByteBuf, WalljumpAbilityComponent> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, WalljumpAbilityComponent::velocityMultiplier,
            ByteBufCodecs.BOOL, WalljumpAbilityComponent::shouldUpdateObservers,
            WalljumpAbilityComponent::new
    );

    public static @Nullable WalljumpAbilityComponent get(ItemStack stack) {
        return stack.getComponents().get(KlaxonDataComponentTypes.WALLJUMP_ABILITY.value());
    }

    public void set(ItemStack stack) {
        stack.applyComponents(DataComponentMap.builder().set(KlaxonDataComponentTypes.WALLJUMP_ABILITY.value(), this).build());
    }

    // rising edge block hit - uses MinecraftClientMixin and HammerWalljumpTriggerPacket
    // called on both client and server
    public void processHammerWalljump(Player player, Level world, BlockPos pos, Direction direction) {
        BlockState targetBlockState = world.getBlockState(pos);

        // validate this to make sure a random block pos was not passed in
        if (player == null || player.getEyePosition().distanceTo(pos.getCenter()) > player.blockInteractionRange() * 2) {
            return;
        }

        ItemStack walljumpStack = player.getMainHandItem();

        float attackCooldownProgress = player.getAttackStrengthScale(0.5f);

        boolean canWalljumpWithMount = canWalljumpWithMount(player, walljumpStack, targetBlockState);

        // validate that player has sufficient attack cooldown and satisfies conditions for walljump
        if (attackCooldownProgress > 0.7 && (!world.isClientSide() || canWalljumpWithMount || canStandardWallJump(player, walljumpStack, targetBlockState))) {
            world.addDestroyBlockEffect(pos, targetBlockState);

            Entity movedEntity = canWalljumpWithMount ? player.getVehicle() : player;

            float walljumpStrength = processWallJumpPhysics(player, movedEntity);

            world.playSound(player, pos, KlaxonSoundEvents.ITEM_HAMMER_WALLJUMP_SUCCESS, SoundSource.PLAYERS, 2 * attackCooldownProgress, 2f * attackCooldownProgress);

            if (player instanceof ServerPlayer serverPlayer) {
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
                if (movedEntity != null && movedEntity.getType().is(ConventionalEntityTypeTags.MINECARTS)) {
                    KlaxonAdvancementTriggers.triggerWalljumpAbility(serverPlayer, HammerItem.UsageType.MINECART_WALLJUMP);
                }
            }

            // trip sculk sensors
            world.gameEvent(player, GameEvent.HIT_GROUND, pos);

            // player shenanigans
            player.resetFallDistance();
            player.resetAttackStrengthTicker();

            // damage main hand walljumping stack
            walljumpStack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
        }
    }

    private float calculateWalljumpStrength(Player sourcePlayer, Entity launchedEntity) {
        float walljumpStrength = 1f;

        walljumpStrength *= sourcePlayer.getAttackStrengthScale(0.5f);
        walljumpStrength *= AbilityModifierCalculator.calculateHammerWalljumpMultiplier(sourcePlayer, launchedEntity);

        // commit total multiplier to walljump strength value
        walljumpStrength *= this.velocityMultiplier;

        return walljumpStrength;
    }

    // yoinked from trident riptide physics - edited to suit my needs
    private float processWallJumpPhysics(Player sourcePlayer, Entity launchedEntity) {

        float playerYaw = sourcePlayer.getYRot();
        float playerPitch = sourcePlayer.getXRot();
        float h = Mth.sin(playerYaw * 0.017453292F) * Mth.cos(playerPitch * 0.017453292F);
        float k = Mth.sin(playerPitch * 0.017453292F);
        float l = -Mth.cos(playerYaw * 0.017453292F) * Mth.cos(playerPitch * 0.017453292F);
        float m = Mth.sqrt(h * h + k * k + l * l);
        float walljumpStrength = calculateWalljumpStrength(sourcePlayer, launchedEntity);
        float n = walljumpStrength * 0.6f;

        h *= n / m;
        k *= n / m;
        l *= n / m;

        // this is needed because minecarts are wack and don't want to move horizontally as much
        if (launchedEntity instanceof Minecart) {
            h *= 12;
            k *= 3;
            l *= 12;
        }

        // no-op early - we shouldn't give the player velocity from the server end
        if (launchedEntity.equals(sourcePlayer) && launchedEntity instanceof ServerPlayer) {
            return walljumpStrength;
        }

        // add velocity to the entity
        launchedEntity.push(h, k, l);

        return walljumpStrength;
    }

    // called in ItemMixin
    // present to prevent you from demolishing your world when walljumping around in creative
    public static boolean allowsMining(Player miner) {
        if (miner.isCreative()) {
            // mining is allowed if there's no walljump ability component
            return WalljumpAbilityComponent.get(miner.getWeaponItem()) == null;
        }

        return true;
    }

    public static boolean canWallJump(Player player, ItemStack walljumpStack, BlockState state) {
        return canWalljumpWithMount(player, walljumpStack, state) || canStandardWallJump(player, walljumpStack, state);
    }

    public static boolean canStandardWallJump(Player player, ItemStack wallJumpStack, BlockState state) {
        // originally you could use the hammer in spectator - funny, but not good.
        return !player.isSpectator()
                // prevents spammy bs when descending and unintentional hammer walljump procs
                && player.getDeltaMovement().y() > 0
                // make sure they're actually holding a walljumpable item
                && get(wallJumpStack) != null
                // allows players to not walljump if they don't want to
                && !player.isShiftKeyDown()
                // you cant walljump when you're in a boat or on a horse
                && player.getVehicle() == null
                // walljumping in water is janky
                && !player.isInLiquid()
                // you can't walljump off of instabreakable blocks - in creative you can tho - also in adventure
                && (state.getDestroyProgress(player, null, null) < 1 || player.isCreative() || !player.getAbilities().mayBuild);
    }

    public static boolean canWalljumpWithMount(Player player, ItemStack wallJumpStack, BlockState state) {
        // make sure there is a vehicle
        return player.getVehicle() != null
                // make sure vehicle is suitable for walljump
                && player.getVehicle().getType().is(KlaxonEntityTypeTags.WALLJUMP_MOVABLE_ENTITIES)
                // make sure you can actually walljump
                && get(wallJumpStack) != null
                // still can't walljump in spectator
                && !player.isSpectator()
                // block still has to be suitable
                && (state.getDestroyProgress(player, null, null) < 1 || player.isCreative() || !player.getAbilities().mayBuild);
    }

    private void updateAdjacentMonitoringObservers(Level world, BlockPos interactionPos, BlockState interactionState) {
        // block updating abilities
        // this quite literally allows you to hit something with a hammer to fix it
        world.neighborChanged(interactionPos, interactionState.getBlock(), interactionPos);

        // trigger observers next to target block because its really funny
        for (Direction side : Direction.values()) {
            BlockPos observerPos = interactionPos.relative(side);
            BlockState observerState = world.getBlockState(observerPos);
            if (observerState.getBlock() instanceof ObserverBlock observerBlock) {
                if (observerPos.relative(observerState.getValue(FACING)).equals(interactionPos)) {
                    ((ObserverBlockInvoker) observerBlock).invokeScheduledTick(observerState, (ServerLevel) world, observerPos, world.getRandom());
                }
            }
        }
    }
}