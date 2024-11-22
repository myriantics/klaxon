package net.myriantics.klaxon.recipes.blast_processing;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class BlastProcessingRecipeSerializer implements RecipeSerializer<BlastProcessingRecipe> {
    public BlastProcessingRecipeSerializer() {
    }

    @Override
    public BlastProcessingRecipe read(Identifier id, JsonObject json) {
        Ingredient processingItem = Ingredient.fromJson(json.getAsJsonObject("inputA"));
        Item outputItem = JsonHelper.getItem(json, "outputItem");
        int outputAmount = JsonHelper.getInt(json, "outputAmount", 1);
        double explosionPowerMin = JsonHelper.getDouble(json, "explosionPowerMin", 0.0);
        double explosionPowerMax = JsonHelper.getDouble(json, "explosionPowerMax", 0.0);

        if(processingItem == null) {
            throw new JsonSyntaxException("A required attribute is missing!");
        }

        ItemStack output = new ItemStack(outputItem, outputAmount);

        return new BlastProcessingRecipe(processingItem, explosionPowerMin, explosionPowerMax, output, id);
    }

    @Override
    public void write(PacketByteBuf packetData, BlastProcessingRecipe recipe) {
        recipe.getProcessingItem().write(packetData);
        packetData.writeItemStack(recipe.getOutput(null));
        packetData.writeDouble(recipe.getExplosionPowerMin());
        packetData.writeDouble(recipe.getExplosionPowerMax());
    }


    @Override
    public BlastProcessingRecipe read(Identifier id, PacketByteBuf packetData) {
        Ingredient inputA = Ingredient.fromPacket(packetData);
        ItemStack output = packetData.readItemStack();
        double explosionPowerMin = packetData.readDouble();
        double explosionPowerMax = packetData.readDouble();
        return new BlastProcessingRecipe(inputA, explosionPowerMin, explosionPowerMax, output, id);
    }
}