package net.myriantics.klaxon.recipe.tool_usage;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.SoundEvent;
import net.myriantics.klaxon.registry.KlaxonRegistryKeys;
import net.myriantics.klaxon.util.KlaxonCodecUtils;

import javax.tools.Tool;

public class ToolUsageRecipeSerializer implements RecipeSerializer<ToolUsageRecipe> {
    public ToolUsageRecipeSerializer() {
    }

    private MapCodec<ToolUsageRecipe> createCodec() {
        return RecordCodecBuilder.mapCodec(recipeInstance -> recipeInstance.group(
                ToolUsageRecipeType.KEY_CODEC.fieldOf("tool_usage_recipe_type").forGetter(ToolUsageRecipe::getTypeKey),
                Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("input_ingredient").forGetter(ToolUsageRecipe::getInputIngredient),
                ItemStack.OPTIONAL_CODEC.fieldOf("output_stack").forGetter(ToolUsageRecipe::getOutputStack),
                KlaxonCodecUtils.OPTIONAL_SOUND_EVENT_CODEC.fieldOf("sound_override").forGetter(ToolUsageRecipe::getSoundOverride)
        ).apply(recipeInstance, ToolUsageRecipe::new));
    }

    private PacketCodec<RegistryByteBuf, ToolUsageRecipe> createPacketCodec() {
        return PacketCodec.of(
                ((value, buf) -> this.write(buf, value)),
                (this::read)
        );
    }

    private ToolUsageRecipe read(RegistryByteBuf buf) {
        RegistryKey<ToolUsageRecipeType> typeKey = ToolUsageRecipeType.KEY_PACKET_CODEC.decode(buf);
        Ingredient ingredient = Ingredient.PACKET_CODEC.decode(buf);
        ItemStack output = ItemStack.OPTIONAL_PACKET_CODEC.decode(buf);
        SoundEvent soundOverride = KlaxonCodecUtils.OPTIONAL_SOUND_EVENT_PACKET_CODEC.decode(buf);

        return new ToolUsageRecipe(typeKey, ingredient, output, soundOverride);
    }

    private void write(RegistryByteBuf buf, ToolUsageRecipe recipe) {
        ToolUsageRecipeType.KEY_PACKET_CODEC.encode(buf, recipe.getTypeKey());
        Ingredient.PACKET_CODEC.encode(buf, recipe.getInputIngredient());
        ItemStack.OPTIONAL_PACKET_CODEC.encode(buf, recipe.getResult(buf.getRegistryManager()));
        KlaxonCodecUtils.OPTIONAL_SOUND_EVENT_PACKET_CODEC.encode(buf, recipe.getSoundOverride());
    }

    @Override
    public MapCodec<ToolUsageRecipe> codec() {
        return createCodec();
    }

    @Override
    public PacketCodec<RegistryByteBuf, ToolUsageRecipe> packetCodec() {
        return createPacketCodec();
    }
}
