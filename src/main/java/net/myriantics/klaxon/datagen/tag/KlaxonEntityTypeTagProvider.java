package net.myriantics.klaxon.datagen.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalEntityTypeTags;
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

        getOrCreateTagBuilder(KlaxonEntityTypeTags.WALLJUMP_MOVABLE_ENTITIES)
                .forceAddTag(ConventionalEntityTypeTags.MINECARTS);

        // weight tags
        getOrCreateTagBuilder(KlaxonEntityTypeTags.ULTRA_HEAVY_ENTITIES)
                .forceAddTag(ConventionalEntityTypeTags.BOSSES)
                .add(EntityType.ENDER_DRAGON)
                .add(EntityType.WITHER)
                .add(EntityType.GIANT);
        getOrCreateTagBuilder(KlaxonEntityTypeTags.ULTRA_LIGHT_ENTITIES)
                .add(EntityType.GHAST);
        getOrCreateTagBuilder(KlaxonEntityTypeTags.HEAVY_ENTITIES)
                .add(EntityType.WARDEN);
        getOrCreateTagBuilder(KlaxonEntityTypeTags.LIGHT_ENTITIES)
                .add(EntityType.ENDERMAN);
    }
}
