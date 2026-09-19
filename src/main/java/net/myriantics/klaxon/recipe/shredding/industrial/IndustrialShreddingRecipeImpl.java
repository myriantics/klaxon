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
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.myriantics.klaxon.recipe.RecipeOutputCompound;
import net.myriantics.klaxon.registry.recipe.KlaxonRecipeSerializers;

public class IndustrialShreddingRecipeImpl implements IndustrialShreddingRecipe{

    private final Ingredient ingredient;
    private final int totalShreddingTime;
    private final RecipeOutputCompound compound;

    public IndustrialShreddingRecipeImpl(Ingredient ingredient, int totalShreddingTime, RecipeOutputCompound compound) {
        this.ingredient = ingredient;
        this.totalShreddingTime = totalShreddingTime;
        this.compound = compound;
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

    public static final class Serializer implements RecipeSerializer<IndustrialShreddingRecipeImpl> {

        public static final MapCodec<IndustrialShreddingRecipeImpl> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(IndustrialShreddingRecipeImpl::getIngredient),
                Codec.intRange(1, Integer.MAX_VALUE).fieldOf("total_shredding_time").forGetter(IndustrialShreddingRecipeImpl::getTotalShreddingTime),
                RecipeOutputCompound.createCodec(9).fieldOf("recipe_output_compound").forGetter(i -> i.compound)
        ).apply(instance, IndustrialShreddingRecipeImpl::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, IndustrialShreddingRecipeImpl> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, IndustrialShreddingRecipeImpl::getIngredient,
                ByteBufCodecs.INT, IndustrialShreddingRecipeImpl::getTotalShreddingTime,
                RecipeOutputCompound.STREAM_CODEC, i -> i.compound,
                IndustrialShreddingRecipeImpl::new
        );

        @Override
        public MapCodec<IndustrialShreddingRecipeImpl> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, IndustrialShreddingRecipeImpl> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
