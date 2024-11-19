package net.myriantics.klaxon.mixin.hammer;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.myriantics.klaxon.item.KlaxonItems;
import net.myriantics.klaxon.util.KlaxonDamageTypes;
import net.myriantics.klaxon.util.KlaxonTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin {
    @WrapOperation(
            method = "tryAttack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/MobEntity;disablePlayerShield(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)V")
    )
    private void shieldDisableWrapper(MobEntity instance, PlayerEntity player, ItemStack mobStack, ItemStack playerStack, Operation<Void> original) {
        if (!mobStack.isEmpty() && !playerStack.isEmpty() && mobStack.isIn(KlaxonTags.Items.SHIELD_DISABLING_MELEE_WEAPONS) && playerStack.isOf(Items.SHIELD)) {
            player.getItemCooldownManager().set(playerStack.getItem(), 100);
            player.stopUsingItem();
            instance.getWorld().sendEntityStatus(player, (byte) 30);

        } else {
            original.call(instance, player, mobStack, playerStack);
        }
    }

    @ModifyExpressionValue(
            method = "tryAttack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/damage/DamageSources;mobAttack(Lnet/minecraft/entity/LivingEntity;)Lnet/minecraft/entity/damage/DamageSource;",
                    ordinal = 0))
    private DamageSource checkCustomAttackType(DamageSource original, @Local(ordinal = 0, argsOnly = true) Entity target) {
        if(original.getAttacker() instanceof MobEntity attacker && attacker.getMainHandStack().isOf(KlaxonItems.HAMMER)) {
            if (attacker.getType().isIn(KlaxonTags.Entities.HEAVY_HITTERS)) {
                return KlaxonDamageTypes.hammerWalloping(attacker);
            }

            return KlaxonDamageTypes.hammerBonking(attacker);
        }

        return original;
    }
}
