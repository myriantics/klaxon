package net.myriantics.klaxon.worldgen.features.hallnox;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record DownrightHallnoxGrowthFeatureConfig(BlockPredicate replaceableBlocks, int maxDepth, BlockState denseStemBlock, BlockState stemBlock, BlockState wartBlock, BlockState podBlock) implements FeatureConfiguration {
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
