package net.myriantics.klaxon.mixin.minecraft.grapple_winch;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.myriantics.klaxon.mechanics.grapple_winch.GrapplingHook;
import net.myriantics.klaxon.mechanics.grapple_winch.connection.ServerGrappleWinchConnection;
import net.myriantics.klaxon.mechanics.grapple_winch.manager.GrappleWinchConnectionManager;
import net.myriantics.klaxon.mixin.minecraft.grapple_winch.grapple_claw.EnderDragonEntityAccessor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements PlayerEntityGrappleAccess {

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @WrapOperation(
            method = "attack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z")
    )
    private boolean klaxon$tryFastReloadWhenHittingEntity(Entity instance, DamageSource source, float amount, Operation<Boolean> original) {
        @Nullable GrappleWinchConnectionManager manager = ((GrappleWinchConnectionManager.Access) this.getWorld()).klaxon$get();
        assert manager != null;

        if (manager.fromPlayer((PlayerEntity) (Object) this) instanceof ServerGrappleWinchConnection connection) {
            @Nullable GrapplingHook hook = connection.getHook();
            @Nullable ItemStack weaponStack = source.getWeaponStack();

            Entity attackedEntity = instance instanceof EnderDragonPart part
                    ? ((EnderDragonEntityAccessor) part.owner).getBody()
                    : instance;

            // try to fast reload the grapple claw attached to the entity if it's attached
            if (source.isDirect() && hook != null && weaponStack != null && attackedEntity.equals(hook.klaxon$getHookedEntity())) {
                if (hook.klaxon$tryFastReload((PlayerEntity) (Object) this, weaponStack)) {
                    return false;
                }
            }
        }

        return original.call(instance, source, amount);
    }
}
