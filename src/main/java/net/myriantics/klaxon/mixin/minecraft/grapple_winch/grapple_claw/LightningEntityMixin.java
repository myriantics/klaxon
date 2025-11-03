package net.myriantics.klaxon.mixin.minecraft.grapple_winch.grapple_claw;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.myriantics.klaxon.entity.entities.grapple_claw.GrappleClawEntity;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.PlayerEntityGrappleAccess;
import net.myriantics.klaxon.mechanics.grapple_winch.EntityGrappleClawContainerAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(LightningEntity.class)
public abstract class LightningEntityMixin extends Entity {

    public LightningEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;onStruckByLightning(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/LightningEntity;)V")
    )
    private void klaxon$conductLightningEffectsThroughGrappleCable(
            CallbackInfo ci,
            @Local Entity struckEntity,
            @Local List<Entity> struckEntities
    ) {
        // check to see if we've directly struck a grapple claw
        @Nullable GrappleClawEntity grappleClaw = struckEntity instanceof GrappleClawEntity ? (GrappleClawEntity) struckEntity : null;

        // check to see if we've struck a player wielding a grapple cable
        if (grappleClaw == null && struckEntity instanceof PlayerEntityGrappleAccess access) {
            grappleClaw = access.klaxon$getGrappleClaw();
        }

        // check to see if we've struck an entity hooked by a grapple claw
        if (grappleClaw == null && struckEntity instanceof EntityGrappleClawContainerAccess access) {
            grappleClaw = access.klaxon$get().getOptionalGrappleClaw().orElse(null);
        }

        // if we've found a grapple claw, run its lightning conduction logic
        if (grappleClaw != null) {
            grappleClaw.conductLightningEffects((ServerWorld) this.getWorld(), struckEntities, (LightningEntity) (Object) this);
        }
    }
}
