package net.myriantics.klaxon.tag.klaxon;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.myriantics.klaxon.KlaxonCommon;

public class KlaxonStatusEffectTags {

    public static final TagKey<StatusEffect> HEAVY_STATUS_EFFECTS =
            createTag("heavy_status_effects");

    private static TagKey<StatusEffect> createTag(String name) {
        return TagKey.of(RegistryKeys.STATUS_EFFECT, KlaxonCommon.locate(name));
    }
}
