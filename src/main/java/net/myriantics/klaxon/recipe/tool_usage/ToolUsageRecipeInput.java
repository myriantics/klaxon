package net.myriantics.klaxon.recipe.tool_usage;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public class ToolUsageRecipeInput implements RecipeInput {
    private final ItemStack toolStack;
    private final ItemStack targetStack;
    private final ResourceKey<ToolUsageRecipeType> type;

    public ToolUsageRecipeInput(ItemStack toolStack, ItemStack targetStack, ResourceKey<ToolUsageRecipeType> type) {
        this.toolStack = toolStack;
        this.targetStack = targetStack;
        this.type = type;
    }

    @Override
    public ItemStack getItem(int slot) {
        return switch (slot) {
            case 0 -> toolStack;
            case 1 -> targetStack;
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public int size() {
        return 2;
    }

    public ResourceKey<ToolUsageRecipeType> getTypeKey() {
        return type;
    }
}
