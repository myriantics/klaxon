package net.myriantics.klaxon.recipe.item_explosion_power;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;
import net.myriantics.klaxon.block.customblocks.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;

public record ExplosiveCatalystRecipeInput(ItemStack catalystStack) implements RecipeInput {
    public ExplosiveCatalystRecipeInput(DeepslateBlastProcessorBlockEntity blastProcessor) {
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
