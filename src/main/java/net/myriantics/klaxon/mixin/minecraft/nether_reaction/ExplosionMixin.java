package net.myriantics.klaxon.mixin.minecraft.nether_reaction;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.myriantics.klaxon.recipe.nether_reaction.NetherReactionConversionListener;
import net.myriantics.klaxon.recipe.nether_reaction.NetherReactionRecipeLogic;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementTriggers;
import net.myriantics.klaxon.registry.misc.KlaxonParticleTypes;
import net.myriantics.klaxon.registry.misc.KlaxonSoundEvents;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockEntityTypeTags;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.BiConsumer;

@Mixin(Explosion.class)
public abstract class ExplosionMixin {

    @Shadow @Final private double x;
    @Shadow @Final private double y;
    @Shadow @Final private double z;

    @Mutable
    @Shadow
    @Final
    private ParticleOptions smallExplosionParticles;

    @Mutable
    @Shadow
    @Final
    private Holder<SoundEvent> explosionSound;

    @Mutable
    @Shadow
    @Final
    private ParticleOptions largeExplosionParticles;
    @Unique
    private boolean klaxon$isNetherReactionExplosion = false;

    @Inject(
            method = "<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;Lnet/minecraft/world/level/ExplosionDamageCalculator;DDDFZLnet/minecraft/world/level/Explosion$BlockInteraction;Lnet/minecraft/core/particles/ParticleOptions;Lnet/minecraft/core/particles/ParticleOptions;Lnet/minecraft/core/Holder;)V",
            at = @At(value = "TAIL")
    )
    private void klaxon$setOriginState(Level world, Entity entity, DamageSource damageSource, ExplosionDamageCalculator behavior, double x, double y, double z, float power, boolean createFire, Explosion.BlockInteraction destructionType, ParticleOptions particle, ParticleOptions emitterParticle, Holder<SoundEvent> soundEvent, CallbackInfo ci) {
        BlockState explosionOriginBlockState = world.getBlockState(BlockPos.containing(x, y, z));

        // if the blockstate at the explosion's origin is a valid conversion catalyst, update the stored variable to say so.
        if (NetherReactionRecipeLogic.test(explosionOriginBlockState)) {
            this.klaxon$isNetherReactionExplosion = true;

            // proc block activation advancement
            for (ServerPlayer serverPlayerEntity : world.getEntitiesOfClass(ServerPlayer.class, AABB.ofSize(new Vec3(x, y, z), 24, 24, 24))) {
                KlaxonAdvancementTriggers.triggerBlockActivation(serverPlayerEntity, explosionOriginBlockState);
            }

            this.smallExplosionParticles = KlaxonParticleTypes.NETHER_REACTION_EXPLOSION.value();
            this.largeExplosionParticles = KlaxonParticleTypes.NETHER_REACTION_EXPLOSION_EMITTER.value();
            this.explosionSound = KlaxonSoundEvents.NETHER_REACTION_EXPLOSION;
        }
    }

    @WrapOperation(
            method = "explode",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/ExplosionDamageCalculator;getBlockExplosionResistance(Lnet/minecraft/world/level/Explosion;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;)Ljava/util/Optional;")
    )
    private Optional<Float> klaxon$ignoreOriginConversionCatalyst(ExplosionDamageCalculator instance, Explosion explosion, BlockGetter world, BlockPos targetPos, BlockState targetState, FluidState fluidState, Operation<Optional<Float>> original) {
        // this is to prevent high blast resistance catalysts completely bonking the strength of the conversion reaction.
        if (klaxon$isNetherReactionExplosion && targetPos.equals(BlockPos.containing(x, y, z))) {
            return Optional.of(0.0f);
        }

        // if the target state is anything but the origin conversion catalyst, return the original value.
        return original.call(instance, explosion, world, targetPos, targetState, fluidState);
    }

    @WrapMethod(
            method = "finalizeExplosion"
    )
    private void klaxon$alwaysEnableParticlesForNetherReactionExplosions(boolean particles, Operation<Void> original) {
        original.call(particles);
    }

    @WrapOperation(
            method = "finalizeExplosion",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;onExplosionHit(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/Explosion;Ljava/util/function/BiConsumer;)V")
    )
    private void klaxon$hijackBlockDestruction(
            BlockState instance,
            Level world,
            BlockPos pos,
            Explosion explosion,
            BiConsumer<ItemStack, BlockPos> biConsumer,
            Operation<Void> original,
            @Share("klaxon$netherReactionOutputs") LocalRef<HashMap<Block, Block>> netherReactionOutputs
    ) {
        // if it's not a nether reaction explosion, call original method and break out of this one
        if (!klaxon$isNetherReactionExplosion) {
            original.call(instance, world, pos, explosion, biConsumer);
            return;
        }

        // if the target state is immune to nether reaction, skip operating on it
        if (instance.is(KlaxonBlockTags.NETHER_REACTION_IMMUNE)) {
            return;
        }

        // don't convert any nether reactor cores aside from the origin one
        if (instance.is(KlaxonBlockTags.NETHER_REACTOR_CORES) && !pos.equals(BlockPos.containing(x, y, z))) {
            return;
        }

        // sign text preserved for reapplication later
        CompoundTag signNbt = null;

        // yonk the blockentity from the target position
        BlockEntity targetBlockEntity = world.getBlockEntity(pos);
        if (targetBlockEntity != null) {

            // if there is a blockentity, make sure it's tagged as overwritable before replacing it
            Holder<BlockEntityType<?>> entry = targetBlockEntity.getType().builtInRegistryHolder();
            if (entry != null && !entry.is(KlaxonBlockEntityTypeTags.NETHER_REACTION_OVERWRITABLE) && !(targetBlockEntity instanceof SignBlockEntity)) {
                // if it's not overwritable, skip further operations
                return;
            }

            // preserve the data from sign block entities
            if (targetBlockEntity instanceof SignBlockEntity) {
                signNbt = targetBlockEntity.saveWithoutMetadata(world.registryAccess());
            }
        }

        // override for door blocks because i cant think of a better way to handle them
        if (instance.getBlock() instanceof DoorBlock && instance.getValue(DoorBlock.HALF).equals(DoubleBlockHalf.UPPER)) {
            pos = pos.below();
            instance = world.getBlockState(pos);
        }

        if (world instanceof ServerLevel serverWorld) {
            BlockState newState = NetherReactionRecipeLogic.getOutputState(instance, pos, serverWorld, explosion);

            // make sure we've changed something before setting the blockstate
            if (!instance.equals(newState)) {
                if (targetBlockEntity instanceof NetherReactionConversionListener listener) {
                    listener.klaxon$beforeConversion(instance, newState);
                }

                serverWorld.setBlockAndUpdate(pos, newState);
                // tick stuff so it doesnt get stuck
                serverWorld.scheduleTick(pos, newState.getBlock(), 1);
            }
        }

        // apply the preserved sign text if present
        if (signNbt != null && world.getBlockEntity(pos) instanceof SignBlockEntity signBlockEntity) {
            signBlockEntity.loadWithComponents(signNbt, world.registryAccess());
        }
    }
}
