package net.myriantics.klaxon.recipe.cooling;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;

public class ItemCoolingRecipeSerializer implements RecipeSerializer<ItemCoolingRecipe> {
    public ItemCoolingRecipeSerializer() {
    }

    private static final MapCodec<ItemCoolingRecipe> CODEC = RecordCodecBuilder.mapCodec((recipeInstance -> {
        return recipeInstance.group(
                Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("input_ingredient").forGetter(ItemCoolingRecipe::getInputIngredient),
                ItemStack.UNCOUNTED_CODEC.fieldOf("output_item").forGetter(ItemCoolingRecipe::getOutputStack)
                )
                .apply(recipeInstance, ItemCoolingRecipe::new);
    }));

    private static final PacketCodec<RegistryByteBuf, ItemCoolingRecipe> PACKET_CODEC = PacketCodec.ofStatic(
            ItemCoolingRecipeSerializer::write, ItemCoolingRecipeSerializer::read
    );

    @Override
    public MapCodec<ItemCoolingRecipe> codec() {
        return CODEC;
    }

    @Override
    public PacketCodec<RegistryByteBuf, ItemCoolingRecipe> packetCodec() {
        return PACKET_CODEC;
    }

    private static ItemCoolingRecipe read(RegistryByteBuf buf) {
        Ingredient inputIngredient = Ingredient.PACKET_CODEC.decode(buf);
        ItemStack outputItem = ItemStack.PACKET_CODEC.decode(buf);

        return new ItemCoolingRecipe(inputIngredient, outputItem);
    }

    private static void write(RegistryByteBuf buf, ItemCoolingRecipe recipe) {
        Ingredient.PACKET_CODEC.encode(buf, recipe.getInputIngredient());
        ItemStack.PACKET_CODEC.encode(buf, recipe.getOutputStack());
    }
}
