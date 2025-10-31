package net.myriantics.klaxon.recipe.world_item_application;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.World;
import net.myriantics.klaxon.registry.misc.KlaxonRecipeTypes;

public class WorldItemApplicationRecipe implements Recipe<WorldItemApplicationRecipeInput> {
    private final Ingredient inputIngredient;
    private final TagKey<Block> validBlocks;
    private final Block outputBlock;

    public WorldItemApplicationRecipe(TagKey<Block> validBlocks, Ingredient inputIngredient, Block outputBlock) {
        this.inputIngredient = inputIngredient;
        this.validBlocks = validBlocks;
        this.outputBlock = outputBlock;
    }

    @Override
    public boolean matches(WorldItemApplicationRecipeInput input, World world) {
        return inputIngredient.test(input.usedStack()) && input.inputState().isIn(validBlocks);
    }

    @Override
    public ItemStack craft(WorldItemApplicationRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return getResult(lookup);
    }

    @Override
    public boolean fits(int width, int height) {
        return width == 1 && height == 1;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return KlaxonRecipeTypes.WORLD_ITEM_APPLICATION_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return KlaxonRecipeTypes.WORLD_ITEM_APPLICATION;
    }

    public Ingredient getInputIngredient() {
        return inputIngredient;
    }

    public TagKey<Block> getValidBlockInputs() {
        return validBlocks;
    }

    public Block getOutputBlock() {
        return outputBlock;
    }
}
