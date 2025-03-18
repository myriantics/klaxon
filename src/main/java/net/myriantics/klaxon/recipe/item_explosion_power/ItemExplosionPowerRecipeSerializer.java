package net.myriantics.klaxon.recipe.item_explosion_power;

import com.google.gson.JsonObject;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class ItemExplosionPowerRecipeSerializer implements RecipeSerializer<ItemExplosionPowerRecipe> {
    public ItemExplosionPowerRecipeSerializer() {
    }


    @Override
    public ItemExplosionPowerRecipe read(Identifier id, JsonObject json) {
        Ingredient catalystIngredient = Ingredient.fromJson(json.getAsJsonObject("catalyst_ingredient"));
        double explosionPower = JsonHelper.getDouble(json, "explosion_power");
        boolean producesFire = JsonHelper.getBoolean(json, "produces_fire");
        boolean isHidden = JsonHelper.getBoolean(json, "is_hidden");

        return new ItemExplosionPowerRecipe(id, catalystIngredient, explosionPower, producesFire, isHidden);
    }

    @Override
    public ItemExplosionPowerRecipe read(Identifier id, PacketByteBuf buf) {
        Ingredient catalystIngredient = Ingredient.fromPacket(buf);
        double explosionPower = buf.readDouble();
        boolean producesFire = buf.readBoolean();
        boolean isHidden = buf.readBoolean();

        return new ItemExplosionPowerRecipe(id, catalystIngredient, explosionPower, producesFire, isHidden);
    }

    @Override
    public void write(PacketByteBuf buf, ItemExplosionPowerRecipe recipe) {
        recipe.getIngredient().write(buf);
        buf.writeDouble(recipe.getExplosionPower());
        buf.writeBoolean(recipe.producesFire());
        buf.writeBoolean(recipe.isHidden());
    }
}