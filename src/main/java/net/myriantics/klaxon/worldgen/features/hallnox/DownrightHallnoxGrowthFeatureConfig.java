package net.myriantics.klaxon.worldgen.features.hallnox;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record DownrightHallnoxGrowthFeatureConfig(BlockPredicate replaceableBlocks, int maxDepth, BlockState denseStemBlock, BlockState stemBlock, BlockState wartBlock, BlockState podBlock) implements FeatureConfiguration {
    public DownrightHallnoxGrowthFeatureConfig(BlockPredicate replaceableBlocks, int maxHeight, Block denseStemBlock, Block stemBlock, Block wartBlock, Block podBlock) {
        this(replaceableBlocks, maxHeight, denseStemBlock.defaultBlockState(), stemBlock.defaultBlockState(), wartBlock.defaultBlockState(), podBlock.defaultBlockState());
    }

    public DownrightHallnoxGrowthFeatureConfig(BlockPredicate replaceableBlocks, int maxHeight, Holder<Block> denseStemBlockHolder, Holder<Block> stemBlockHolder, Holder<Block> wartBlockHolder, Holder<Block> podBlockHolder) {
        this(replaceableBlocks, maxHeight, denseStemBlockHolder.value(), stemBlockHolder.value(), wartBlockHolder.value(), podBlockHolder.value());
    }

    public static final Codec<DownrightHallnoxGrowthFeatureConfig> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            BlockPredicate.CODEC.fieldOf("replaceable_blocks").forGetter(DownrightHallnoxGrowthFeatureConfig::replaceableBlocks),
                            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("max_depth").forGetter(DownrightHallnoxGrowthFeatureConfig::maxDepth),
                            BlockState.CODEC.fieldOf("dense_stem_block").forGetter(DownrightHallnoxGrowthFeatureConfig::denseStemBlock),
                            BlockState.CODEC.fieldOf("stem_block").forGetter(DownrightHallnoxGrowthFeatureConfig::stemBlock),
                            BlockState.CODEC.fieldOf("wart_block").forGetter(DownrightHallnoxGrowthFeatureConfig::wartBlock),
                            BlockState.CODEC.fieldOf("pod_block").forGetter(DownrightHallnoxGrowthFeatureConfig::podBlock)
                    )
                    .apply(instance, DownrightHallnoxGrowthFeatureConfig::new)
    );
}
