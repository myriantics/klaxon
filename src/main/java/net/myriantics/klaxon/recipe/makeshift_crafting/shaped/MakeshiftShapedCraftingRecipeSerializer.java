package net.myriantics.klaxon.recipe.makeshift_crafting.shaped;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.myriantics.klaxon.mixin.ShapedRecipeInvoker;
import net.myriantics.klaxon.util.KlaxonCodecUtils;

import java.util.List;
import java.util.Map;

public class MakeshiftShapedCraftingRecipeSerializer implements RecipeSerializer<MakeshiftShapedCraftingRecipe> {

    @Override
    public MakeshiftShapedCraftingRecipe read(Identifier id, JsonObject json) {
        String group = JsonHelper.getString(json, "group", "");
        CraftingRecipeCategory craftingRecipeCategory = CraftingRecipeCategory.CODEC
                .byId(JsonHelper.getString(json, "category", null), CraftingRecipeCategory.MISC);
        Map<String, Ingredient> key = ShapedRecipeInvoker.klaxon$invokeReadSymbols(JsonHelper.getObject(json, "key"));
        String[] strings = ShapedRecipeInvoker.klaxon$invokeRemovePadding(ShapedRecipeInvoker.klaxon$invokeGetPattern(JsonHelper.getArray(json, "pattern")));
        int i = strings[0].length();
        int j = strings.length;

        List<Ingredient> constantIngredients = KlaxonCodecUtils.readIngredientListFromJson(json.getAsJsonArray("constant_ingredients"));

        DefaultedList<Ingredient> ingredients = ShapedRecipeInvoker.klaxon$invokeCreatePatternMatrix(strings, key, i, j);
        ItemStack itemStack = ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "result"));
        boolean bl = JsonHelper.getBoolean(json, "show_notification", true);

        return new MakeshiftShapedCraftingRecipe(id, group, craftingRecipeCategory, i, j, ingredients, constantIngredients, itemStack, bl);
    }

    @Override
    public MakeshiftShapedCraftingRecipe read(Identifier id, PacketByteBuf buf) {

        // parse recipe group stuff
        String group = buf.readString();
        CraftingRecipeCategory craftingRecipeCategory = buf.readEnumConstant(CraftingRecipeCategory.class);
        boolean bl = buf.readBoolean();


        // parse input ingredients
        int width = buf.readVarInt();
        int height = buf.readVarInt();
        DefaultedList<Ingredient> ingredients = DefaultedList.ofSize(width * height, Ingredient.EMPTY);
        ingredients.replaceAll(empty -> Ingredient.fromPacket(buf));

        // parse constant ingredients
        DefaultedList<Ingredient> constantIngredients = DefaultedList.ofSize(buf.readVarInt());
        constantIngredients.replaceAll(empty -> Ingredient.fromPacket(buf));

        // parse output
        ItemStack itemStack = buf.readItemStack();

        return new MakeshiftShapedCraftingRecipe(id, group, craftingRecipeCategory, width, height, ingredients, constantIngredients, itemStack, bl);
    }

    @Override
    public void write(PacketByteBuf buf, MakeshiftShapedCraftingRecipe recipe) {
        // encode recipe group stuff
        buf.writeString(recipe.getGroup());
        buf.writeEnumConstant(recipe.getCategory());
        buf.writeBoolean(recipe.showNotification());

        // encode recipe input stuff
        buf.writeVarInt(recipe.getWidth());
        buf.writeVarInt(recipe.getHeight());
        for (Ingredient ingredient : recipe.getIngredients()) {
            ingredient.write(buf);
        }

        // encode constant ingredients
        buf.writeVarInt(recipe.getConstantIngredients().size());
        for (Ingredient ingredient : recipe.getConstantIngredients()) {
            ingredient.write(buf);
        }

        // encode result
        buf.writeItemStack(recipe.getOutput(null));
    }
}
