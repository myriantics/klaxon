package net.myriantics.klaxon.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.myriantics.klaxon.registry.KlaxonItems;
import net.myriantics.klaxon.item.equipment.tools.HammerItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    // for future reference
    // bl in PlayerEntity tells if attack is fully charged
    // bl2 tells if its a knockback hit
    // bl3 tells if its a crit
    // bl4 tells if its a sweeping
    // bl5 is fire aspect
    // bl6 tells if the attack was successful

    // this is how it was in 1.20.1 idk if thats how it works now lol
    
    @ModifyVariable(
            method = "attack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getVelocity()Lnet/minecraft/util/math/Vec3d;")
    )
    // ordinal 2 selects boolean #3 (bl3)
    // Switches the player's attacking damage type to Hammer Walloping if they crit with a hammer, otherwise uses Hammer Bonking if they do a regular hit.
    private DamageSource klaxon$attackDamageTypeOverride(DamageSource value, @Local(ordinal = 2) boolean willCrit) {

        if (value.getAttacker() instanceof PlayerEntity player && player.getMainHandStack().isOf(KlaxonItems.STEEL_HAMMER)) {
            value = HammerItem.getDamageType(player, willCrit);
        }

        return value;
    }
}
