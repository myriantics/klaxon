package net.myriantics.klaxon.mixin.minecraft.grapple_winch;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.myriantics.klaxon.mechanics.grapple_winch.connection.GrappleWinchConnection;
import net.myriantics.klaxon.mechanics.grapple_winch.manager.GrappleWinchConnectionManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow public abstract boolean isOnGround();

    @Shadow
    public abstract World getWorld();

    @Inject(
            method = "scheduleVelocityUpdate",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    public void klaxon$cancelDamageVelocityIfMidairWithGrappleWinch(CallbackInfo ci) {
        // this is here to fix an issue with players being flung downwards if they get damaged at all when grappling.
        if (!isOnGround() && (Object) this instanceof PlayerEntity player) {
            GrappleWinchConnectionManager manager = GrappleWinchConnectionManager.get(this.getWorld());
            @Nullable GrappleWinchConnection connection = manager.fromPlayer(player);
            if (connection != null) {
                ci.cancel();
            }
        }
    }
}
