package net.myriantics.klaxon.recipe.hammering;

import com.google.gson.JsonObject;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.util.KlaxonCodecUtils;

public class HammeringRecipeSerializer implements RecipeSerializer<HammeringRecipe> {
    public HammeringRecipeSerializer() {
    }

    @Override
    public HammeringRecipe read(Identifier id, JsonObject json) {
        Ingredient inputIngredient = Ingredient.fromJson(json.getAsJsonObject("input_ingredient"));
        ItemStack outputStack = KlaxonCodecUtils.readStackFromJson(json.getAsJsonObject("output_stack"));

        return new HammeringRecipe(id, inputIngredient, outputStack);
    }

    @Override
    public HammeringRecipe read(Identifier id, PacketByteBuf buf) {
        Ingredient inputIngredient = Ingredient.fromPacket(buf);
        ItemStack outputStack = buf.readItemStack();

        return new HammeringRecipe(id, inputIngredient, outputStack);
    }

    @Override
    public void write(PacketByteBuf buf, HammeringRecipe recipe) {
        recipe.getIngredient().write(buf);
        buf.writeItemStack(recipe.getOutput(null));
    }
}
