package net.myriantics.klaxon.registry.worldgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.myriantics.klaxon.KlaxonCommon;

public abstract class KlaxonStructures {

    public static final ResourceKey<Structure> OMINOUS_DEEPSLATE_HALL = of("ominous_deepslate_hall");

    private static ResourceKey<Structure> of(String path) {
        return ResourceKey.create(Registries.STRUCTURE, KlaxonCommon.locate(path));
    }
}
