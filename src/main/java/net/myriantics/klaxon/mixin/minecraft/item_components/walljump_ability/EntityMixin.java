package net.myriantics.klaxon.mixin.minecraft.item_components.walljump_ability;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.myriantics.klaxon.component.ability.WalljumpAbilityComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @WrapOperation(
            method = "causeFallDamage",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;causeFallDamage(FFLnet/minecraft/world/damagesource/DamageSource;)Z")
    )
    private boolean klaxon$walljumpComponentMinecartFallDamageOverride(Entity passenger, float fallDistance, float damageMultiplier, DamageSource damageSource, Operation<Boolean> original) {
        Entity self = (Entity) (Object) this;

        // only skip passenger fall damage application this if its a living entity riding any minecart that's holding an item that lets you walljump
        if (self instanceof AbstractMinecart && passenger instanceof LivingEntity livingPassenger) {
            if (WalljumpAbilityComponent.get(livingPassenger.getMainHandItem()) != null || WalljumpAbilityComponent.get(livingPassenger.getOffhandItem()) != null) return false;
        }

        return original.call(passenger, fallDistance, damageMultiplier, damageSource);
    }
}
