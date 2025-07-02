package net.myriantics.klaxon.worldgen.features.hallnox;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.FeatureConfig;

public record HallnoxGrowthFeatureConfig(BlockPredicate replaceableBlocks) implements FeatureConfig {
    public static final Codec<HallnoxGrowthFeatureConfig> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    BlockPredicate.BASE_CODEC.fieldOf("replaceable_blocks").forGetter(HallnoxGrowthFeatureConfig::replaceableBlocks)
            )
            .apply(instance, HallnoxGrowthFeatureConfig::new)
    );
}
