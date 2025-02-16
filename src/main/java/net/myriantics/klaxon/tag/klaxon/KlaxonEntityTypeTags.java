package net.myriantics.klaxon.tag.klaxon;

import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.myriantics.klaxon.KlaxonCommon;

public class KlaxonEntityTypeTags {
    // klaxon's tags
    public static final TagKey<EntityType<?>> HEAVY_HITTERS =
            createTag("heavy_hitter_entities");
    public static final TagKey<EntityType<?>> FERROMAGNETIC_ENTITIES =
            createTag("ferromagnetic_entities");

    private static TagKey<EntityType<?>> createTag(String name) {
        return TagKey.of(RegistryKeys.ENTITY_TYPE, KlaxonCommon.locate(name));
    }
}
