package net.myriantics.klaxon.mixin.minecraft.grapple_winch;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.myriantics.klaxon.mechanics.grapple_winch.GrapplingHook;
import net.myriantics.klaxon.mechanics.grapple_winch.connection.ServerGrappleWinchConnection;
import net.myriantics.klaxon.mechanics.grapple_winch.manager.GrappleWinchConnectionManager;
import net.myriantics.klaxon.mixin.minecraft.grapple_winch.grapple_claw.EnderDragonAccessor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @WrapOperation(
            method = "attack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;skipAttackInteraction(Lnet/minecraft/world/entity/Entity;)Z")
    )
    private boolean klaxon$tryFastReloadWhenHittingEntity(Entity instance, Entity attacker, Operation<Boolean> original) {
        @Nullable GrappleWinchConnectionManager manager = GrappleWinchConnectionManager.get(this.level());

        if (manager.fromPlayer((Player) (Object) this) instanceof ServerGrappleWinchConnection connection) {
            @Nullable GrapplingHook hook = connection.getHook();
            @Nullable ItemStack weaponStack = attacker.getWeaponItem();

            Entity attackedEntity = instance instanceof EnderDragonPart part
                    ? ((EnderDragonAccessor) part.parentMob).getBody()
                    : instance;

            // try to fast reload the grapple claw attached to the entity if it's attached
            if (hook != null && weaponStack != null && attackedEntity.equals(hook.klaxon$getHookedEntity())) {
                if (hook.klaxon$tryFastReload((Player) (Object) this, weaponStack)) {
                    return true;
                }
            }
        }

        return original.call(instance, attacker);
    }
}
