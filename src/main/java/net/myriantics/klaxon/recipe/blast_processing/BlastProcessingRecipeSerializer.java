package net.myriantics.klaxon.recipe.blast_processing;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.myriantics.klaxon.util.KlaxonCodecUtils;

public class BlastProcessingRecipeSerializer implements RecipeSerializer<BlastProcessingRecipe> {
    public BlastProcessingRecipeSerializer() {
    }

    @Override
    public BlastProcessingRecipe read(Identifier id, JsonObject json) {
        Ingredient inputIngredient = Ingredient.fromJson(json.getAsJsonObject("input_ingredient"));
        ItemStack outputStack = KlaxonCodecUtils.readStackFromJson(json.getAsJsonObject("output_stack"));
        double explosionPowerMin = JsonHelper.getDouble(json, "explosion_power_min", 0.0);
        double explosionPowerMax = JsonHelper.getDouble(json, "explosion_power_max", 0.0);

        if (inputIngredient == null) {
            throw new JsonSyntaxException("A required attribute is missing!");
        }

        return new BlastProcessingRecipe(id, inputIngredient, explosionPowerMin, explosionPowerMax, outputStack);
    }

    @Override
    public BlastProcessingRecipe read(Identifier id, PacketByteBuf buf) {
        Ingredient inputIngredient = Ingredient.fromPacket(buf);
        ItemStack outputStack = buf.readItemStack();
        double explosionPowerMin = buf.readDouble();
        double explosionPowerMax = buf.readDouble();

        return new BlastProcessingRecipe(id, inputIngredient, explosionPowerMin, explosionPowerMax, outputStack);
    }

    @Override
    public void write(PacketByteBuf buf, BlastProcessingRecipe recipe) {
        recipe.getIngredient().write(buf);
        buf.writeItemStack(recipe.getOutput(null));
        buf.writeDouble(recipe.getExplosionPowerMin());
        buf.writeDouble(recipe.getExplosionPowerMax());
    }
}