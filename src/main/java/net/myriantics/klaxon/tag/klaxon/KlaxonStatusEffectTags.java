package net.myriantics.klaxon.tag.klaxon;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.myriantics.klaxon.KlaxonCommon;

public abstract class KlaxonStatusEffectTags {

    public static final TagKey<MobEffect> HEAVY_EFFECTS =
            createTag("heavy_effects");
    public static final TagKey<MobEffect> STRENGTHENING_EFFECTS =
            createTag("strengthening_effects");
    public static final TagKey<MobEffect> HASTENING_EFFECTS =
            createTag("hastening_effects");
    public static final TagKey<MobEffect> WEAKENING_EFFECTS =
            createTag("weakening_effects");

    private static TagKey<MobEffect> createTag(String name) {
        return TagKey.create(Registries.MOB_EFFECT, KlaxonCommon.locate(name));
    }
}
