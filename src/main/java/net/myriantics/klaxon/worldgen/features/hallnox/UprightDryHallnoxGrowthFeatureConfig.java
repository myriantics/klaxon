package net.myriantics.klaxon.worldgen.features.hallnox;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.FeatureConfig;

public record UprightDryHallnoxGrowthFeatureConfig(BlockPredicate replaceableBlocks, int maxHeight) implements FeatureConfig {
    public static final Codec<UprightDryHallnoxGrowthFeatureConfig> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    BlockPredicate.BASE_CODEC.fieldOf("replaceable_blocks").forGetter(UprightDryHallnoxGrowthFeatureConfig::replaceableBlocks),
                    Codecs.NONNEGATIVE_INT.fieldOf("max_height").forGetter(UprightDryHallnoxGrowthFeatureConfig::maxHeight)
            )
            .apply(instance, UprightDryHallnoxGrowthFeatureConfig::new)
    );
}
