package net.myriantics.klaxon.recipes.hammer;

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

public class HammerRecipeSerializer implements RecipeSerializer<HammerRecipe> {
    private HammerRecipeSerializer() {
    }

    public static final HammerRecipeSerializer INSTANCE = new HammerRecipeSerializer();

    public static final Identifier ID = new Identifier("klaxon:hammering");

    @Override
    public HammerRecipe read(Identifier id, JsonObject json) {
        HammerRecipeJsonFormat hammerRecipeJson = new Gson().fromJson(json, HammerRecipeJsonFormat.class);

        if(hammerRecipeJson.inputA == null || hammerRecipeJson.outputItem == null) {
            throw new JsonSyntaxException("A required attribute is missing!");
        }

        if (hammerRecipeJson.outputAmount == 0) hammerRecipeJson.outputAmount = 1;


        Ingredient inputA = Ingredient.fromJson(hammerRecipeJson.inputA);
        Item outputItem = Registries.ITEM.getOrEmpty(new Identifier(hammerRecipeJson.outputItem))
                .orElseThrow(() -> new JsonSyntaxException("No such item " + hammerRecipeJson.outputItem));
        ItemStack output = new ItemStack(outputItem, hammerRecipeJson.outputAmount);

        return new HammerRecipe(inputA, output, id);
    }

    @Override
    public void write(PacketByteBuf packetData, HammerRecipe recipe) {
        recipe.getInputA().write(packetData);
        packetData.writeItemStack(recipe.getOutput(DynamicRegistryManager.of(Registries.REGISTRIES)));
    }

    //MinecraftClient.getInstance().getServer().getRegistryManager()
    @Override
    public HammerRecipe read(Identifier id, PacketByteBuf packetData) {
        Ingredient inputA = Ingredient.fromPacket(packetData);
        ItemStack output = packetData.readItemStack();
        return new HammerRecipe(inputA, output, id);
    }
}
