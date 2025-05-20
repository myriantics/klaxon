package net.myriantics.klaxon.recipe.tool_usage;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.sound.SoundEvent;
import net.myriantics.klaxon.util.KlaxonCodecUtils;

public class ToolUsageRecipeSerializer implements RecipeSerializer<ToolUsageRecipe> {
    public ToolUsageRecipeSerializer() {
    }

    private final MapCodec<ToolUsageRecipe> CODEC = RecordCodecBuilder.mapCodec((recipeInstance -> {
        return recipeInstance.group(
                Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("required_tool").forGetter(ToolUsageRecipe::getRequiredTool),
                Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("input_ingredient").forGetter(ToolUsageRecipe::getInputIngredient),
                ItemStack.OPTIONAL_CODEC.fieldOf("output_stack").forGetter(ToolUsageRecipe::getOutputStack),
                KlaxonCodecUtils.OPTIONAL_SOUND_EVENT_CODEC.fieldOf("sound_override").forGetter(ToolUsageRecipe::getSoundOverride)
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
        SoundEvent soundOverride = KlaxonCodecUtils.OPTIONAL_SOUND_EVENT_PACKET_CODEC.decode(buf);

        return new ToolUsageRecipe(requiredTool, ingredient, output, soundOverride);
    }

    private static void write(RegistryByteBuf buf, ToolUsageRecipe recipe) {
        Ingredient.PACKET_CODEC.encode(buf, recipe.getRequiredTool());
        Ingredient.PACKET_CODEC.encode(buf, recipe.getInputIngredient());
        ItemStack.OPTIONAL_PACKET_CODEC.encode(buf, recipe.getResult(null));
        KlaxonCodecUtils.OPTIONAL_SOUND_EVENT_PACKET_CODEC.encode(buf, recipe.getSoundOverride());
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
