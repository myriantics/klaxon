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

public class BlastProcessingRecipeSerializer implements RecipeSerializer<BlastProcessingRecipe> {
    public BlastProcessingRecipeSerializer() {
    }

    private final MapCodec<BlastProcessingRecipe> CODEC = RecordCodecBuilder.mapCodec((recipeInstance -> {
        return recipeInstance.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("input_ingredient").forGetter(BlastProcessingRecipe::getIngredientItem),
                PrimitiveCodec.DOUBLE.fieldOf("explosion_power_min").forGetter(BlastProcessingRecipe::getExplosionPowerMin),
                PrimitiveCodec.DOUBLE.fieldOf("explosion_power_max").forGetter(BlastProcessingRecipe::getExplosionPowerMax),
                RecipeOutputCompound.createCodec(9).fieldOf("recipe_output_compound").forGetter(BlastProcessingRecipe::getRecipeOutputCompound)
        ).apply(recipeInstance, BlastProcessingRecipe::new);
    }));

    private final StreamCodec<RegistryFriendlyByteBuf, BlastProcessingRecipe> PACKET_CODEC = StreamCodec.of(
            BlastProcessingRecipeSerializer::write, BlastProcessingRecipeSerializer::read
    );

    private static BlastProcessingRecipe read(RegistryFriendlyByteBuf buf) {
        Ingredient ingredientItem = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
        double explosionPowerMin = ByteBufCodecs.DOUBLE.decode(buf);
        double explosionPowerMax = ByteBufCodecs.DOUBLE.decode(buf);
        RecipeOutputCompound outputCompound = RecipeOutputCompound.PACKET_CODEC.decode(buf);

        return new BlastProcessingRecipe(ingredientItem, explosionPowerMin, explosionPowerMax, outputCompound);
    }

    private static void write(RegistryFriendlyByteBuf buf, BlastProcessingRecipe recipe) {
        Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.getIngredientItem());
        ByteBufCodecs.DOUBLE.encode(buf, recipe.getExplosionPowerMin());
        ByteBufCodecs.DOUBLE.encode(buf, recipe.getExplosionPowerMax());
        RecipeOutputCompound.PACKET_CODEC.encode(buf, recipe.getRecipeOutputCompound());
    }


    @Override
    public MapCodec<BlastProcessingRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, BlastProcessingRecipe> streamCodec() {
        return PACKET_CODEC;
    }
}