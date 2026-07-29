package net.myriantics.klaxon.registry.worldgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.myriantics.klaxon.KlaxonCommon;

public abstract class KlaxonStructureSets {

    public static final ResourceKey<StructureSet> OMINOUS_DEEPSLATE_HALLS = register("ominous_deepslate_halls");

    private static ResourceKey<StructureSet> register(String name) {
        return ResourceKey.create(Registries.STRUCTURE_SET, KlaxonCommon.locate(name));
    }
}
