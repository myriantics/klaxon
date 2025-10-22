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

public class WorldItemApplicationRecipeSerializer implements RecipeSerializer<WorldItemApplicationRecipe> {
    private final MapCodec<WorldItemApplicationRecipe> CODEC = RecordCodecBuilder.mapCodec((recipeInstance) -> {
        return recipeInstance.group(
                KlaxonCodecUtils.BLOCK_TAG_CODEC.fieldOf("valid_blocks_tag").forGetter(WorldItemApplicationRecipe::getValidBlockInputs),
                Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("input_ingredient").forGetter(WorldItemApplicationRecipe::getInputIngredient),
                KlaxonCodecUtils.BLOCK_CODEC.fieldOf("output_block").forGetter(WorldItemApplicationRecipe::getOutputBlock)
        ).apply(recipeInstance, WorldItemApplicationRecipe::new);
    });

    private final PacketCodec<RegistryByteBuf, WorldItemApplicationRecipe> PACKET_CODEC = PacketCodec.ofStatic(
            WorldItemApplicationRecipeSerializer::write, WorldItemApplicationRecipeSerializer::read
    );

    private static void write(RegistryByteBuf buf, WorldItemApplicationRecipe recipe) {
        Ingredient.PACKET_CODEC.encode(buf, recipe.getInputIngredient());
        KlaxonCodecUtils.BLOCK_TAG_PACKET_CODEC.encode(buf, recipe.getValidBlockInputs());
        KlaxonCodecUtils.BLOCK_PACKET_CODEC.encode(buf, recipe.getOutputBlock());
    }

    private static WorldItemApplicationRecipe read(RegistryByteBuf buf) {
        Ingredient inputIngredient = Ingredient.PACKET_CODEC.decode(buf);
        TagKey<Block> validBlockInputs = KlaxonCodecUtils.BLOCK_TAG_PACKET_CODEC.decode(buf);
        Block resultBlock = KlaxonCodecUtils.BLOCK_PACKET_CODEC.decode(buf);

        return new WorldItemApplicationRecipe(validBlockInputs, inputIngredient, resultBlock);
    }

    @Override
    public MapCodec<WorldItemApplicationRecipe> codec() {
        return CODEC;
    }

    @Override
    public PacketCodec<RegistryByteBuf, WorldItemApplicationRecipe> packetCodec() {
        return PACKET_CODEC;
    }
}
