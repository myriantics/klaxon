package net.myriantics.klaxon.registry.worldgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.myriantics.klaxon.KlaxonCommon;

public abstract class KlaxonStructureTemplatePools {

    public static final ResourceKey<StructureTemplatePool> OMINOUS_DEEPSLATE_HALL = of("ominous_deepslate_hall");

    private static ResourceKey<StructureTemplatePool> of(String path) {
        return ResourceKey.create(Registries.TEMPLATE_POOL, KlaxonCommon.locate(path));
    }
}
