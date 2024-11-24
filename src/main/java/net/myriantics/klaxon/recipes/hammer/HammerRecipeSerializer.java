package net.myriantics.klaxon.recipes.hammer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class HammerRecipeSerializer implements RecipeSerializer<HammerRecipe> {
    public HammerRecipeSerializer() {
    }

    private final MapCodec<HammerRecipe> CODEC = RecordCodecBuilder.mapCodec((hammerRecipeInstance -> {
        return hammerRecipeInstance.group(ItemStack.CODEC.fieldOf("outputItem").forGetter((recipe) -> {
            return ((HammerRecipe) recipe).getResult(null);
                }));
    }));

    @Override
    public HammerRecipe read(Identifier id, JsonObject json) {
        Ingredient input = Ingredient.(json.getAsJsonObject("inputA"));
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

    @Override
    public MapCodec<HammerRecipe> codec() {
        return ;
    }

    @Override
    public PacketCodec<RegistryByteBuf, HammerRecipe> packetCodec() {
        return null;
    }
}
