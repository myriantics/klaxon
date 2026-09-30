package net.myriantics.klaxon.recipe.shredding.industrial;

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
import net.minecraft.world.level.Level;
import net.myriantics.klaxon.recipe.RecipeOutputCompound;
import net.myriantics.klaxon.registry.recipe.KlaxonRecipeSerializers;

public class StandardIndustrialShreddingRecipe implements IndustrialShreddingRecipe {

    private final Ingredient ingredient;
    private final int totalShreddingTime;
    private final RecipeOutputCompound compound;
    private final boolean delegatesShreddingProgressToItemDurability;

    public StandardIndustrialShreddingRecipe(Ingredient ingredient, int totalShreddingTime, RecipeOutputCompound compound) {
        this(ingredient, totalShreddingTime, compound, true);
    }

    public StandardIndustrialShreddingRecipe(Ingredient ingredient, int totalShreddingTime, RecipeOutputCompound compound, boolean delegatesShreddingProgressToItemDurability) {
        this.ingredient = ingredient;
        this.totalShreddingTime = totalShreddingTime;
        this.compound = compound;
        this.delegatesShreddingProgressToItemDurability = delegatesShreddingProgressToItemDurability;
    }

    @Override
    public Ingredient getIngredient() {
        return this.ingredient;
    }

    @Override
    public int getTotalShreddingTime() {
        return this.totalShreddingTime;
    }

    @Override
    public boolean delegatesShreddingTimeToItemDurability() {
        return this.delegatesShreddingProgressToItemDurability;
    }

    @Override
    public boolean matches(IndustrialShreddingRecipeInput input, Level level) {
        return this.ingredient.test(input.inputStack());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return KlaxonRecipeSerializers.INDUSTRIAL_SHREDDING.value();
    }

    @Override
    public ItemStack[] properlyAssemble(IndustrialShreddingRecipeInput input, HolderLookup.Provider registries) {
        return this.compound.computeDrops(input.random());
    }

    @Override
    public ItemStack[] getDisplayStacks(IndustrialShreddingRecipeInput input, HolderLookup.Provider registries) {
        return this.compound.getDisplayStacks();
    }

    public static final class Serializer implements RecipeSerializer<StandardIndustrialShreddingRecipe> {

        public static final MapCodec<StandardIndustrialShreddingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(StandardIndustrialShreddingRecipe::getIngredient),
                Codec.intRange(1, Integer.MAX_VALUE).fieldOf("total_shredding_time").forGetter(StandardIndustrialShreddingRecipe::getTotalShreddingTime),
                RecipeOutputCompound.createCodec(9).fieldOf("recipe_output_compound").forGetter(i -> i.compound),
                Codec.BOOL.optionalFieldOf("delegate_shredding_progress_to_item_durability", true).forGetter(StandardIndustrialShreddingRecipe::delegatesShreddingTimeToItemDurability)
        ).apply(instance, StandardIndustrialShreddingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, StandardIndustrialShreddingRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, StandardIndustrialShreddingRecipe::getIngredient,
                ByteBufCodecs.INT, StandardIndustrialShreddingRecipe::getTotalShreddingTime,
                RecipeOutputCompound.STREAM_CODEC, i -> i.compound,
                ByteBufCodecs.BOOL, StandardIndustrialShreddingRecipe::delegatesShreddingTimeToItemDurability,
                StandardIndustrialShreddingRecipe::new
        );

        @Override
        public MapCodec<StandardIndustrialShreddingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, StandardIndustrialShreddingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
