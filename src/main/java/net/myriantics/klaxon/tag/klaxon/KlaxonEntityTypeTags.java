package net.myriantics.klaxon.tag.klaxon;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.myriantics.klaxon.KlaxonCommon;

public abstract class KlaxonEntityTypeTags {
    // klaxon's tags
    public static final TagKey<EntityType<?>> HEAVY_HITTERS =
            createTag("heavy_hitter_entities");
    public static final TagKey<EntityType<?>> FERROMAGNETIC_ENTITIES =
            createTag("ferromagnetic_entities");
    public static final TagKey<EntityType<?>> WALLJUMP_MOVABLE_ENTITIES =
            createTag("walljump_movable_entities");
    public static final TagKey<EntityType<?>> GRAPPLE_CLAW_DRAGGABLE =
            createTag("grapple_claw_draggable");

    // entity weight logistics tags
    public static final TagKey<EntityType<?>> HEAVY_ENTITIES =
            createTag("heavy_entities");
    public static final TagKey<EntityType<?>> LIGHT_ENTITIES =
            createTag("light_entities");

    // allowlist / denylist tags
    /**
     * Entities that cannot be moved by the Grapple Winch of their direct passenger.
     * Put in place to streamline interactions with Boats because they were already jank and this makes them less so.
     */
    public static final TagKey<EntityType<?>> GRAPPLE_WINCH_IMMOVABLE_DIRECT_MOUNTS =
            createTag("grapple_winch_immovable_direct_mounts");
    public static final TagKey<EntityType<?>> GRAPPLE_CLAW_GENTLY_HOOKED_ENTITIES =
            createTag("grapple_claw_gently_hooked_entities");
    public static final TagKey<EntityType<?>> GRAPPLE_CLAW_HOOKING_DENYLIST =
            createTag("grapple_claw_hooking_denylist");
    public static final TagKey<EntityType<?>> GRAPPLE_CLAW_COLLISION_DENYLIST =
            createTag("grapple_claw_collision_denylist");

    private static TagKey<EntityType<?>> createTag(String name) {
        return TagKey.create(Registries.ENTITY_TYPE, KlaxonCommon.locate(name));
    }
}
