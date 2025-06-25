package net.myriantics.klaxon.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import net.myriantics.klaxon.util.ExplosionMixinAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Explosion.class)
public abstract class ExplosionMixin implements ExplosionMixinAccess {

    @Unique
    private BlockState klaxon$originState;

    @Inject(
            method = "<init>(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/damage/DamageSource;Lnet/minecraft/world/explosion/ExplosionBehavior;DDDFZLnet/minecraft/world/explosion/Explosion$DestructionType;Lnet/minecraft/particle/ParticleEffect;Lnet/minecraft/particle/ParticleEffect;Lnet/minecraft/registry/entry/RegistryEntry;)V",
            at = @At(value = "TAIL")
    )
    private void klaxon$setOriginState(World world, Entity entity, DamageSource damageSource, ExplosionBehavior behavior, double x, double y, double z, float power, boolean createFire, Explosion.DestructionType destructionType, ParticleEffect particle, ParticleEffect emitterParticle, RegistryEntry soundEvent, CallbackInfo ci) {
        this.klaxon$originState = world.getBlockState(BlockPos.ofFloored(x, y, z));
    }

    @Unique
    @Override
    public BlockState klaxon$getOriginState() {
        return klaxon$originState;
    }
}
