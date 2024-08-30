package net.myriantics.klaxon.recipes.item_explosion_power;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class ItemExplosionPowerRecipeSerializer implements RecipeSerializer<ItemExplosionPowerRecipe> {
    public ItemExplosionPowerRecipeSerializer() {
    }

    @Override
    public ItemExplosionPowerRecipe read(Identifier id, JsonObject json) {
        Ingredient item = Ingredient.fromJson(json.getAsJsonObject("input"));
        double explosionPower = JsonHelper.getDouble(json, "explosion_power", 0.0);
        boolean producesFire = JsonHelper.getBoolean(json, "produces_fire", false);

        if(item == null) {
            throw new JsonSyntaxException("A required attribute is missing!");
        }

        return new ItemExplosionPowerRecipe(item, explosionPower, producesFire, id);
    }

    @Override
    public void write(PacketByteBuf packetData, ItemExplosionPowerRecipe recipe) {
        recipe.getItem().write(packetData);
        packetData.writeDouble(recipe.getExplosionPower());
        packetData.writeBoolean(recipe.producesFire());
    }


    @Override
    public ItemExplosionPowerRecipe read(Identifier id, PacketByteBuf packetData) {
        Ingredient item = Ingredient.fromPacket(packetData);
        double explosionPower = packetData.readDouble();
        boolean producesFire = packetData.readBoolean();

        return new ItemExplosionPowerRecipe(item, explosionPower, producesFire, id);
    }
}