package net.myriantics.klaxon.worldgen.features.hallnox;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.FeatureConfig;

public record HorizontalDryHallnoxGrowthFeatureConfig(BlockPredicate replaceableBlocks, Direction horizontalFacing, BlockState stemBlock, BlockState wartBlock, BlockState podBlock) implements FeatureConfig {
    public static final Codec<HorizontalDryHallnoxGrowthFeatureConfig> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            BlockPredicate.BASE_CODEC.fieldOf("replaceable_blocks").forGetter(HorizontalDryHallnoxGrowthFeatureConfig::replaceableBlocks),
                            Direction.CODEC.fieldOf("horizontal_facing").forGetter(HorizontalDryHallnoxGrowthFeatureConfig::horizontalFacing),
                            BlockState.CODEC.fieldOf("stem_block").forGetter(HorizontalDryHallnoxGrowthFeatureConfig::stemBlock),
                            BlockState.CODEC.fieldOf("wart_block").forGetter(HorizontalDryHallnoxGrowthFeatureConfig::wartBlock),
                            BlockState.CODEC.fieldOf("pod_block").forGetter(HorizontalDryHallnoxGrowthFeatureConfig::podBlock)
                    )
                    .apply(instance, HorizontalDryHallnoxGrowthFeatureConfig::new)
    );
}
