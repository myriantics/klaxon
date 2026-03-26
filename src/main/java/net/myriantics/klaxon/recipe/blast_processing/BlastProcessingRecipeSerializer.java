package net.myriantics.klaxon.recipe.blast_processing;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.myriantics.klaxon.recipe.RecipeOutputCompound;

public class BlastProcessingRecipeSerializer<T extends BlastProcessingRecipe> implements RecipeSerializer<T> {

    private final MapCodec<T> codec;

    private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

    public BlastProcessingRecipeSerializer(BlastProcessingRecipeInitializer<T> initializer) {
        this.codec = RecordCodecBuilder.mapCodec((recipeInstance -> recipeInstance.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("input_ingredient").forGetter(BlastProcessingRecipe::getIngredientItem),
                PrimitiveCodec.DOUBLE.fieldOf("explosion_power_min").forGetter(BlastProcessingRecipe::getExplosionPowerMin),
                PrimitiveCodec.DOUBLE.fieldOf("explosion_power_max").forGetter(BlastProcessingRecipe::getExplosionPowerMax),
                RecipeOutputCompound.createCodec(9).fieldOf("recipe_output_compound").forGetter(BlastProcessingRecipe::getRecipeOutputCompound)
        ).apply(recipeInstance, initializer::create)));
        this.streamCodec = StreamCodec.of(
                BlastProcessingRecipeSerializer::write, buf -> read(buf, initializer)
        );
    }

    private static <T extends BlastProcessingRecipe> T read(RegistryFriendlyByteBuf buf, BlastProcessingRecipeInitializer<T> initializer) {
        Ingredient ingredientItem = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
        double explosionPowerMin = ByteBufCodecs.DOUBLE.decode(buf);
        double explosionPowerMax = ByteBufCodecs.DOUBLE.decode(buf);
        RecipeOutputCompound outputCompound = RecipeOutputCompound.PACKET_CODEC.decode(buf);

        return initializer.create(ingredientItem, explosionPowerMin, explosionPowerMax, outputCompound);
    }

    private static <T extends BlastProcessingRecipe> void write(RegistryFriendlyByteBuf buf, T recipe) {
        Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.getIngredientItem());
        ByteBufCodecs.DOUBLE.encode(buf, recipe.getExplosionPowerMin());
        ByteBufCodecs.DOUBLE.encode(buf, recipe.getExplosionPowerMax());
        RecipeOutputCompound.PACKET_CODEC.encode(buf, recipe.getRecipeOutputCompound());
    }


    @Override
    public MapCodec<T> codec() {
        return codec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
        return streamCodec;
    }

    public interface BlastProcessingRecipeInitializer<T extends BlastProcessingRecipe> {
        T create(Ingredient ingredient, double explosionPowerMin, double explosionPowerMax, RecipeOutputCompound compound);
    }
}