package net.myriantics.klaxon.recipe.makeshift_crafting.shapeless;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.myriantics.klaxon.util.KlaxonCodecUtils;

import java.util.List;

public class MakeshiftShapelessCraftingRecipeSerializer implements RecipeSerializer<MakeshiftShapelessCraftingRecipe> {

    @Override
    public MakeshiftShapelessCraftingRecipe read(Identifier id, JsonObject json) {
        String group = JsonHelper.getString(json, "group", "");
        CraftingRecipeCategory category = CraftingRecipeCategory.CODEC
                .byId(JsonHelper.getString(json, "category", null), CraftingRecipeCategory.MISC);
        DefaultedList<Ingredient> inputIngredients = getIngredients(JsonHelper.getArray(json, "ingredients"));
        if (inputIngredients.isEmpty()) {
            throw new JsonParseException("No ingredients for shapeless recipe");
        } else if (inputIngredients.size() > 9) {
            throw new JsonParseException("Too many ingredients for shapeless recipe");
        } else {
            ItemStack itemStack = ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "result"));

            // parse constant ingredients
            List<Ingredient> constantIngredients = KlaxonCodecUtils.readIngredientListFromJson(JsonHelper.getArray(json, "constant_ingredients", KlaxonCodecUtils.getDummyIngredientJsonArray()));
            return new MakeshiftShapelessCraftingRecipe(id, group, category, itemStack, inputIngredients, constantIngredients);
        }
    }

    @Override
    public MakeshiftShapelessCraftingRecipe read(Identifier id, PacketByteBuf buf) {
        // decode group stuff
        String group = buf.readString();
        CraftingRecipeCategory category = buf.readEnumConstant(CraftingRecipeCategory.class);

        // decode ingredients
        int inputIngredientsLength = buf.readVarInt();
        DefaultedList<Ingredient> inputIngredients = DefaultedList.ofSize(inputIngredientsLength);
        inputIngredients.replaceAll(empty -> Ingredient.fromPacket(buf));

        // decode constant ingredients
        int constantIngredientsLength = buf.readVarInt();
        DefaultedList<Ingredient> constantIngredients = DefaultedList.ofSize(constantIngredientsLength);
        constantIngredients.replaceAll(empty -> Ingredient.fromPacket(buf));

        // decode result
        ItemStack result = buf.readItemStack();

        return new MakeshiftShapelessCraftingRecipe(id, group, category, result, inputIngredients, constantIngredients);
    }

    @Override
    public void write(PacketByteBuf buf, MakeshiftShapelessCraftingRecipe recipe) {
        // encode group stuff
        buf.writeString(recipe.getGroup());
        buf.writeEnumConstant(recipe.getCategory());

        // encode ingredients
        buf.writeVarInt(recipe.ingredients.size());
        for (Ingredient ingredient : recipe.ingredients) {
            ingredient.write(buf);
        }

        // encode constant ingredients
        buf.writeVarInt(recipe.constantIngredients.size());
        for (Ingredient ingredient : recipe.constantIngredients) {
            ingredient.write(buf);
        }

        // encode result
        buf.writeItemStack(recipe.getRawResult());
    }

    private static DefaultedList<Ingredient> getIngredients(JsonArray json) {
        DefaultedList<Ingredient> defaultedList = DefaultedList.of();

        for (int i = 0; i < json.size(); i++) {
            Ingredient ingredient = Ingredient.fromJson(json.get(i), false);
            if (!ingredient.isEmpty()) {
                defaultedList.add(ingredient);
            }
        }

        return defaultedList;
    }
}
