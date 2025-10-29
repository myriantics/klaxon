package net.myriantics.klaxon.mixin.minecraft.enchantments.gyro;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.registry.item.KlaxonEnchantmentEffectComponentTypes;
import net.myriantics.klaxon.tag.klaxon.KlaxonDamageTypeTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @WrapOperation(
            method = "damage",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;scheduleVelocityUpdate()V")
    )
    private void klaxon$cancelVelocityUpdateIfJuggernautEnchantmentIsPresent(LivingEntity instance, Operation<Void> original, @Local(argsOnly = true) DamageSource source) {
        boolean detected = false;
        if (instance instanceof PlayerEntity player && source.isIn(KlaxonDamageTypeTags.STREAMLINE_ENCHANTMENT_CANCELS_VELOCITY_UPDATE)) {
            for (ItemStack stack : player.getArmorItems()) {
                if (EnchantmentHelper.hasAnyEnchantmentsWith(stack, KlaxonEnchantmentEffectComponentTypes.CANCEL_CERTAIN_VELOCITY_UPDATES)) {
                    detected = true;
                    break;
                }
            }

        }
        if (!detected) {
            original.call(instance);
        }
    }
}
