package net.myriantics.klaxon.worldgen.features.hallnox;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.Direction;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.FeatureConfig;

public record HorizontalDryHallnoxGrowthFeatureConfig(BlockPredicate replaceableBlocks, Direction horizontalFacing) implements FeatureConfig {
    public static final Codec<HorizontalDryHallnoxGrowthFeatureConfig> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            BlockPredicate.BASE_CODEC.fieldOf("replaceable_blocks").forGetter(HorizontalDryHallnoxGrowthFeatureConfig::replaceableBlocks),
                            Direction.CODEC.fieldOf("horizontal_facing").forGetter(HorizontalDryHallnoxGrowthFeatureConfig::horizontalFacing)
                    )
                    .apply(instance, HorizontalDryHallnoxGrowthFeatureConfig::new)
    );
}
