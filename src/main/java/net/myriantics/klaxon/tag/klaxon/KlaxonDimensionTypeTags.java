package net.myriantics.klaxon.tag.klaxon;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.dimension.DimensionType;
import net.myriantics.klaxon.KlaxonCommon;

public abstract class KlaxonDimensionTypeTags {

    public static final TagKey<DimensionType> BLOCKS_BEDLIKE_EXPLOSIVE_CATALYSTS = create("blocks_bedlike_explosive_catalysts");
    public static final TagKey<DimensionType> BLOCKS_RESPAWN_ANCHORLIKE_EXPLOSIVE_CATALYSTS = create("blocks_respawn_anchorlike_explosive_catalysts");

    private static TagKey<DimensionType> create(String name) {
        return TagKey.create(Registries.DIMENSION_TYPE, KlaxonCommon.locate(name));
    }
}
