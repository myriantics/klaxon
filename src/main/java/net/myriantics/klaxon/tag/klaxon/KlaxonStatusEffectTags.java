package net.myriantics.klaxon.tag.klaxon;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.myriantics.klaxon.KlaxonCommon;

public abstract class KlaxonStatusEffectTags {

    public static final TagKey<StatusEffect> HEAVY_EFFECTS =
            createTag("heavy_effects");
    public static final TagKey<StatusEffect> STRENGTHENING_EFFECTS =
            createTag("strengthening_effects");
    public static final TagKey<StatusEffect> HASTENING_EFFECTS =
            createTag("hastening_effects");
    public static final TagKey<StatusEffect> WEAKENING_EFFECTS =
            createTag("weakening_effects");

    private static TagKey<StatusEffect> createTag(String name) {
        return TagKey.of(RegistryKeys.STATUS_EFFECT, KlaxonCommon.locate(name));
    }
}
