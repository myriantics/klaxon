package net.myriantics.klaxon.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.myriantics.klaxon.api.behavior.MuffledBlastProcessorFireworkParticle;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin {

    @Shadow @Final private MinecraftClient client;

    @Inject(
            method = "addFireworkParticle",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    public void klaxon$removeDummyExplosionIfPresent(double x, double y, double z, double velocityX, double velocityY, double velocityZ,
                                                     List<FireworkExplosionComponent> explosions, CallbackInfo ci,
                                                     @Local(argsOnly = true) LocalRef<List<FireworkExplosionComponent>> explosionRef) {
        if (hasMuffledDummyExplosion(explosions)) {
            // remove the dummy element from the list if present
            explosions = explosions.subList(0, explosions.size() - 1);

            // if there are still explosions left to process, then run the override
            // protects against network protocol error from trying to summon firework with no explosions
            if (!explosions.isEmpty()) {
                // add particle with my dummy class so it can differentiate via an instanceof check
                client.particleManager.addParticle(new MuffledBlastProcessorFireworkParticle(
                        ((ClientWorld) (Object) this),
                        x, y, z, velocityX, velocityY, velocityZ,
                        client.particleManager, explosions));
                ci.cancel();
            }

            // Set local variable to fixed explosion list
            explosionRef.set(explosions);
        }
    }

    @Unique
    private static boolean hasMuffledDummyExplosion(List<FireworkExplosionComponent> explosions) {
        // to protect against NoSuchElementException
        if (explosions.isEmpty()) {
            return false;
        }

        return explosions.getLast().shape().equals(FireworkExplosionComponent.Type.SMALL_BALL) && explosions.getLast().colors().contains(12345) && explosions.getLast().colors().contains(12345);
    }
}
