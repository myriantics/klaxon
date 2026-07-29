package net.myriantics.klaxon.datagen.structure;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.myriantics.klaxon.registry.worldgen.KlaxonStructureSets;
import net.myriantics.klaxon.registry.worldgen.KlaxonStructures;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class KlaxonStructureSetProvider extends FabricDynamicRegistryProvider {
    public KlaxonStructureSetProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    public static void bootstrap(BootstrapContext<StructureSet> context) {
        HolderGetter<Structure> structureHolderGetter = context.lookup(Registries.STRUCTURE);
        HolderGetter<Biome> biomeHolderGetter = context.lookup(Registries.BIOME);
        context.register(
                KlaxonStructureSets.OMINOUS_DEEPSLATE_HALLS,
                new StructureSet(
                        List.of(
                                StructureSet.entry(structureHolderGetter.getOrThrow(KlaxonStructures.OMINOUS_DEEPSLATE_HALL))
                        ),
                        new RandomSpreadStructurePlacement(
                                20, 6, RandomSpreadType.LINEAR, 67676769
                        )
                )
        );
    }

    @Override
    protected void configure(HolderLookup.Provider registries, Entries entries) {
        entries.addAll(registries.lookupOrThrow(Registries.STRUCTURE_SET));
    }

    @Override
    public String getName() {
        return "";
    }
}
