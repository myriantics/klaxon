package net.myriantics.klaxon.recipe.nether_reaction;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.World;
import net.myriantics.klaxon.registry.misc.KlaxonRecipeTypes;

public class NetherReactionRecipe implements Recipe<NetherReactionRecipeInput> {
    private final TagKey<Block> validBlockInputs;

    private final Block outputBlock;

    public NetherReactionRecipe(TagKey<Block> validBlockInputs, Block outputBlock) {
        this.validBlockInputs = validBlockInputs;
        this.outputBlock = outputBlock;
    }

    @Override
    public boolean matches(NetherReactionRecipeInput input, World world) {
        return input.inputBlockState().isIn(validBlockInputs);
    }

    @Override
    public ItemStack craft(NetherReactionRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return getResult(lookup);
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
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

    public TagKey<Block> getValidBlockInputs() {
        return validBlockInputs;
    }

    public Block getOutputBlock() {
        return outputBlock;
    }
}
