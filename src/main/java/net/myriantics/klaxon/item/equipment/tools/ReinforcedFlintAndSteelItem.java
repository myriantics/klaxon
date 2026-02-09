package net.myriantics.klaxon.item.equipment.tools;

import net.minecraft.item.FlintAndSteelItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;

public class ReinforcedFlintAndSteelItem extends FlintAndSteelItem {

    private final ToolMaterial material;

    public ReinforcedFlintAndSteelItem(Settings settings, ToolMaterial material) {
        super(settings.maxDamage(material.getDurability()));
        this.material = material;
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return this.material.getRepairIngredient().test(ingredient) || super.canRepair(stack, ingredient);
    }
}
