package net.myriantics.klaxon.worldgen.features.hallnox;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record UprightHallnoxGrowthFeatureConfig(BlockPredicate replaceableBlocks, int maxHeight, BlockState denseStemBlock, BlockState stemBlock, BlockState wartBlock, BlockState podBlock) implements FeatureConfiguration {
    public static final Codec<UprightHallnoxGrowthFeatureConfig> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            BlockPredicate.CODEC.fieldOf("replaceable_blocks").forGetter(UprightHallnoxGrowthFeatureConfig::replaceableBlocks),
                            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("max_height").forGetter(UprightHallnoxGrowthFeatureConfig::maxHeight),
                            BlockState.CODEC.fieldOf("stem_block").forGetter(UprightHallnoxGrowthFeatureConfig::stemBlock),
                            BlockState.CODEC.fieldOf("dense_stem_block").forGetter(UprightHallnoxGrowthFeatureConfig::denseStemBlock),
                            BlockState.CODEC.fieldOf("wart_block").forGetter(UprightHallnoxGrowthFeatureConfig::wartBlock),
                            BlockState.CODEC.fieldOf("pod_block").forGetter(UprightHallnoxGrowthFeatureConfig::podBlock)
                    )
            .apply(instance, UprightHallnoxGrowthFeatureConfig::new)
    );
}
