package net.myriantics.klaxon.tag.klaxon;

import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.myriantics.klaxon.KlaxonCommon;

public class KlaxonDamageTypeTags {
    private static TagKey<DamageType> createTag(String name) {
        return TagKey.of(RegistryKeys.DAMAGE_TYPE, KlaxonCommon.locate(name));
    }
}
