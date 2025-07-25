package net.myriantics.klaxon.mixin.item_components.walljump_ability;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.myriantics.klaxon.component.ability.WalljumpAbilityComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @WrapOperation(
            method = "handleFallDamage",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;handleFallDamage(FFLnet/minecraft/entity/damage/DamageSource;)Z")
    )
    private boolean klaxon$walljumpComponentMinecartFallDamageOverride(Entity passenger, float fallDistance, float damageMultiplier, DamageSource damageSource, Operation<Boolean> original) {
        Entity self = (Entity) (Object) this;

        // only skip passenger fall damage application this if its a living entity riding any minecart that's holding an item that lets you walljump
        if (self instanceof AbstractMinecartEntity && passenger instanceof LivingEntity livingPassenger) {
            if (WalljumpAbilityComponent.get(livingPassenger.getMainHandStack()) != null || WalljumpAbilityComponent.get(livingPassenger.getOffHandStack()) != null) return false;
        }

        return original.call(passenger, fallDistance, damageMultiplier, damageSource);
    }
}
