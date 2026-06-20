package net.myriantics.klaxon.recipe.blast_processing;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public abstract class BlastProcessingTransmutationRecipe implements BlastProcessingRecipe {

    private final Ingredient ingredient;
    private final Bounds bounds;

    public BlastProcessingTransmutationRecipe(Ingredient ingredient, float explosionPowerMin, float explosionPowerMax) {
        this(ingredient, new Bounds(explosionPowerMin, explosionPowerMax));
    }

    public BlastProcessingTransmutationRecipe(Ingredient ingredient, Bounds bounds) {
        this.ingredient = ingredient;
        this.bounds = bounds;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public Bounds getBounds() {
        return this.bounds;
    }

    @Override
    public Ingredient getIngredient() {
        return this.ingredient;
    }

    @Override
    public ItemStack[] getDisplayStacks(BlastProcessingRecipeInput input, HolderLookup.Provider registries) {
        return this.ingredient.getItems().length == 0 ? new ItemStack[0] : new ItemStack[]{this.ingredient.getItems()[0]};
    }

    public interface BlastProcessingTransmutationConstructor<T extends BlastProcessingTransmutationRecipe> {
        T create(Ingredient ingredient, Bounds bounds);
    }

    protected static <T extends BlastProcessingTransmutationRecipe> RecipeSerializer<T> create(BlastProcessingTransmutationConstructor<T> constructor) {
        return new RecipeSerializer<T>() {

            private final MapCodec<T> codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                            Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(BlastProcessingTransmutationRecipe::getIngredient),
                            Bounds.CODEC.fieldOf("bounds").forGetter(BlastProcessingTransmutationRecipe::getBounds)
            ).apply(instance, constructor::create));

            private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec = StreamCodec.composite(
                    Ingredient.CONTENTS_STREAM_CODEC, BlastProcessingTransmutationRecipe::getIngredient,
                    Bounds.STREAM_CODEC, BlastProcessingTransmutationRecipe::getBounds,
                    constructor::create
            );

            @Override
            public MapCodec<T> codec() {
                return codec;
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
                return streamCodec;
            }
        };
    }
}
