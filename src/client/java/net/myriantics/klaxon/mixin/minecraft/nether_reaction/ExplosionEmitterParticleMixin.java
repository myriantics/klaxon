package net.myriantics.klaxon.mixin.minecraft.nether_reaction;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.HugeExplosionSeedParticle;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.core.particles.ParticleOptions;
import net.myriantics.klaxon.particle.NetherReactionExplosionEmitterParticle;
import net.myriantics.klaxon.registry.misc.KlaxonParticleTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HugeExplosionSeedParticle.class)
public abstract class ExplosionEmitterParticleMixin extends NoRenderParticle {
    protected ExplosionEmitterParticleMixin(ClientLevel clientWorld, double d, double e, double f) {
        super(clientWorld, d, e, f);
    }

    @WrapOperation(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V")
    )
    private void klaxon$overrideParticleIfNeeded(ClientLevel instance, ParticleOptions parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ, Operation<Void> original) {
        original.call(
                instance,
                ((Object) this) instanceof NetherReactionExplosionEmitterParticle ? KlaxonParticleTypes.NETHER_REACTION_EXPLOSION : parameters,
                x, y, z,
                velocityX, velocityY, velocityZ
        );
    }
}
