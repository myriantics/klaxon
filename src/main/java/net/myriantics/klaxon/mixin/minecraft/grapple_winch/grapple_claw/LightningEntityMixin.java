package net.myriantics.klaxon.mixin.minecraft.grapple_winch.grapple_claw;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.myriantics.klaxon.entity.entities.grapple_claw.GrappleClawEntity;
import net.myriantics.klaxon.mechanics.grapple_winch.connection.ServerGrappleWinchConnection;
import net.myriantics.klaxon.mechanics.grapple_winch.hooking.HookingGrappleClawAccess;
import net.myriantics.klaxon.mechanics.grapple_winch.manager.ServerGrappleWinchConnectionManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(LightningEntity.class)
public abstract class LightningEntityMixin extends Entity {

    @Shadow
    private @Nullable ServerPlayerEntity channeler;

    public LightningEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getOtherEntities(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;)Ljava/util/List;")
    )
    private void klaxon$initAllConductionTargets(
            CallbackInfo ci,
            @Share(namespace = "klaxon", value = "conductionTargets") LocalRef<ArrayList<Entity>> allConductionTargets
    ) {
        if (this.channeler != null) {
            allConductionTargets.set(new ArrayList<>());
        } else {
            allConductionTargets.set(null);
        }
    }

    @Inject(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;onStruckByLightning(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/LightningEntity;)V")
    )
    private void klaxon$conductLightningEffectsThroughGrappleCable(
            CallbackInfo ci,
            @Local Entity struckEntity,
            @Local List<Entity> struckEntities,
            @Share(namespace = "klaxon", value = "conductionTargets") LocalRef<ArrayList<Entity>> allConductionTargets
    ) {

        // check to see if we've directly struck a grapple claw
        @Nullable GrappleClawEntity grappleClaw = struckEntity instanceof GrappleClawEntity ? (GrappleClawEntity) struckEntity : null;

        // check to see if we've struck a player wielding a grapple cable
        if (grappleClaw == null && struckEntity instanceof ServerPlayerEntity serverPlayer) {
            ServerGrappleWinchConnectionManager manager = ((ServerGrappleWinchConnectionManager.Access) this.getWorld()).klaxon$get();
            @Nullable ServerGrappleWinchConnection connection = manager.fromPlayer(serverPlayer);
            if (connection != null && connection.getHook() instanceof GrappleClawEntity claw) {
                grappleClaw = claw;
            }
        }

        // check to see if we've struck an entity hooked by a grapple claw
        if (grappleClaw == null && struckEntity instanceof HookingGrappleClawAccess access) {
            grappleClaw = access.klaxon$get().getOptionalGrappleClaw().orElse(null);
        }

        // if we've found a grapple claw, run its lightning conduction logic
        if (grappleClaw != null) {
            Entity[] conductionTargets = grappleClaw.conductLightningEffects((ServerWorld) this.getWorld(), struckEntities, (LightningEntity) (Object) this);

            if (allConductionTargets.get() != null) {
                for (Entity entity : conductionTargets) {
                    if (entity != null) {
                        allConductionTargets.get().add(entity);
                    }
                }
            }
        }
    }

    @Inject(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/advancement/criterion/ChanneledLightningCriterion;trigger(Lnet/minecraft/server/network/ServerPlayerEntity;Ljava/util/Collection;)V")
    )
    private void klaxon$includeConductionTargetsInAdvancementCalculation(
            CallbackInfo ci,
            @Local List<Entity> victims,
            @Share(namespace = "klaxon", value = "conductionTargets") LocalRef<ArrayList<Entity>> allConductionTargets
    ) {
        if (allConductionTargets.get() != null && !allConductionTargets.get().isEmpty()) {
            victims.addAll(allConductionTargets.get());
        }
    }
}
