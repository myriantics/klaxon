package net.myriantics.klaxon.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.myriantics.klaxon.tag.klaxon.KlaxonEntityTypeTags;

import java.util.concurrent.CompletableFuture;

public class KlaxonEntityTypeTagProvider extends FabricTagProvider<EntityType<?>> {

    public KlaxonEntityTypeTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, RegistryKeys.ENTITY_TYPE, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        getOrCreateTagBuilder(KlaxonEntityTypeTags.HEAVY_HITTERS)
                .add(EntityType.VINDICATOR)
                .add(EntityType.PIGLIN_BRUTE)
                // haha funny fox bonk
                .add(EntityType.FOX)
                .add(EntityType.WITHER_SKELETON)
                .add(EntityType.ZOMBIFIED_PIGLIN);
    }
}
