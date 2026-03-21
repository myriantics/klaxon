package net.myriantics.klaxon.recipe.nether_reaction;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.myriantics.klaxon.recipe.BlockIngredient;
import net.myriantics.klaxon.util.KlaxonCodecUtils;

public class NetherReactionRecipeSerializer implements RecipeSerializer<NetherReactionRecipe> {

    private final MapCodec<NetherReactionRecipe> CODEC = RecordCodecBuilder.mapCodec((recipeInstance) -> {
        return recipeInstance.group(
                BlockIngredient.DISALLOW_EMPTY_CODEC.fieldOf("block_ingredient").forGetter(NetherReactionRecipe::getBlockIngredient),
                KlaxonCodecUtils.BLOCK_CODEC.fieldOf("output_block").forGetter(NetherReactionRecipe::getOutputBlock)
        ).apply(recipeInstance, NetherReactionRecipe::new);
    });

    private final StreamCodec<RegistryFriendlyByteBuf, NetherReactionRecipe> PACKET_CODEC = StreamCodec.of(
            NetherReactionRecipeSerializer::write, NetherReactionRecipeSerializer::read
    );

    private static void write(RegistryFriendlyByteBuf buf, NetherReactionRecipe recipe) {
        BlockIngredient.PACKET_CODEC.encode(buf, recipe.getBlockIngredient());
        KlaxonCodecUtils.BLOCK_PACKET_CODEC.encode(buf, recipe.getOutputBlock());
    }

    private static NetherReactionRecipe read(RegistryFriendlyByteBuf buf) {
        BlockIngredient validBlockInputs = BlockIngredient.PACKET_CODEC.decode(buf);
        Block outputBlock = KlaxonCodecUtils.BLOCK_PACKET_CODEC.decode(buf);

        return new NetherReactionRecipe(validBlockInputs, outputBlock);
    }

    @Override
    public MapCodec<NetherReactionRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, NetherReactionRecipe> streamCodec() {
        return PACKET_CODEC;
    }
}
