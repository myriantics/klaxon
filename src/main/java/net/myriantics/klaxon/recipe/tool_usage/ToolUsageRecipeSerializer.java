package net.myriantics.klaxon.recipe.tool_usage;

import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.sound.SoundEvent;
import net.myriantics.klaxon.util.KlaxonCodecUtils;

public class ToolUsageRecipeSerializer implements RecipeSerializer<AbstractToolUsageRecipe> {
    private final Function3<Ingredient, ItemStack, SoundEvent, AbstractToolUsageRecipe> recipeConstructor;

    public ToolUsageRecipeSerializer(Function3<Ingredient, ItemStack, SoundEvent, AbstractToolUsageRecipe> function) {
        this.recipeConstructor = function;
    }

    private MapCodec<AbstractToolUsageRecipe> createCodec() {
        return RecordCodecBuilder.mapCodec(recipeInstance -> {
            return recipeInstance.group(
                    Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("input_ingredient").forGetter(AbstractToolUsageRecipe::getInputIngredient),
                    ItemStack.OPTIONAL_CODEC.fieldOf("output_stack").forGetter(AbstractToolUsageRecipe::getOutputStack),
                    KlaxonCodecUtils.OPTIONAL_SOUND_EVENT_CODEC.fieldOf("sound_override").forGetter(AbstractToolUsageRecipe::getSoundOverride)
            ).apply(recipeInstance, (recipeConstructor));
        });
    }

    private PacketCodec<RegistryByteBuf, AbstractToolUsageRecipe> createPacketCodec() {
        return PacketCodec.of(
                ((value, buf) -> this.write(buf, value)),
                (this::read)
        );
    }

    private AbstractToolUsageRecipe read(RegistryByteBuf buf) {
        Ingredient ingredient = Ingredient.PACKET_CODEC.decode(buf);
        ItemStack output = ItemStack.OPTIONAL_PACKET_CODEC.decode(buf);
        SoundEvent soundOverride = KlaxonCodecUtils.OPTIONAL_SOUND_EVENT_PACKET_CODEC.decode(buf);

        return recipeConstructor.apply(ingredient, output, soundOverride);
    }

    private void write(RegistryByteBuf buf, AbstractToolUsageRecipe recipe) {
        Ingredient.PACKET_CODEC.encode(buf, recipe.getInputIngredient());
        ItemStack.OPTIONAL_PACKET_CODEC.encode(buf, recipe.getResult(buf.getRegistryManager()));
        KlaxonCodecUtils.OPTIONAL_SOUND_EVENT_PACKET_CODEC.encode(buf, recipe.getSoundOverride());
    }

    @Override
    public MapCodec<AbstractToolUsageRecipe> codec() {
        return createCodec();
    }

    @Override
    public PacketCodec<RegistryByteBuf, AbstractToolUsageRecipe> packetCodec() {
        return createPacketCodec();
    }
}
