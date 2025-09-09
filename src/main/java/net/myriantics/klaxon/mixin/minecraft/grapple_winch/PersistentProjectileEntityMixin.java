package net.myriantics.klaxon.mixin.minecraft.grapple_winch;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.myriantics.klaxon.entity.GrappleClawEntity;
import net.myriantics.klaxon.registry.entity.KlaxonDataAttachments;
import net.myriantics.klaxon.util.EntityWeightHelper;
import net.myriantics.klaxon.util.grapple_winch.PlayerEntityGrappleAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PersistentProjectileEntity.class)
public abstract class PersistentProjectileEntityMixin extends ProjectileEntity {

    public PersistentProjectileEntityMixin(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @ModifyExpressionValue(
            method = "tick",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;inGround:Z",
                    ordinal = 1
            )
    )
    private boolean klaxon$ignoreInGroundIfRetractingGrappleClaw(boolean original) {
        // original being true indicates being stuck in a block, so only call this if it's true
        if (
                original
                && (Object) this instanceof GrappleClawEntity
                && this.getOwner() instanceof ServerPlayerEntity serverPlayer
                && ((PlayerEntityGrappleAccess) serverPlayer).klaxon$getGrappleClaw().equals(this)
                && ((PlayerEntityGrappleAccess) serverPlayer).klaxon$isRetracting()
        ) {
            // this returning false indicates that the owner exists and is retracting the grapple winch - so we should tick movement like normal
            return original && !EntityWeightHelper.isHeavy(serverPlayer);
        }

        return original;
    }
}
