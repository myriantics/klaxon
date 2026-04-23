package net.myriantics.klaxon.recipe.explosive_catalyst_definition;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;

public record ExplosiveCatalystDefinitionRecipeInput(ItemStack catalystStack) implements RecipeInput {
    public ExplosiveCatalystDefinitionRecipeInput(DeepslateBlastProcessorBlockEntity blastProcessor) {
        this(blastProcessor.getCatalystStack());
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot == 0 ? catalystStack : ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return 1;
    }
}
