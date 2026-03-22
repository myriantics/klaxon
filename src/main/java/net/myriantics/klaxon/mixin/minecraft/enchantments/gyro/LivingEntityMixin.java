package net.myriantics.klaxon.mixin.minecraft.enchantments.gyro;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.myriantics.klaxon.registry.item.KlaxonEnchantmentEffectComponentTypes;
import net.myriantics.klaxon.tag.klaxon.KlaxonDamageTypeTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @WrapOperation(
            method = "hurt",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;markHurt()V")
    )
    private void klaxon$cancelVelocityUpdateIfJuggernautEnchantmentIsPresent(LivingEntity instance, Operation<Void> original, @Local(argsOnly = true) DamageSource source) {
        boolean detected = false;
        if (instance instanceof Player player && source.is(KlaxonDamageTypeTags.STREAMLINE_ENCHANTMENT_CANCELS_VELOCITY_UPDATE)) {
            for (ItemStack stack : player.getArmorSlots()) {
                if (EnchantmentHelper.has(stack, KlaxonEnchantmentEffectComponentTypes.CANCEL_CERTAIN_VELOCITY_UPDATES.value())) {
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
