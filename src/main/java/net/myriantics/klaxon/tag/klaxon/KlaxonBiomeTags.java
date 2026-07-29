package net.myriantics.klaxon.tag.klaxon;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.myriantics.klaxon.KlaxonCommon;

public abstract class KlaxonBiomeTags {

    public static final TagKey<Biome> HAS_OMINOUS_DEEPSLATE_HALL = of("has_structure/ominous_deepslate_hall");

    private static TagKey<Biome> of(String name) {
        return TagKey.create(Registries.BIOME, KlaxonCommon.locate(name));
    }
}
