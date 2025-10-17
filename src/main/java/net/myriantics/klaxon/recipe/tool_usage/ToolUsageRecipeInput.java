package net.myriantics.klaxon.recipe.tool_usage;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.RegistryKey;

public class ToolUsageRecipeInput implements RecipeInput {
    private final ItemStack toolStack;
    private final ItemStack targetStack;
    private final RegistryKey<ToolUsageRecipeType> type;

    public ToolUsageRecipeInput(ItemStack toolStack, ItemStack targetStack, RegistryKey<ToolUsageRecipeType> type) {
        this.toolStack = toolStack;
        this.targetStack = targetStack;
        this.type = type;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return switch (slot) {
            case 0 -> toolStack;
            case 1 -> targetStack;
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public int getSize() {
        return 2;
    }

    public RegistryKey<ToolUsageRecipeType> getTypeKey() {
        return type;
    }
}
