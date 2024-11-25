package net.myriantics.klaxon.recipes.blast_processing;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.dynamic.Codecs;

import java.util.Arrays;

public class BlastProcessingRecipeSerializer implements RecipeSerializer<BlastProcessingRecipe> {
    public BlastProcessingRecipeSerializer() {
    }



    private final MapCodec<BlastProcessingRecipe> CODEC = RecordCodecBuilder.mapCodec((recipeInstance -> {
        return recipeInstance.group(Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("processItem").forGetter((recipe) -> {
            return recipe.getProcessingItem();
        }), PrimitiveCodec.DOUBLE.fieldOf("explosionPowerMin").forGetter((recipe) -> {
            return recipe.getExplosionPowerMin();
        }), PrimitiveCodec.DOUBLE.fieldOf("explosionPowerMax").forGetter((recipe) -> {
            return recipe.getExplosionPowerMax();
        }), ItemStack.CODEC.fieldOf("outputStack").forGetter((recipe) -> {
            return recipe.getResult(null);
        })).apply(recipeInstance, BlastProcessingRecipe::new);
    }));

    private final PacketCodec<RegistryByteBuf, BlastProcessingRecipe> PACKET_CODEC = PacketCodec.ofStatic(
            BlastProcessingRecipeSerializer::write, BlastProcessingRecipeSerializer::read
    );

    private static BlastProcessingRecipe read(RegistryByteBuf buf) {
        Ingredient inputA = Ingredient.PACKET_CODEC.decode(buf);
        double explosionPowerMin = PacketCodecs.DOUBLE.decode(buf);
        double explosionPowerMax = PacketCodecs.DOUBLE.decode(buf);
        ItemStack output = ItemStack.PACKET_CODEC.decode(buf);

        return new BlastProcessingRecipe(inputA, explosionPowerMin, explosionPowerMax, output);
    }

    private static void write(RegistryByteBuf buf, BlastProcessingRecipe recipe) {
        Ingredient.PACKET_CODEC.encode(buf, recipe.getProcessingItem());
        PacketCodecs.DOUBLE.encode(buf, recipe.getExplosionPowerMin());
        PacketCodecs.DOUBLE.encode(buf, recipe.getExplosionPowerMax());
        ItemStack.PACKET_CODEC.encode(buf, recipe.getResult(null));
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