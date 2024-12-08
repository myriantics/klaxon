package net.myriantics.klaxon.mixin;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(FireworkRocketEntity.class)
public abstract class FireworkRocketEntityMixin {


    @Shadow protected abstract List<FireworkExplosionComponent> getExplosions();

    // Add a dummy explosion component to the list that signifies a muted blast processor explosion
    @Inject(
            method = "handleStatus",
            at = @At(value = "HEAD", target = "Lnet/minecraft/entity/projectile/ProjectileEntity;handleStatus(B)V")
    )
    public void muffledBlastProcessorSoundOverride(byte status, CallbackInfo ci) {
        FireworkRocketEntity fireworkRocket = ((FireworkRocketEntity) (Object) this);
        if (status == 16 && fireworkRocket.getWorld().isClient) {
            Vec3d vec3d = fireworkRocket.getVelocity();

            // Add the dummy explosion
            ArrayList<FireworkExplosionComponent> explosions = new ArrayList<>(getExplosions());
            explosions.add(new FireworkExplosionComponent(FireworkExplosionComponent.Type.SMALL_BALL, IntList.of(12345), IntList.of(12345), false, false));

            // Summon firework particle with the dummy explosion included in the list
            fireworkRocket.getWorld().addFireworkParticle(fireworkRocket.getX(), fireworkRocket.getY(), fireworkRocket.getZ(), vec3d.x, vec3d.y, vec3d.z, List.copyOf(explosions));
        }
    }
}
