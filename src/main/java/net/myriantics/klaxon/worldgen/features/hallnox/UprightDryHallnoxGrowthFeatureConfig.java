package net.myriantics.klaxon.worldgen.features.hallnox;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.FeatureConfig;

public record UprightDryHallnoxGrowthFeatureConfig(BlockPredicate replaceableBlocks) implements FeatureConfig {
    public static final Codec<UprightDryHallnoxGrowthFeatureConfig> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    BlockPredicate.BASE_CODEC.fieldOf("replaceable_blocks").forGetter(UprightDryHallnoxGrowthFeatureConfig::replaceableBlocks)
            )
            .apply(instance, UprightDryHallnoxGrowthFeatureConfig::new)
    );
}
