package net.myriantics.klaxon.recipe.nether_reaction;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.myriantics.klaxon.recipe.BlockIngredient;
import net.myriantics.klaxon.registry.misc.KlaxonRecipeTypes;

public class NetherReactionRecipe implements Recipe<NetherReactionRecipeInput> {
    private final BlockIngredient blockIngredient;

    private final Block outputBlock;

    public NetherReactionRecipe(BlockIngredient blockIngredient, Block outputBlock) {
        this.blockIngredient = blockIngredient;
        this.outputBlock = outputBlock;
    }

    @Override
    public boolean matches(NetherReactionRecipeInput input, Level world) {
        return blockIngredient.test(input.inputBlockState());
    }

    @Override
    public ItemStack assemble(NetherReactionRecipeInput input, HolderLookup.Provider lookup) {
        return getResultItem(lookup);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registriesLookup) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<NetherReactionRecipe> getSerializer() {
        return KlaxonRecipeTypes.NETHER_REACTION_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return KlaxonRecipeTypes.NETHER_REACTION;
    }

    public BlockIngredient getBlockIngredient() {
        return blockIngredient;
    }

    public Block getOutputBlock() {
        return outputBlock;
    }
}
