package net.myriantics.klaxon.datagen.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalEntityTypeTags;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.EntityTypeTags;
import net.myriantics.klaxon.registry.entity.KlaxonEntityTypes;
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
        getOrCreateTagBuilder(KlaxonEntityTypeTags.HEAVY_ENTITIES)
                .forceAddTag(ConventionalEntityTypeTags.BOSSES)
                .forceAddTag(ConventionalEntityTypeTags.CAPTURING_NOT_SUPPORTED)
                .forceAddTag(ConventionalEntityTypeTags.TELEPORTING_NOT_SUPPORTED)
                .add(EntityType.SHULKER)
                .add(EntityType.IRON_GOLEM)
                .add(EntityType.RAVAGER)
                .add(EntityType.ENDER_DRAGON)
                .add(EntityType.WITHER)
                .add(EntityType.GIANT)
                .add(EntityType.WARDEN)
                .add(EntityType.AREA_EFFECT_CLOUD);
        getOrCreateTagBuilder(KlaxonEntityTypeTags.LIGHT_ENTITIES)
                .forceAddTag(EntityTypeTags.POWDER_SNOW_WALKABLE_MOBS)
                .forceAddTag(EntityTypeTags.SKELETONS)
                .add(EntityType.SNOW_GOLEM)
                .add(EntityType.GHAST)
                .add(EntityType.ITEM)
                .add(EntityType.ENDER_PEARL)
                .add(EntityType.EYE_OF_ENDER);

        // allow / denylist tags
        getOrCreateTagBuilder(KlaxonEntityTypeTags.GRAPPLE_CLAW_HOOKING_DENYLIST)
                .add(KlaxonEntityTypes.GRAPPLE_CLAW);
    }
}
