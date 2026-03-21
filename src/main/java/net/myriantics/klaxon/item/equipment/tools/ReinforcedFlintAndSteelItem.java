package net.myriantics.klaxon.item.equipment.tools;

import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;

public class ReinforcedFlintAndSteelItem extends FlintAndSteelItem {

    private final Tier material;

    public ReinforcedFlintAndSteelItem(Properties settings, Tier material) {
        super(settings.durability(material.getUses()));
        this.material = material;
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack ingredient) {
        return this.material.getRepairIngredient().test(ingredient) || super.isValidRepairItem(stack, ingredient);
    }
}
