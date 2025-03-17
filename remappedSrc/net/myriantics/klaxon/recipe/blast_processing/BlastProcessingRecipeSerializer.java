package net.myriantics.klaxon.recipe.blast_processing;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.*;

public class BlastProcessingRecipeSerializer implements RecipeSerializer<BlastProcessingRecipe> {
    public BlastProcessingRecipeSerializer() {
    }

    private final MapCodec<BlastProcessingRecipe> CODEC = RecordCodecBuilder.mapCodec((recipeInstance -> {
        return recipeInstance.group(Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("ingredientItem").forGetter((recipe) -> {
            return recipe.getIngredientItem();
        }), PrimitiveCodec.DOUBLE.fieldOf("explosionPowerMin").forGetter((recipe) -> {
            return recipe.getExplosionPowerMin();
        }), PrimitiveCodec.DOUBLE.fieldOf("explosionPowerMax").forGetter((recipe) -> {
            return recipe.getExplosionPowerMax();
        }), ItemStack.OPTIONAL_CODEC.fieldOf("output").forGetter((recipe) -> {
            return recipe.getResult(null);
        })).apply(recipeInstance, BlastProcessingRecipe::new);
    }));

    private final PacketCodec<RegistryByteBuf, BlastProcessingRecipe> PACKET_CODEC = PacketCodec.ofStatic(
            BlastProcessingRecipeSerializer::write, BlastProcessingRecipeSerializer::read
    );

    private static BlastProcessingRecipe read(RegistryByteBuf buf) {
        Ingredient ingredientItem = Ingredient.PACKET_CODEC.decode(buf);
        double explosionPowerMin = PacketCodecs.DOUBLE.decode(buf);
        double explosionPowerMax = PacketCodecs.DOUBLE.decode(buf);
        ItemStack output = ItemStack.OPTIONAL_PACKET_CODEC.decode(buf);

        return new BlastProcessingRecipe(ingredientItem, explosionPowerMin, explosionPowerMax, output);
    }

    private static void write(RegistryByteBuf buf, BlastProcessingRecipe recipe) {
        Ingredient.PACKET_CODEC.encode(buf, recipe.getIngredientItem());
        PacketCodecs.DOUBLE.encode(buf, recipe.getExplosionPowerMin());
        PacketCodecs.DOUBLE.encode(buf, recipe.getExplosionPowerMax());
        ItemStack.OPTIONAL_PACKET_CODEC.encode(buf, recipe.getOutput(null));
    }


    @Override
    public MapCodec<BlastProcessingRecipe> codec() {
        return CODEC;
    }

    @Override
    public PacketCodec<RegistryByteBuf, BlastProcessingRecipe> packetCodec() {
        return PACKET_CODEC;
    }
}