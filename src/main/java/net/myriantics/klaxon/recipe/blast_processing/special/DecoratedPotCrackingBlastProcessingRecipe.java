package net.myriantics.klaxon.recipe.blast_processing.special;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.myriantics.klaxon.recipe.RecipeOutputCompound;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipe;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeInput;
import net.myriantics.klaxon.registry.misc.KlaxonRecipeTypes;

import java.util.Map;

public class DecoratedPotCrackingBlastProcessingRecipe extends BlastProcessingRecipe {

    public DecoratedPotCrackingBlastProcessingRecipe(Ingredient inputA, double explosionPowerMin, double explosionPowerMax, RecipeOutputCompound result) {
        super(inputA, explosionPowerMin, explosionPowerMax, result);
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return KlaxonRecipeTypes.DECORATED_POT_CRACKING_BLAST_PROCESSING_SERIALIZER;
    }

    @Override
    protected ItemStack[] getDrops(BlastProcessingRecipeInput input, HolderLookup.Provider provider, RandomSource randomSource) {
        ItemStack inputStack = input.getIngredientStack();
        if (inputStack.getItem() instanceof BlockItem blockItem && blockItem.getBlock().defaultBlockState().hasProperty(BlockStateProperties.CRACKED)) {
            ItemStack newStack = inputStack.copy();
            newStack.set(DataComponents.BLOCK_STATE, new BlockItemStateProperties(Map.of("cracked", "true")));
            return new ItemStack[] {newStack};
        }
        return super.getDrops(input, provider, randomSource);
    }
}
