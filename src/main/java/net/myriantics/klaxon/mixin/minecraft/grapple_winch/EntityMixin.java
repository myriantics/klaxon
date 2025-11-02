package net.myriantics.klaxon.mixin.minecraft.grapple_winch;

import net.minecraft.entity.Entity;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.PlayerEntityGrappleAccess;
import net.myriantics.klaxon.mechanics.grapple_winch.AttachedGrappleClawContainer;
import net.myriantics.klaxon.mechanics.grapple_winch.EntityGrappleClawContainerAccess;
import org.jetbrains.annotations.Nullable;
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
        if (!isOnGround() && this instanceof PlayerEntityGrappleAccess access && access.klaxon$hasActiveConnection()) {
            ci.cancel();
        }
    }
}
