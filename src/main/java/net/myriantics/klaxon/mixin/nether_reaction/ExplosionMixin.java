package net.myriantics.klaxon.mixin.nether_reaction;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.entity.BrushableBlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import net.myriantics.klaxon.recipe.nether_reaction.NetherReactionRecipeLogic;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementTriggers;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.function.BiConsumer;

@Mixin(Explosion.class)
public abstract class ExplosionMixin {

    @Shadow @Final private double x;
    @Shadow @Final private double y;
    @Shadow @Final private double z;
    @Unique
    private boolean klaxon$isNetherReactionExplosion = false;

    @Inject(
            method = "<init>(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/damage/DamageSource;Lnet/minecraft/world/explosion/ExplosionBehavior;DDDFZLnet/minecraft/world/explosion/Explosion$DestructionType;Lnet/minecraft/particle/ParticleEffect;Lnet/minecraft/particle/ParticleEffect;Lnet/minecraft/registry/entry/RegistryEntry;)V",
            at = @At(value = "TAIL")
    )
    private void klaxon$setOriginState(World world, Entity entity, DamageSource damageSource, ExplosionBehavior behavior, double x, double y, double z, float power, boolean createFire, Explosion.DestructionType destructionType, ParticleEffect particle, ParticleEffect emitterParticle, RegistryEntry<SoundEvent> soundEvent, CallbackInfo ci) {
        BlockState explosionOriginBlockState = world.getBlockState(BlockPos.ofFloored(x, y, z));

        // if the blockstate at the explosion's origin is a valid conversion catalyst, update the stored variable to say so.
        if (NetherReactionRecipeLogic.test(explosionOriginBlockState)) {
            this.klaxon$isNetherReactionExplosion = true;

            // proc block activation advancement
            for (ServerPlayerEntity serverPlayerEntity : world.getNonSpectatingEntities(ServerPlayerEntity.class, Box.of(new Vec3d(x, y, z), 24, 24, 24))) {
                KlaxonAdvancementTriggers.triggerBlockActivation(serverPlayerEntity, explosionOriginBlockState);
            }
        }
    }

    @WrapOperation(
            method = "collectBlocksAndDamageEntities",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/explosion/ExplosionBehavior;getBlastResistance(Lnet/minecraft/world/explosion/Explosion;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/fluid/FluidState;)Ljava/util/Optional;")
    )
    private Optional<Float> klaxon$ignoreOriginConversionCatalyst(ExplosionBehavior instance, Explosion explosion, BlockView world, BlockPos targetPos, BlockState targetState, FluidState fluidState, Operation<Optional<Float>> original) {
        // this is to prevent high blast resistance catalysts completely bonking the strength of the conversion reaction.
        if (klaxon$isNetherReactionExplosion && targetPos.equals(BlockPos.ofFloored(x, y, z))) {
            return Optional.of(0.0f);
        }

        // if the target state is anything but the origin conversion catalyst, return the original value.
        return original.call(instance, explosion, world, targetPos, targetState, fluidState);
    }

    @WrapOperation(
            method = "affectWorld",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;onExploded(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/explosion/Explosion;Ljava/util/function/BiConsumer;)V")
    )
    private void klaxon$hijackBlockDestruction(BlockState instance, World world, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> biConsumer, Operation<Void> original) {

        if (klaxon$isNetherReactionExplosion) {
            // make sure block entity is either absent or valid, and that blockstate can be moved
            if ((world.getBlockEntity(pos) == null || world.getBlockEntity(pos) instanceof BrushableBlockEntity) && !instance.isIn(ConventionalBlockTags.RELOCATION_NOT_SUPPORTED)) {
                // override for door blocks because i cant think of a better way to handle them
                if (instance.getBlock() instanceof DoorBlock && instance.get(DoorBlock.HALF).equals(DoubleBlockHalf.UPPER)) {
                    pos = pos.down();
                    instance = world.getBlockState(pos);
                }

                if (world instanceof ServerWorld serverWorld) {
                    BlockState newState = NetherReactionRecipeLogic.getOutputState(instance, pos, serverWorld, explosion);
                    // make sure we've changed something before setting the blockstate
                    if (!instance.equals(newState)) serverWorld.setBlockState(pos, newState);
                }
            }

            // we did our own custom processing, no need to call original
            return;
        }

        // if anything failed, call the original method
        original.call(instance, world, pos, explosion, biConsumer);
    }
}
