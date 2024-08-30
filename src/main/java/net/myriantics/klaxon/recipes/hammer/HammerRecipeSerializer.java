package net.myriantics.klaxon.recipes.hammer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class HammerRecipeSerializer implements RecipeSerializer<HammerRecipe> {
    public HammerRecipeSerializer() {
    }

    @Override
    public HammerRecipe read(Identifier id, JsonObject json) {
        Ingredient input = Ingredient.fromJson(json.getAsJsonObject("inputA"));
        Item outputItem = JsonHelper.getItem(json, "outputItem");
        int outputAmount = JsonHelper.getInt(json, "outputAmount", 1);

        if(input == null || outputItem == null) {
            throw new JsonSyntaxException("A required attribute is missing!");
        }

        ItemStack output = new ItemStack(outputItem, outputAmount);

        return new HammerRecipe(input, output, id);
    }

    @Override
    public void write(PacketByteBuf packetData, HammerRecipe recipe) {
        recipe.getInputA().write(packetData);
        packetData.writeItemStack(recipe.getOutput(null));
    }

    @Override
    public HammerRecipe read(Identifier id, PacketByteBuf packetData) {
        Ingredient inputA = Ingredient.fromPacket(packetData);
        ItemStack output = packetData.readItemStack();
        return new HammerRecipe(inputA, output, id);
    }
}
