package net.myriantics.klaxon.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.world.World;
import net.myriantics.klaxon.item.KlaxonItems;
import net.myriantics.klaxon.util.KlaxonDamageTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerEntity.class)
public abstract class PlayerAttackDamageTypeMixin {
    @Shadow public abstract ItemStack getEquippedStack(EquipmentSlot slot);

    @Shadow public abstract Iterable<ItemStack> getHandItems();

    @Shadow public abstract Arm getMainArm();

    // for future reference
    // bl in PlayerEntity tells if attack is fully charged
    // bl2 tells if its a knockback hit
    // bl3 tells if its a crit
    // bl4 tells if its a sweeping
    // bl5 is fire aspect
    // bl6 tells if the attack was successful

    @ModifyExpressionValue(
            method = "attack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/damage/DamageSources;playerAttack(Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/entity/damage/DamageSource;",
            ordinal = 0))
    // ordinal 2 selects boolean #3 (bl3)
    private DamageSource checkCustomAttackType(DamageSource original, @Local(ordinal = 2) boolean willCrit) {
        if (original.getAttacker() instanceof PlayerEntity player && player.getMainHandStack().isOf(KlaxonItems.HAMMER)) {
            if (willCrit) {
                return KlaxonDamageTypes.hammerWalloping(player);
            }
            return KlaxonDamageTypes.hammerBonking(player);
        }
        return original;
    }
}
