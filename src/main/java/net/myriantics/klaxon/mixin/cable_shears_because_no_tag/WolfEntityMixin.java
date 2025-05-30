package net.myriantics.klaxon.mixin.cable_shears_because_no_tag;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.item.ItemStack;
import net.myriantics.klaxon.item.equipment.tools.CableShearsItem;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WolfEntity.class)
public abstract class WolfEntityMixin {
    @ModifyExpressionValue(
            method = "interactMob",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z")
    )
    public boolean klaxon$cableShearsOverride(boolean original, @Local ItemStack usedStack) {
        return original || usedStack.isIn(KlaxonItemTags.CABLE_SHEARS);
    }
}
