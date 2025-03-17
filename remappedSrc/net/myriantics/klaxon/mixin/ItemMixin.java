package net.myriantics.klaxon.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Item.class)
public abstract class ItemMixin {

    @ModifyReturnValue(
            method = "canRepair",
            at =  @At(value = "RETURN")
    )
    public boolean klaxon$flintAndSteelRepairItemOverride(boolean original, @Local(ordinal = 0, argsOnly = true) ItemStack repairedStack, @Local(ordinal = 1, argsOnly = true) ItemStack repairIngredientStack) {
        // allows for flint and steels to be repaired using steel nuggets - if a mod wants to override this its tag based :D
        // top 10 necessary and good features
        return original || (repairedStack.isIn(KlaxonItemTags.STEEL_REPAIRABLE_FLINT_AND_STEEL) && repairIngredientStack.isIn(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_NUGGETS));
    }
}
