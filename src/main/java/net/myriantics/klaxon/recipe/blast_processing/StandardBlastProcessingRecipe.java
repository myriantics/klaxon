package net.myriantics.klaxon.recipe.blast_processing;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.myriantics.klaxon.recipe.RecipeOutputCompound;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.registry.recipe.KlaxonRecipeSerializers;

public class StandardBlastProcessingRecipe implements BlastProcessingRecipe {
    private final Ingredient ingredientItem;
    private final Bounds bounds;
    private final RecipeOutputCompound recipeOutputCompound;

    public StandardBlastProcessingRecipe(Ingredient inputA, float explosionPowerMin, float explosionPowerMax, RecipeOutputCompound result) {
        this(inputA, new Bounds(explosionPowerMin, explosionPowerMax), result);
    }

    public StandardBlastProcessingRecipe(Ingredient inputA, Bounds bounds, RecipeOutputCompound result) {
        this.ingredientItem = inputA;
        this.bounds = bounds;
        this.recipeOutputCompound = result;
    }

    @Override
    public ItemStack assemble(BlastProcessingRecipeInput input, HolderLookup.Provider lookup) {
        return ItemStack.EMPTY;
    }

    public ItemStack[] properlyAssemble(BlastProcessingRecipeInput input, HolderLookup.Provider lookup) {
        return recipeOutputCompound.computeDrops(input.getRandom());
    }

    @Override
    public Ingredient getIngredient() {
        return this.ingredientItem;
    }

    public Ingredient getIngredientItem() {
        return ingredientItem;
    }

    @Override
    public Bounds getBounds() {
        return this.bounds;
    }

    public RecipeOutputCompound getRecipeOutputCompound() {
        return recipeOutputCompound;
    }

    @Override
    public ItemStack[] getDisplayStacks() {
        return this.recipeOutputCompound.getDisplayStacks();
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(KlaxonItems.STEEL_BLAST_PROCESSOR);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return KlaxonRecipeSerializers.BLAST_PROCESSING_RECIPE_SERIALIZER.value();
    }

    public static class Serializer implements RecipeSerializer<StandardBlastProcessingRecipe> {
        public static final MapCodec<StandardBlastProcessingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(StandardBlastProcessingRecipe::getIngredient),
                Bounds.CODEC.fieldOf("bounds").forGetter(StandardBlastProcessingRecipe::getBounds),
                RecipeOutputCompound.createCodec(9).fieldOf("recipe_output_compound").forGetter(StandardBlastProcessingRecipe::getRecipeOutputCompound)
                ).apply(instance, StandardBlastProcessingRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, StandardBlastProcessingRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, StandardBlastProcessingRecipe::getIngredient,
                Bounds.STREAM_CODEC, StandardBlastProcessingRecipe::getBounds,
                RecipeOutputCompound.STREAM_CODEC, StandardBlastProcessingRecipe::getRecipeOutputCompound,
                StandardBlastProcessingRecipe::new
        );

        @Override
        public MapCodec<StandardBlastProcessingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, StandardBlastProcessingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
