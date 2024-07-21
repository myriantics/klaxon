package net.myriantics.klaxon.mixin.hammer;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.myriantics.klaxon.item.KlaxonItems;
import net.myriantics.klaxon.util.KlaxonDamageTypes;
import net.myriantics.klaxon.util.KlaxonTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MobEntity.class)
public abstract class MobEntityAttackDamageTypeMixin {

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
