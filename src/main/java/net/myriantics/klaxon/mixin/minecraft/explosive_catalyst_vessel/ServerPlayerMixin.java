package net.myriantics.klaxon.mixin.minecraft.explosive_catalyst_vessel;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystVessel;
import net.myriantics.klaxon.tag.klaxon.KlaxonExplosiveCatalystBehaviorTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {
    @Shadow public abstract ServerLevel serverLevel();

    @WrapOperation(
            method = "onExplosionHit",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;setIgnoreFallDamageFromCurrentImpulse(Z)V")
    )
    private void klaxon$checkForExplosiveCatalystVessel(ServerPlayer instance, boolean originalCheck, Operation<Void> original, @Local(argsOnly = true) Entity causingEntity) {
        original.call(instance, originalCheck || (causingEntity instanceof ExplosiveCatalystVessel vessel && vessel.getRawData().behavior(this.serverLevel()).is(KlaxonExplosiveCatalystBehaviorTags.SETS_IGNORE_FALL_DAMAGE_FROM_CURRENT_IMPULSE)));
    }
}
