package net.myriantics.klaxon.recipes.item_explosion_power;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class ItemExplosionPowerRecipeSerializer implements RecipeSerializer<ItemExplosionPowerRecipe> {
    private ItemExplosionPowerRecipeSerializer() {
    }

    public static final ItemExplosionPowerRecipeSerializer INSTANCE = new ItemExplosionPowerRecipeSerializer();

    public static final Identifier ID = new Identifier("klaxon:item_explosion_power");

    @Override
    public ItemExplosionPowerRecipe read(Identifier id, JsonObject json) {
        ItemExplosionPowerRecipeJsonFormat itemExplosionPowerRecipeJson = new Gson().fromJson(json, ItemExplosionPowerRecipeJsonFormat.class);

        if(itemExplosionPowerRecipeJson.input == null) {
            throw new JsonSyntaxException("A required attribute is missing!");
        }

        Ingredient item = Ingredient.fromJson(itemExplosionPowerRecipeJson.input);
        double explosionPower = itemExplosionPowerRecipeJson.explosion_power;
        boolean producesFire = itemExplosionPowerRecipeJson.produces_fire;

        return new ItemExplosionPowerRecipe(item, explosionPower, producesFire, id);
    }

    @Override
    public void write(PacketByteBuf packetData, ItemExplosionPowerRecipe recipe) {
        recipe.getItem().write(packetData);
        packetData.writeItemStack(recipe.getOutput(DynamicRegistryManager.of(Registries.REGISTRIES)));
    }


    @Override
    public ItemExplosionPowerRecipe read(Identifier id, PacketByteBuf packetData) {
        Ingredient item = Ingredient.fromPacket(packetData);
        double explosionPower = packetData.readDouble();
        boolean producesFire = packetData.readBoolean();

        return new ItemExplosionPowerRecipe(item, explosionPower, producesFire, id);
    }
}