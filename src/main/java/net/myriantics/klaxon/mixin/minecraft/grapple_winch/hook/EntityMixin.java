package net.myriantics.klaxon.mixin.minecraft.grapple_winch.hook;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.myriantics.klaxon.mechanics.grapple_winch.GrapplingHook;
import net.myriantics.klaxon.mechanics.grapple_winch.manager.GrappleWinchConnectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow
    public abstract Level level();

    @ModifyReturnValue(
            method = "shouldBeSaved",
            at = @At(value = "RETURN")
    )
    private boolean klaxon$delegateSavingLogicToGrappleWinchConnectionManager(boolean original) {
        // grappling hooks with an active connection shouldn't be saved to the world - their saving and loading is handled in ServerGrappleWinchConnectionManager
        return original && !(
                this instanceof GrapplingHook hook && GrappleWinchConnectionManager.get(this.level()).fromHook(hook) != null
        );
    }
}
