package net.myriantics.klaxon.worldgen.features.hallnox;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record HorizontalHallnoxGrowthFeatureConfig(BlockPredicate replaceableBlocks, Direction horizontalFacing, BlockState stemBlock, BlockState wartBlock, BlockState podBlock) implements FeatureConfiguration {
    public static final Codec<HorizontalHallnoxGrowthFeatureConfig> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            BlockPredicate.CODEC.fieldOf("replaceable_blocks").forGetter(HorizontalHallnoxGrowthFeatureConfig::replaceableBlocks),
                            Direction.CODEC.fieldOf("horizontal_facing").forGetter(HorizontalHallnoxGrowthFeatureConfig::horizontalFacing),
                            BlockState.CODEC.fieldOf("stem_block").forGetter(HorizontalHallnoxGrowthFeatureConfig::stemBlock),
                            BlockState.CODEC.fieldOf("wart_block").forGetter(HorizontalHallnoxGrowthFeatureConfig::wartBlock),
                            BlockState.CODEC.fieldOf("pod_block").forGetter(HorizontalHallnoxGrowthFeatureConfig::podBlock)
                    )
                    .apply(instance, HorizontalHallnoxGrowthFeatureConfig::new)
    );
}
