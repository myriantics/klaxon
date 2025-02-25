package net.myriantics.klaxon.api;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

public abstract class EntityWeightHelper {

    public static boolean isEntityHeavy(LivingEntity entity) {
        boolean isHeavy = false;

        // check if entity is wearing any heavy itemstacks
        for (ItemStack stack : entity.getAllArmorItems()) {
            if (isStackHeavy(stack)) {
                return true;
            }
        }

        return isHeavy;
    }

    public static boolean isStackHeavy(ItemStack stack) {
        return stack.isIn(KlaxonItemTags.HEAVY_EQUIPMENT);
    }
}
