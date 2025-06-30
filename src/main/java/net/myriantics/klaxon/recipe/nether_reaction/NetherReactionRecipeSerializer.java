package net.myriantics.klaxon.recipe.nether_reaction;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.tag.TagKey;
import net.myriantics.klaxon.util.KlaxonCodecUtils;

public class NetherReactionRecipeSerializer implements RecipeSerializer<NetherReactionRecipe> {

    private final MapCodec<NetherReactionRecipe> CODEC = RecordCodecBuilder.mapCodec((recipeInstance) -> {
        return recipeInstance.group(KlaxonCodecUtils.BLOCK_TAG_CODEC.fieldOf("valid_blocks").forGetter(NetherReactionRecipe::getValidBlockInputs),
                KlaxonCodecUtils.BLOCK_CODEC.fieldOf("output_block").forGetter(NetherReactionRecipe::getOutputBlock)
        ).apply(recipeInstance, NetherReactionRecipe::new);
    });

    private final PacketCodec<RegistryByteBuf, NetherReactionRecipe> PACKET_CODEC = PacketCodec.ofStatic(
            NetherReactionRecipeSerializer::write, NetherReactionRecipeSerializer::read
    );

    private static void write(RegistryByteBuf buf, NetherReactionRecipe recipe) {
        KlaxonCodecUtils.BLOCK_TAG_PACKET_CODEC.encode(buf, recipe.getValidBlockInputs());
        KlaxonCodecUtils.BLOCK_PACKET_CODEC.encode(buf, recipe.getOutputBlock());
    }

    private static NetherReactionRecipe read(RegistryByteBuf buf) {
        TagKey<Block> validBlockInputs = KlaxonCodecUtils.BLOCK_TAG_PACKET_CODEC.decode(buf);
        Block outputBlock = KlaxonCodecUtils.BLOCK_PACKET_CODEC.decode(buf);

        return new NetherReactionRecipe(validBlockInputs, outputBlock);
    }

    @Override
    public MapCodec<NetherReactionRecipe> codec() {
        return CODEC;
    }

    @Override
    public PacketCodec<RegistryByteBuf, NetherReactionRecipe> packetCodec() {
        return PACKET_CODEC;
    }
}
