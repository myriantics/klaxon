package net.myriantics.klaxon.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;

public abstract class KlaxonCodecUtils {

    public static void writeIngredientToJson(Ingredient ingredient, JsonObject jsonObject) {

    }

    public static JsonArray writeIngredientListToJson(List<Ingredient> ingredients) {
        JsonArray jsonArray = new JsonArray(ingredients.size());

        for (Ingredient ingredient : ingredients) {
            jsonArray.add(ingredient.toJson());
        }

        return jsonArray;
    }

    public static List<Ingredient> readIngredientListFromJson(JsonArray jsonArray) {
        DefaultedList<Ingredient> ingredients = DefaultedList.of();

        for (JsonElement jsonElement : jsonArray) {
            ingredients.add(Ingredient.fromJson(jsonElement));
        }

        return List.copyOf(ingredients);
    }

    public static void writeItemToJson(ItemConvertible item, JsonObject jsonObject) {
        jsonObject.addProperty("item", Registries.ITEM.getId(item.asItem()).toString());
    }

    public static JsonObject writeStackToJson(ItemStack stack) {
        JsonObject jsonObject = new JsonObject();

        writeItemToJson(stack.getItem(), jsonObject);
        jsonObject.addProperty("count", stack.getCount());

        return jsonObject;
    }

    public static ItemStack readStackFromJson(JsonObject jsonObject) {
        Item item = JsonHelper.getItem(jsonObject, "item");
        int count = JsonHelper.getInt(jsonObject, "count", 0);

        return new ItemStack(item, count);
    }
}
