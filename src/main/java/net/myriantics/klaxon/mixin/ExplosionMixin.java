package net.myriantics.klaxon.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import net.myriantics.klaxon.registry.minecraft.KlaxonBlocks;
import net.myriantics.klaxon.util.PermissionsHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
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
    private BlockState klaxon$conversionCatalyst;

    @Inject(
            method = "<init>(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/damage/DamageSource;Lnet/minecraft/world/explosion/ExplosionBehavior;DDDFZLnet/minecraft/world/explosion/Explosion$DestructionType;Lnet/minecraft/particle/ParticleEffect;Lnet/minecraft/particle/ParticleEffect;Lnet/minecraft/registry/entry/RegistryEntry;)V",
            at = @At(value = "TAIL")
    )
    private void klaxon$setOriginState(World world, Entity entity, DamageSource damageSource, ExplosionBehavior behavior, double x, double y, double z, float power, boolean createFire, Explosion.DestructionType destructionType, ParticleEffect particle, ParticleEffect emitterParticle, RegistryEntry<SoundEvent> soundEvent, CallbackInfo ci) {
        this.klaxon$conversionCatalyst = world.getBlockState(BlockPos.ofFloored(x, y, z));
    }

    @WrapOperation(
            method = "collectBlocksAndDamageEntities",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/explosion/ExplosionBehavior;getBlastResistance(Lnet/minecraft/world/explosion/Explosion;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/fluid/FluidState;)Ljava/util/Optional;")
    )
    private Optional<Float> klaxon$ignoreOriginConversionCatalyst(ExplosionBehavior instance, Explosion explosion, BlockView world, BlockPos targetPos, BlockState targetState, FluidState fluidState, Operation<Optional<Float>> original) {
        // this is to prevent high blast resistance catalysts completely bonking the strength of the conversion reaction.
        if (targetState.equals(klaxon$conversionCatalyst) && targetPos.equals(BlockPos.ofFloored(x, y, z))) {
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
        if (world.isClient() || (explosion.getCausingEntity() instanceof PlayerEntity player && PermissionsHelper.canModifyWorld(player))) {
            // we don't want to override anything if it's on the client or the player doesnt have editing perms.
            original.call(instance, world, pos, explosion, biConsumer);
            return;
        }

        // yoink origin state from the explosion
        Optional<BlockState> originState = Optional.ofNullable(this.klaxon$conversionCatalyst);

        // make sure we have a valid conversion catalyst before changing block state.
        if (originState.isPresent() && originState.get().isOf(KlaxonBlocks.HALLNOX_NETHER_REACTOR_CORE)) {
            world.setBlockState(pos, KlaxonBlocks.STEEL_BLOCK.getDefaultState());
        } else {
            // if there's no valid catalyst at the explosion's origin, call the original.
            original.call(instance, world, pos, explosion, biConsumer);
        }
    }
}
