package net.myriantics.klaxon.worldgen.features.hallnox;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.FeatureConfig;

public record UprightHallnoxGrowthFeatureConfig(BlockPredicate replaceableBlocks, int maxHeight, BlockState denseStemBlock, BlockState stemBlock, BlockState wartBlock, BlockState podBlock) implements FeatureConfig {
    public static final Codec<UprightHallnoxGrowthFeatureConfig> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            BlockPredicate.BASE_CODEC.fieldOf("replaceable_blocks").forGetter(UprightHallnoxGrowthFeatureConfig::replaceableBlocks),
                            Codecs.NONNEGATIVE_INT.fieldOf("max_height").forGetter(UprightHallnoxGrowthFeatureConfig::maxHeight),
                            BlockState.CODEC.fieldOf("stem_block").forGetter(UprightHallnoxGrowthFeatureConfig::stemBlock),
                            BlockState.CODEC.fieldOf("dense_stem_block").forGetter(UprightHallnoxGrowthFeatureConfig::denseStemBlock),
                            BlockState.CODEC.fieldOf("wart_block").forGetter(UprightHallnoxGrowthFeatureConfig::wartBlock),
                            BlockState.CODEC.fieldOf("pod_block").forGetter(UprightHallnoxGrowthFeatureConfig::podBlock)
                    )
            .apply(instance, UprightHallnoxGrowthFeatureConfig::new)
    );
}
