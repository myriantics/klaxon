package net.myriantics.klaxon.mixin.grapple_winch;

import net.minecraft.entity.Entity;
import net.myriantics.klaxon.util.PlayerEntityGrappleAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow public abstract boolean isOnGround();

    @Inject(
            method = "scheduleVelocityUpdate",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    public void klaxon$cancelDamageVelocityIfMidairWithGrappleWinch(CallbackInfo ci) {
        // this is here to fix an issue with players being flung downwards if they get damaged at all when grappling.
        if (!isOnGround() && this instanceof PlayerEntityGrappleAccess access && (access.klaxon$getGrappleClaw() != null || access.klaxon$getFallbackGrappleClawPos() != null)) {
            ci.cancel();
        }
    }
}
