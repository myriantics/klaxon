package net.myriantics.klaxon.recipe.explosion_conversion;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.myriantics.klaxon.recipe.item_explosion_power.ItemExplosionPowerRecipe;
import net.myriantics.klaxon.util.KlaxonCodecUtils;

public class ExplosionConversionRecipeSerializer implements RecipeSerializer<ExplosionConversionRecipe> {

    private final MapCodec<ExplosionConversionRecipe> CODEC = RecordCodecBuilder.mapCodec((recipeInstance) -> {
        return recipeInstance.group(KlaxonCodecUtils.BLOCK_TAG_CODEC.fieldOf("valid_blocks").forGetter(ExplosionConversionRecipe::getValidBlockInputs),
                KlaxonCodecUtils.BLOCK_TAG_CODEC.fieldOf("valid_conversion_catalysts").forGetter(ExplosionConversionRecipe::getValidConversionCatalysts),
                KlaxonCodecUtils.BLOCK_CODEC.fieldOf("output_block").forGetter(ExplosionConversionRecipe::getOutputBlock)
        ).apply(recipeInstance, ExplosionConversionRecipe::new);
    });

    private final PacketCodec<RegistryByteBuf, ExplosionConversionRecipe> PACKET_CODEC = PacketCodec.ofStatic(
            ExplosionConversionRecipeSerializer::write, ExplosionConversionRecipeSerializer::read
    );

    private static void write(RegistryByteBuf buf, ExplosionConversionRecipe recipe) {
        KlaxonCodecUtils.BLOCK_TAG_PACKET_CODEC.encode(buf, recipe.getValidBlockInputs());
        KlaxonCodecUtils.BLOCK_TAG_PACKET_CODEC.encode(buf, recipe.getValidConversionCatalysts());
        KlaxonCodecUtils.BLOCK_PACKET_CODEC.encode(buf, recipe.getOutputBlock());
    }

    private static ExplosionConversionRecipe read(RegistryByteBuf buf) {
        TagKey<Block> validBlockInputs = KlaxonCodecUtils.BLOCK_TAG_PACKET_CODEC.decode(buf);
        TagKey<Block> validConversionCatalysts = KlaxonCodecUtils.BLOCK_TAG_PACKET_CODEC.decode(buf);
        Block outputBlock = KlaxonCodecUtils.BLOCK_PACKET_CODEC.decode(buf);

        return new ExplosionConversionRecipe(validBlockInputs, validConversionCatalysts, outputBlock);
    }

    @Override
    public MapCodec<ExplosionConversionRecipe> codec() {
        return CODEC;
    }

    @Override
    public PacketCodec<RegistryByteBuf, ExplosionConversionRecipe> packetCodec() {
        return PACKET_CODEC;
    }
}
