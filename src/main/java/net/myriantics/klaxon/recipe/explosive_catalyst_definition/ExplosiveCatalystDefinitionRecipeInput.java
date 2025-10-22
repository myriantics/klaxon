package net.myriantics.klaxon.recipe.explosive_catalyst_definition;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;

public record ExplosiveCatalystDefinitionRecipeInput(ItemStack catalystStack) implements RecipeInput {
    public ExplosiveCatalystDefinitionRecipeInput(DeepslateBlastProcessorBlockEntity blastProcessor) {
        this(blastProcessor.getStack(DeepslateBlastProcessorBlockEntity.CATALYST_INDEX));
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return slot == 0 ? catalystStack : ItemStack.EMPTY;
    }

    @Override
    public int getSize() {
        return 1;
    }
}
