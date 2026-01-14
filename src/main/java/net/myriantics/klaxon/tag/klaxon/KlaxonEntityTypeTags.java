package net.myriantics.klaxon.tag.klaxon;

import net.minecraft.entity.Entity;
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
    public static final TagKey<EntityType<?>> LIGHT_ENTITIES =
            createTag("light_entities");

    // allowlist / denylist tags
    public static final TagKey<EntityType<?>> GRAPPLE_CLAW_HOOKING_DENYLIST =
            createTag("grapple_claw_hooking_denylist");
    public static final TagKey<EntityType<?>> GRAPPLE_CLAW_COLLISION_DENYLIST =
            createTag("grapple_claw_collision_denylist");

    private static TagKey<EntityType<?>> createTag(String name) {
        return TagKey.of(RegistryKeys.ENTITY_TYPE, KlaxonCommon.locate(name));
    }
}
