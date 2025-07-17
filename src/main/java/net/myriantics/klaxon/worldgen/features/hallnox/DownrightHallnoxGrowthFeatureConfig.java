package net.myriantics.klaxon.worldgen.features.hallnox;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.FeatureConfig;

public record DownrightHallnoxGrowthFeatureConfig(BlockPredicate replaceableBlocks, int maxDepth, BlockState denseStemBlock, BlockState stemBlock, BlockState wartBlock, BlockState podBlock) implements FeatureConfig {
    public static final Codec<DownrightHallnoxGrowthFeatureConfig> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            BlockPredicate.BASE_CODEC.fieldOf("replaceable_blocks").forGetter(DownrightHallnoxGrowthFeatureConfig::replaceableBlocks),
                            Codecs.NONNEGATIVE_INT.fieldOf("max_depth").forGetter(DownrightHallnoxGrowthFeatureConfig::maxDepth),
                            BlockState.CODEC.fieldOf("dense_stem_block").forGetter(DownrightHallnoxGrowthFeatureConfig::denseStemBlock),
                            BlockState.CODEC.fieldOf("stem_block").forGetter(DownrightHallnoxGrowthFeatureConfig::stemBlock),
                            BlockState.CODEC.fieldOf("wart_block").forGetter(DownrightHallnoxGrowthFeatureConfig::wartBlock),
                            BlockState.CODEC.fieldOf("pod_block").forGetter(DownrightHallnoxGrowthFeatureConfig::podBlock)
                    )
                    .apply(instance, DownrightHallnoxGrowthFeatureConfig::new)
    );
}
