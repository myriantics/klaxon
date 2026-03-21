package net.myriantics.klaxon.mixin.minecraft.item_components.walljump_ability;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.myriantics.klaxon.component.ability.WalljumpAbilityComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Item.class)
public abstract class ItemMixin {
    @ModifyReturnValue(
            method = "canAttackBlock",
            at = @At(value = "RETURN")
    )
    public boolean klaxon$walljumpAbilityMiningRestrictionOverride(boolean original, @Local(argsOnly = true) Player miner) {
        // if it restricts mining, say that you can't mine.
        if (!WalljumpAbilityComponent.allowsMining(miner)) return false;
        return original;
    }
}
