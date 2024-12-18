package net.myriantics.klaxon.recipe.hammering;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;

public class HammeringRecipeSerializer implements RecipeSerializer<HammeringRecipe> {
    public HammeringRecipeSerializer() {
    }

    private final MapCodec<HammeringRecipe> CODEC = RecordCodecBuilder.mapCodec((recipeInstance -> {
        return recipeInstance.group(Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("input").forGetter((recipe) -> {
            return recipe.getIngredient();
                }), ItemStack.OPTIONAL_CODEC.fieldOf("output").forGetter((recipe) -> {
            return recipe.getResult(null);
                })).apply(recipeInstance, HammeringRecipe::new);
    }));

    private final PacketCodec<RegistryByteBuf, HammeringRecipe> PACKET_CODEC = PacketCodec.ofStatic(
            HammeringRecipeSerializer::write, HammeringRecipeSerializer::read
    );

    private static HammeringRecipe read(RegistryByteBuf buf) {
        Ingredient ingredient = Ingredient.PACKET_CODEC.decode(buf);
        ItemStack output = ItemStack.OPTIONAL_PACKET_CODEC.decode(buf);

        return new HammeringRecipe(ingredient, output);
    }

    private static void write(RegistryByteBuf buf, HammeringRecipe recipe) {
        Ingredient.PACKET_CODEC.encode(buf, recipe.getIngredient());
        ItemStack.OPTIONAL_PACKET_CODEC.encode(buf, recipe.getResult(null));
    }

    @Override
    public MapCodec<HammeringRecipe> codec() {
        return CODEC;
    }

    @Override
    public PacketCodec<RegistryByteBuf, HammeringRecipe> packetCodec() {
        return PACKET_CODEC;
    }
}
