package net.myriantics.klaxon.datagen.custom;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.datagen.custom.providers.*;
import net.myriantics.klaxon.datagen.structure.KlaxonStructureTemplatePoolProvider;

import java.util.concurrent.CompletableFuture;

public final class KlaxonDynamicRegistryProvider extends FabricDynamicRegistryProvider {
    private static final String id = KlaxonCommon.locate("dynamic_registry_provider").toString();

    public KlaxonDynamicRegistryProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider registries, Entries entries) {
        new KlaxonDamageTypeProvider(registries, entries);
        new KlaxonToolUsageRecipeTypeProvider(registries, entries);
        new KlaxonVeinmineGroupProvider(registries, entries);
        new KlaxonWrenchInteractionPredicateProvider(registries, entries);
        new KlaxonExplosiveCatalystBehaviorProvider(registries, entries);
        new KlaxonExplosiveCatalystDefinitionProvider(registries, entries);
        new KlaxonStructureTemplatePoolProvider(registries, entries);
    }

    @Override
    public String getName() {
        return id;
    }
}
