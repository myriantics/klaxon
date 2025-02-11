package net.myriantics.klaxon.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.myriantics.klaxon.item.equipment.tools.HammerItem;
import net.myriantics.klaxon.util.KlaxonDamageTypes;
import net.myriantics.klaxon.util.KlaxonTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin {

    // Allows tag-defined entities such as Piglin Brutes and Wither Skeletons to use Walloping damage over Bonking
    @ModifyExpressionValue(
            method = "tryAttack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/damage/DamageSources;mobAttack(Lnet/minecraft/entity/LivingEntity;)Lnet/minecraft/entity/damage/DamageSource;",
                    ordinal = 0))
    private DamageSource checkCustomAttackType(DamageSource original, @Local(ordinal = 0, argsOnly = true) Entity target) {

        // change damage type to reflect hammer damage type
        if(original.getAttacker() instanceof MobEntity attacker && attacker.getMainHandStack().getItem() instanceof HammerItem) {
            if (attacker.getType().isIn(KlaxonTags.Entities.HEAVY_HITTERS)) {
                // if the entity is defined to hit harder, it uses walloping
                return KlaxonDamageTypes.hammerWalloping(attacker);
            }

            // if it's a regular entity, use bonking
            return KlaxonDamageTypes.hammerBonking(attacker);
        }

        return original;
    }
}
