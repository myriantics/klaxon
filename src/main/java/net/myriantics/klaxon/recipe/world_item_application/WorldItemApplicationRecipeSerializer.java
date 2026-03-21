package net.myriantics.klaxon.recipe.world_item_application;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.myriantics.klaxon.util.KlaxonCodecUtils;

public class WorldItemApplicationRecipeSerializer implements RecipeSerializer<WorldItemApplicationRecipe> {
    private final MapCodec<WorldItemApplicationRecipe> CODEC = RecordCodecBuilder.mapCodec((recipeInstance) -> {
        return recipeInstance.group(
                KlaxonCodecUtils.BLOCK_TAG_CODEC.fieldOf("valid_blocks_tag").forGetter(WorldItemApplicationRecipe::getValidBlockInputs),
                Ingredient.CODEC_NONEMPTY.fieldOf("input_ingredient").forGetter(WorldItemApplicationRecipe::getInputIngredient),
                KlaxonCodecUtils.BLOCK_CODEC.fieldOf("output_block").forGetter(WorldItemApplicationRecipe::getOutputBlock)
        ).apply(recipeInstance, WorldItemApplicationRecipe::new);
    });

    private final StreamCodec<RegistryFriendlyByteBuf, WorldItemApplicationRecipe> PACKET_CODEC = StreamCodec.of(
            WorldItemApplicationRecipeSerializer::write, WorldItemApplicationRecipeSerializer::read
    );

    private static void write(RegistryFriendlyByteBuf buf, WorldItemApplicationRecipe recipe) {
        Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.getInputIngredient());
        KlaxonCodecUtils.BLOCK_TAG_PACKET_CODEC.encode(buf, recipe.getValidBlockInputs());
        KlaxonCodecUtils.BLOCK_PACKET_CODEC.encode(buf, recipe.getOutputBlock());
    }

    private static WorldItemApplicationRecipe read(RegistryFriendlyByteBuf buf) {
        Ingredient inputIngredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
        TagKey<Block> validBlockInputs = KlaxonCodecUtils.BLOCK_TAG_PACKET_CODEC.decode(buf);
        Block resultBlock = KlaxonCodecUtils.BLOCK_PACKET_CODEC.decode(buf);

        return new WorldItemApplicationRecipe(validBlockInputs, inputIngredient, resultBlock);
    }

    @Override
    public MapCodec<WorldItemApplicationRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, WorldItemApplicationRecipe> streamCodec() {
        return PACKET_CODEC;
    }
}
