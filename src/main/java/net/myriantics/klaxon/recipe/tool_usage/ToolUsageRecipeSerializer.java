package net.myriantics.klaxon.recipe.tool_usage;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;

public class ToolUsageRecipeSerializer implements RecipeSerializer<ToolUsageRecipe> {
    public ToolUsageRecipeSerializer() {
    }

    private final MapCodec<ToolUsageRecipe> CODEC = RecordCodecBuilder.mapCodec((recipeInstance -> {
        return recipeInstance.group(
                Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("required_tool").forGetter(ToolUsageRecipe::getRequiredTool),
                Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("input_ingredient").forGetter(ToolUsageRecipe::getInputIngredient),
                ItemStack.OPTIONAL_CODEC.fieldOf("output_stack").forGetter(ToolUsageRecipe::getOutputStack)
                )
                .apply(recipeInstance, ToolUsageRecipe::new);
    }));

    private final PacketCodec<RegistryByteBuf, ToolUsageRecipe> PACKET_CODEC = PacketCodec.ofStatic(
            ToolUsageRecipeSerializer::write, ToolUsageRecipeSerializer::read
    );

    private static ToolUsageRecipe read(RegistryByteBuf buf) {
        Ingredient requiredTool = Ingredient.PACKET_CODEC.decode(buf);
        Ingredient ingredient = Ingredient.PACKET_CODEC.decode(buf);
        ItemStack output = ItemStack.OPTIONAL_PACKET_CODEC.decode(buf);

        return new ToolUsageRecipe(requiredTool, ingredient, output);
    }

    private static void write(RegistryByteBuf buf, ToolUsageRecipe recipe) {
        Ingredient.PACKET_CODEC.encode(buf, recipe.getRequiredTool());
        Ingredient.PACKET_CODEC.encode(buf, recipe.getInputIngredient());
        ItemStack.OPTIONAL_PACKET_CODEC.encode(buf, recipe.getResult(null));
    }

    @Override
    public MapCodec<ToolUsageRecipe> codec() {
        return CODEC;
    }

    @Override
    public PacketCodec<RegistryByteBuf, ToolUsageRecipe> packetCodec() {
        return PACKET_CODEC;
    }
}
