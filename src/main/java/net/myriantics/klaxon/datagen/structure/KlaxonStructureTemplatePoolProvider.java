package net.myriantics.klaxon.datagen.structure;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.myriantics.klaxon.datagen.custom.KlaxonDynamicRegistrySubProvider;
import net.myriantics.klaxon.registry.worldgen.KlaxonStructureTemplatePools;

import java.util.List;

public class KlaxonStructureTemplatePoolProvider extends KlaxonDynamicRegistrySubProvider<StructureTemplatePool> {
    public KlaxonStructureTemplatePoolProvider(HolderLookup.Provider wrapperLookup, FabricDynamicRegistryProvider.Entries entries) {
        super(wrapperLookup, entries);
    }

    @Override
    protected void build() {

    }

    public static void bootstrap(BootstrapContext<StructureTemplatePool> structureTemplatePoolBootstrapContext) {
        HolderGetter<StructureTemplatePool> holderGetter = structureTemplatePoolBootstrapContext.lookup(Registries.TEMPLATE_POOL);

        structureTemplatePoolBootstrapContext.register(
                KlaxonStructureTemplatePools.OMINOUS_DEEPSLATE_HALL,
                new StructureTemplatePool(
                        holderGetter.getOrThrow(Pools.EMPTY),
                        List.of(Pair.of(StructurePoolElement.single("klaxon:ominous_deepslate_hall"), 1)),
                        StructureTemplatePool.Projection.RIGID
                )
        );
    }
}
