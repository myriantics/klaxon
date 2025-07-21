package net.myriantics.klaxon.recipe.manual_item_application;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.tag.TagKey;
import net.myriantics.klaxon.util.KlaxonCodecUtils;

public class ManualItemApplicationRecipeSerializer implements RecipeSerializer<ManualItemApplicationRecipe> {
    private final MapCodec<ManualItemApplicationRecipe> CODEC = RecordCodecBuilder.mapCodec((recipeInstance) -> {
        return recipeInstance.group(
                KlaxonCodecUtils.BLOCK_TAG_CODEC.fieldOf("valid_blocks").forGetter(ManualItemApplicationRecipe::getValidBlockInputs),
                Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("input").forGetter(ManualItemApplicationRecipe::getInputIngredient),
                KlaxonCodecUtils.BLOCK_CODEC.fieldOf("output_block").forGetter(ManualItemApplicationRecipe::getOutputBlock)
        ).apply(recipeInstance, ManualItemApplicationRecipe::new);
    });

    private final PacketCodec<RegistryByteBuf, ManualItemApplicationRecipe> PACKET_CODEC = PacketCodec.ofStatic(
            ManualItemApplicationRecipeSerializer::write, ManualItemApplicationRecipeSerializer::read
    );

    private static void write(RegistryByteBuf buf, ManualItemApplicationRecipe recipe) {
        Ingredient.PACKET_CODEC.encode(buf, recipe.getInputIngredient());
        KlaxonCodecUtils.BLOCK_TAG_PACKET_CODEC.encode(buf, recipe.getValidBlockInputs());
        KlaxonCodecUtils.BLOCK_PACKET_CODEC.encode(buf, recipe.getOutputBlock());
    }

    private static ManualItemApplicationRecipe read(RegistryByteBuf buf) {
        Ingredient inputIngredient = Ingredient.PACKET_CODEC.decode(buf);
        TagKey<Block> validBlockInputs = KlaxonCodecUtils.BLOCK_TAG_PACKET_CODEC.decode(buf);
        Block resultBlock = KlaxonCodecUtils.BLOCK_PACKET_CODEC.decode(buf);

        return new ManualItemApplicationRecipe(validBlockInputs, inputIngredient, resultBlock);
    }

    @Override
    public MapCodec<ManualItemApplicationRecipe> codec() {
        return CODEC;
    }

    @Override
    public PacketCodec<RegistryByteBuf, ManualItemApplicationRecipe> packetCodec() {
        return PACKET_CODEC;
    }
}
