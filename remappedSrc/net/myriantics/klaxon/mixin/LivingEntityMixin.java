package net.myriantics.klaxon.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.myriantics.klaxon.util.EntityWeightHelper;
import net.myriantics.klaxon.util.StatusEffectHelper;
import net.myriantics.klaxon.registry.KlaxonDamageTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow public abstract void damageShield(float amount);

    @Shadow protected abstract void takeShieldHit(LivingEntity attacker);

    @Shadow public abstract ItemStack eatFood(World world, ItemStack stack, FoodComponent foodComponent);

    // Allows hammer walloping damage to disable shields and deal damage through them - only activated on crit
    @ModifyExpressionValue(
            method = "damage",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;blockedByShield(Lnet/minecraft/entity/damage/DamageSource;)Z")
    )
    public boolean klaxon$hammerWallopingOverride(boolean original, @Local(argsOnly = true) DamageSource damageSource, @Local(argsOnly = true) float amount) {
        // if its already blocked by shield dont mess with it - walloping passes through this because it's tagged as bypasses_shield
        if (original) return true;

        if (damageSource.isOf(KlaxonDamageTypes.HAMMER_WALLOPING) && damageSource.getSource() instanceof PlayerEntity attacker) {
            damageShield(amount);
            takeShieldHit(attacker);

            // we have to call this independently because the hammer itself doesn't disable shields
            // it has to be walloping damage to pierce through a shield
            if (((Object)this) instanceof PlayerEntity player) {
                player.disableShield();
            }

            // we have our own custom processing, we don't need to run the regular shield disabling stuff
            return false;
        }
        // if it's not walloping
        return false;
    }

    @Inject(
            method = "onEquipStack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;emitGameEvent(Lnet/minecraft/registry/entry/RegistryEntry;)V")
    )
    public void klaxon$updateHeavyEquipmentEffect(EquipmentSlot slot, ItemStack oldStack, ItemStack newStack, CallbackInfo ci) {
        EntityWeightHelper.updateEntityWeightStatusEffect((LivingEntity) (Object) this, slot, newStack);
    }

    @Inject(
            method = "clearStatusEffects",
            at = @At(value = "RETURN", ordinal = 1)
    )
    public void klaxon$recomputeEffectsOverride(CallbackInfoReturnable<Boolean> cir) {
        StatusEffectHelper.recomputePersistentEffects((LivingEntity) (Object) this);
    }
}