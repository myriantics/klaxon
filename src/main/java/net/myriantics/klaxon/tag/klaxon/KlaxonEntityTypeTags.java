package net.myriantics.klaxon.tag.klaxon;

import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.myriantics.klaxon.KlaxonCommon;

public abstract class KlaxonEntityTypeTags {
    // klaxon's tags
    public static final TagKey<EntityType<?>> HEAVY_HITTERS =
            createTag("heavy_hitter_entities");
    public static final TagKey<EntityType<?>> FERROMAGNETIC_ENTITIES =
            createTag("ferromagnetic_entities");
    public static final TagKey<EntityType<?>> WALLJUMP_MOVABLE_ENTITIES =
            createTag("walljump_movable_entities");

    // entity weight logistics tags
    public static final TagKey<EntityType<?>> HEAVY_ENTITIES =
            createTag("heavy_entities");
    public static final TagKey<EntityType<?>> ULTRA_HEAVY_ENTITIES =
            createTag("ultra_heavy_entities");
    public static final TagKey<EntityType<?>> LIGHT_ENTITIES =
            createTag("light_entities");
    public static final TagKey<EntityType<?>> ULTRA_LIGHT_ENTITIES =
            createTag("ultra_light_entities");

    private static TagKey<EntityType<?>> createTag(String name) {
        return TagKey.of(RegistryKeys.ENTITY_TYPE, KlaxonCommon.locate(name));
    }
}
