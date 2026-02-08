package net.myriantics.klaxon.mixin.minecraft.grapple_winch;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
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
public abstract class PlayerEntityMixin extends LivingEntity {

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @WrapOperation(
            method = "attack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;handleAttack(Lnet/minecraft/entity/Entity;)Z")
    )
    private boolean klaxon$tryFastReloadWhenHittingEntity(Entity instance, Entity attacker, Operation<Boolean> original) {
        @Nullable GrappleWinchConnectionManager manager = GrappleWinchConnectionManager.get(this.getWorld());

        if (manager.fromPlayer((PlayerEntity) (Object) this) instanceof ServerGrappleWinchConnection connection) {
            @Nullable GrapplingHook hook = connection.getHook();
            @Nullable ItemStack weaponStack = attacker.getWeaponStack();

            Entity attackedEntity = instance instanceof EnderDragonPart part
                    ? ((EnderDragonEntityAccessor) part.owner).getBody()
                    : instance;

            // try to fast reload the grapple claw attached to the entity if it's attached
            if (hook != null && weaponStack != null && attackedEntity.equals(hook.klaxon$getHookedEntity())) {
                if (hook.klaxon$tryFastReload((PlayerEntity) (Object) this, weaponStack)) {
                    return true;
                }
            }
        }

        return original.call(instance, attacker);
    }
}
