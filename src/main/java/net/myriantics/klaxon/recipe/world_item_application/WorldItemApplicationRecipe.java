package net.myriantics.klaxon.recipe.world_item_application;

import net.minecraft.core.HolderLookup;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.myriantics.klaxon.registry.recipe.KlaxonRecipeSerializers;
import net.myriantics.klaxon.registry.recipe.KlaxonRecipeTypes;

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
    public boolean matches(WorldItemApplicationRecipeInput input, Level world) {
        return inputIngredient.test(input.usedStack()) && input.inputState().is(validBlocks);
    }

    @Override
    public ItemStack assemble(WorldItemApplicationRecipeInput input, HolderLookup.Provider lookup) {
        return getResultItem(lookup);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width == 1 && height == 1;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registriesLookup) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return KlaxonRecipeSerializers.WORLD_ITEM_APPLICATION_RECIPE_SERIALIZER.value();
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
