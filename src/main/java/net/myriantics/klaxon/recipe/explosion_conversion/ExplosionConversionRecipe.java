package net.myriantics.klaxon.recipe.explosion_conversion;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.World;
import net.myriantics.klaxon.registry.minecraft.KlaxonRecipeTypes;

public class ExplosionConversionRecipe implements Recipe<ExplosionConversionRecipeInput> {
    private final TagKey<Block> validBlockInputs;
    private final TagKey<Block> validConversionCatalysts;

    private final Block outputBlock;

    public ExplosionConversionRecipe(TagKey<Block> validBlockInputs, TagKey<Block> validConversionCatalysts, Block outputBlock) {
        this.validBlockInputs = validBlockInputs;
        this.validConversionCatalysts = validConversionCatalysts;
        this.outputBlock = outputBlock;
    }

    @Override
    public boolean matches(ExplosionConversionRecipeInput input, World world) {
        return input.catalystState().isIn(validConversionCatalysts) && input.inputBlockState().isIn(validBlockInputs);
    }

    @Override
    public ItemStack craft(ExplosionConversionRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
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
    public RecipeSerializer<ExplosionConversionRecipe> getSerializer() {
        return KlaxonRecipeTypes.EXPLOSION_CONVERSION_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return KlaxonRecipeTypes.EXPLOSION_CONVERSION;
    }

    public TagKey<Block> getValidBlockInputs() {
        return validBlockInputs;
    }

    public TagKey<Block> getValidConversionCatalysts() {
        return validConversionCatalysts;
    }

    public Block getOutputBlock() {
        return outputBlock;
    }
}
