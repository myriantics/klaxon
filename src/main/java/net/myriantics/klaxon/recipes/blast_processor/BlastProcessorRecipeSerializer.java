package net.myriantics.klaxon.recipes.blast_processor;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class BlastProcessorRecipeSerializer implements RecipeSerializer<BlastProcessorRecipe> {
    private BlastProcessorRecipeSerializer() {
    }

    public static final BlastProcessorRecipeSerializer INSTANCE = new BlastProcessorRecipeSerializer();

    public static final Identifier ID = new Identifier("klaxon:blast_processing");

    @Override
    public BlastProcessorRecipe read(Identifier id, JsonObject json) {
        BlastProcessorRecipeJsonFormat blastProcessorRecipeJson = new Gson().fromJson(json, BlastProcessorRecipeJsonFormat.class);

        if(blastProcessorRecipeJson.inputA == null || blastProcessorRecipeJson.outputItem == null) {
            throw new JsonSyntaxException("A required attribute is missing!");
        }

        if (blastProcessorRecipeJson.outputAmount == 0) blastProcessorRecipeJson.outputAmount = 1;


        Ingredient processingItem = Ingredient.fromJson(blastProcessorRecipeJson.inputA);
        double explosionPowerMin = blastProcessorRecipeJson.explosionPowerMin;
        double explosionPowerMax = blastProcessorRecipeJson.explosionPowerMax;
        Item outputItem = Registries.ITEM.getOrEmpty(new Identifier(blastProcessorRecipeJson.outputItem))
                .orElseThrow(() -> new JsonSyntaxException("No such item " + blastProcessorRecipeJson.outputItem));
        ItemStack output = new ItemStack(outputItem, blastProcessorRecipeJson.outputAmount);

        return new BlastProcessorRecipe(processingItem, explosionPowerMin, explosionPowerMax, output, id);
    }

    @Override
    public void write(PacketByteBuf packetData, BlastProcessorRecipe recipe) {
        recipe.getProcessingItem().write(packetData);
        packetData.writeItemStack(recipe.getOutput(DynamicRegistryManager.of(Registries.REGISTRIES)));
    }


    @Override
    public BlastProcessorRecipe read(Identifier id, PacketByteBuf packetData) {
        Ingredient inputA = Ingredient.fromPacket(packetData);
        double explosionPowerMin = packetData.readDouble();
        double explosionPowerMax = packetData.readDouble();
        ItemStack output = packetData.readItemStack();
        return new BlastProcessorRecipe(inputA, explosionPowerMin, explosionPowerMax, output, id);
    }
}