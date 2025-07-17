package net.myriantics.klaxon.worldgen.features.hallnox;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.Direction;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.FeatureConfig;

public record UpsideDownHallnoxGrowthFeatureConfig(BlockPredicate replaceableBlocks, int maxDepth, BlockState denseStemBlock, BlockState stemBlock, BlockState wartBlock, BlockState podBlock) implements FeatureConfig {
    public static final Codec<UpsideDownHallnoxGrowthFeatureConfig> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            BlockPredicate.BASE_CODEC.fieldOf("replaceable_blocks").forGetter(UpsideDownHallnoxGrowthFeatureConfig::replaceableBlocks),
                            Codecs.NONNEGATIVE_INT.fieldOf("max_depth").forGetter(UpsideDownHallnoxGrowthFeatureConfig::maxDepth),
                            BlockState.CODEC.fieldOf("dense_stem_block").forGetter(UpsideDownHallnoxGrowthFeatureConfig::denseStemBlock),
                            BlockState.CODEC.fieldOf("stem_block").forGetter(UpsideDownHallnoxGrowthFeatureConfig::stemBlock),
                            BlockState.CODEC.fieldOf("wart_block").forGetter(UpsideDownHallnoxGrowthFeatureConfig::wartBlock),
                            BlockState.CODEC.fieldOf("pod_block").forGetter(UpsideDownHallnoxGrowthFeatureConfig::podBlock)
                    )
                    .apply(instance, UpsideDownHallnoxGrowthFeatureConfig::new)
    );
}
