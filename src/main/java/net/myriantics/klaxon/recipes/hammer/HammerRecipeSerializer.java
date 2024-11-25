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

    private final MapCodec<HammerRecipe> CODEC = RecordCodecBuilder.mapCodec((recipeInstance -> {
        return recipeInstance.group(Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("inputA").forGetter((recipe) -> {
            return recipe.getInputA();
                }), ItemStack.CODEC.fieldOf("outputItem").forGetter((recipe) -> {
            return recipe.getResult(null);
                })).apply(recipeInstance, HammerRecipe::new);
    }));

    private final PacketCodec<RegistryByteBuf, HammerRecipe> PACKET_CODEC = PacketCodec.ofStatic(
            HammerRecipeSerializer::write, HammerRecipeSerializer::read
    );

    private static HammerRecipe read(RegistryByteBuf buf) {
        Ingredient ingredient = Ingredient.PACKET_CODEC.decode(buf);
        ItemStack output = ItemStack.PACKET_CODEC.decode(buf);

        return new HammerRecipe(ingredient, output);
    }

    private static void write(RegistryByteBuf buf, HammerRecipe recipe) {
        Ingredient.PACKET_CODEC.encode(buf, recipe.getInputA());
        ItemStack.PACKET_CODEC.encode(buf, recipe.getResult(null));
    }

    @Override
    public MapCodec<HammerRecipe> codec() {
        return CODEC;
    }

    @Override
    public PacketCodec<RegistryByteBuf, HammerRecipe> packetCodec() {
        return PACKET_CODEC;
    }
}
