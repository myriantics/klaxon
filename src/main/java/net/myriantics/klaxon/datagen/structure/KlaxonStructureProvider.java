package net.myriantics.klaxon.datagen.structure;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockRotProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.myriantics.klaxon.registry.worldgen.KlaxonStructureTemplatePools;
import net.myriantics.klaxon.registry.worldgen.KlaxonStructures;
import net.myriantics.klaxon.tag.klaxon.KlaxonBiomeTags;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class KlaxonStructureProvider extends FabricDynamicRegistryProvider{

    public KlaxonStructureProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    public static void bootstrap(BootstrapContext<Structure> structureBootstrapContext) {
        HolderGetter<Biome> biomeHolderGetter = structureBootstrapContext.lookup(Registries.BIOME);
        HolderGetter<StructureTemplatePool> templatePoolHolderGetter = structureBootstrapContext.lookup(Registries.TEMPLATE_POOL);


        structureBootstrapContext.register(
                KlaxonStructures.OMINOUS_DEEPSLATE_HALL,
                new JigsawStructure(
                        new Structure.StructureSettings.Builder(biomeHolderGetter.getOrThrow(KlaxonBiomeTags.HAS_OMINOUS_DEEPSLATE_HALL))
                                .spawnOverrides(
                                        Arrays.stream(MobCategory.values())
                                                .collect(
                                                        Collectors.toMap(
                                                                mobCategory -> mobCategory, mobCategory -> new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.STRUCTURE, WeightedRandomList.create())
                                                        )
                                                )
                                )
                                .generationStep(GenerationStep.Decoration.UNDERGROUND_STRUCTURES)
                                .terrainAdapation(TerrainAdjustment.NONE)
                                .build(),
                        templatePoolHolderGetter.getOrThrow(KlaxonStructureTemplatePools.OMINOUS_DEEPSLATE_HALL),
                        Optional.empty(),
                        1,
                        UniformHeight.of(VerticalAnchor.absolute(-56), VerticalAnchor.absolute(-30)),
                        false,
                        Optional.empty(),
                        32,
                        List.of(),
                        DimensionPadding.ZERO,
                        LiquidSettings.IGNORE_WATERLOGGING
                )
        );
    }

    @Override
    protected void configure(HolderLookup.Provider registries, Entries entries) {
        entries.addAll(registries.lookupOrThrow(Registries.STRUCTURE));
        entries.addAll(registries.lookupOrThrow(Registries.TEMPLATE_POOL));
    }

    @Override
    public String getName() {
        return "klaxon_structure_balling";
    }
}
