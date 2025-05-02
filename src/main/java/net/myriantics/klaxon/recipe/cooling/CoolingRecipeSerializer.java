package net.myriantics.klaxon.recipe.cooling;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;

public class CoolingRecipeSerializer implements RecipeSerializer<CoolingRecipe> {
    public CoolingRecipeSerializer() {
    }

    private static final MapCodec<CoolingRecipe> CODEC = RecordCodecBuilder.mapCodec((recipeInstance -> {
        return recipeInstance.group(
                Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("input_ingredient").forGetter(CoolingRecipe::getInputIngredient),
                ItemStack.UNCOUNTED_CODEC.fieldOf("output_item").forGetter(CoolingRecipe::getOutputStack)
                )
                .apply(recipeInstance, CoolingRecipe::new);
    }));

    private static final PacketCodec<RegistryByteBuf, CoolingRecipe> PACKET_CODEC = PacketCodec.ofStatic(
            CoolingRecipeSerializer::write, CoolingRecipeSerializer::read
    );

    @Override
    public MapCodec<CoolingRecipe> codec() {
        return CODEC;
    }

    @Override
    public PacketCodec<RegistryByteBuf, CoolingRecipe> packetCodec() {
        return PACKET_CODEC;
    }

    private static CoolingRecipe read(RegistryByteBuf buf) {
        Ingredient inputIngredient = Ingredient.PACKET_CODEC.decode(buf);
        ItemStack outputItem = ItemStack.PACKET_CODEC.decode(buf);

        return new CoolingRecipe(inputIngredient, outputItem);
    }

    private static void write(RegistryByteBuf buf, CoolingRecipe recipe) {
        Ingredient.PACKET_CODEC.encode(buf, recipe.getInputIngredient());
        ItemStack.PACKET_CODEC.encode(buf, recipe.getOutputStack());
    }
}
