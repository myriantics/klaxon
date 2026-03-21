package net.myriantics.klaxon.recipe.tool_usage;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.myriantics.klaxon.util.KlaxonCodecUtils;

public class ToolUsageRecipeSerializer implements RecipeSerializer<ToolUsageRecipe> {
    public ToolUsageRecipeSerializer() {
    }

    private MapCodec<ToolUsageRecipe> createCodec() {
        return RecordCodecBuilder.mapCodec(recipeInstance -> recipeInstance.group(
                ToolUsageRecipeType.KEY_CODEC.fieldOf("tool_usage_recipe_type").forGetter(ToolUsageRecipe::getTypeKey),
                Ingredient.CODEC_NONEMPTY.fieldOf("input_ingredient").forGetter(ToolUsageRecipe::getInputIngredient),
                ItemStack.OPTIONAL_CODEC.fieldOf("output_stack").forGetter(ToolUsageRecipe::getOutputStack),
                KlaxonCodecUtils.OPTIONAL_SOUND_EVENT_CODEC.fieldOf("sound_override").forGetter(ToolUsageRecipe::getSoundOverride)
        ).apply(recipeInstance, ToolUsageRecipe::new));
    }

    private StreamCodec<RegistryFriendlyByteBuf, ToolUsageRecipe> createPacketCodec() {
        return StreamCodec.ofMember(
                ((value, buf) -> this.write(buf, value)),
                (this::read)
        );
    }

    private ToolUsageRecipe read(RegistryFriendlyByteBuf buf) {
        ResourceKey<ToolUsageRecipeType> typeKey = ToolUsageRecipeType.KEY_PACKET_CODEC.decode(buf);
        Ingredient ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
        ItemStack output = ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
        SoundEvent soundOverride = KlaxonCodecUtils.OPTIONAL_SOUND_EVENT_PACKET_CODEC.decode(buf);

        return new ToolUsageRecipe(typeKey, ingredient, output, soundOverride);
    }

    private void write(RegistryFriendlyByteBuf buf, ToolUsageRecipe recipe) {
        ToolUsageRecipeType.KEY_PACKET_CODEC.encode(buf, recipe.getTypeKey());
        Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.getInputIngredient());
        ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, recipe.getResultItem(buf.registryAccess()));
        KlaxonCodecUtils.OPTIONAL_SOUND_EVENT_PACKET_CODEC.encode(buf, recipe.getSoundOverride());
    }

    @Override
    public MapCodec<ToolUsageRecipe> codec() {
        return createCodec();
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ToolUsageRecipe> streamCodec() {
        return createPacketCodec();
    }
}
