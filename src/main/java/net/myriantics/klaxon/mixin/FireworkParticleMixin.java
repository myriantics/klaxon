package net.myriantics.klaxon.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.particle.FireworksSparkParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.myriantics.klaxon.api.behavior.MuffledBlastProcessorFireworkParticle;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(targets = "net.minecraft.client.particle.FireworksSparkParticle$FireworkParticle")
public abstract class FireworkParticleMixin {

    // Wrap all calls to playSound with a check for special class instance I add in FireworkRocketEntityMixin
    @WrapOperation(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;playSound(DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZ)V"/*, ordinal = 0*/)
    )
    public void muffledBlastProcessorSoundOverride(ClientWorld instance, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch, boolean useDistance, Operation<Void> original) {
        // if it's a muffled blast processor particle, don't run
        if (((FireworksSparkParticle.FireworkParticle) (Object) this) instanceof MuffledBlastProcessorFireworkParticle) {
            return;
        }
        original.call(instance, x, y, z, sound, category, volume, pitch, useDistance);
    }
}
