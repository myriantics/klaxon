package net.myriantics.klaxon.tag.klaxon;

import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.myriantics.klaxon.KlaxonCommon;

public abstract class KlaxonDamageTypeTags {
    public static TagKey<DamageType> CLEAVING_DAMAGE = createTag("cleaving_damage");

    private static TagKey<DamageType> createTag(String name) {
        return TagKey.of(RegistryKeys.DAMAGE_TYPE, KlaxonCommon.locate(name));
    }
}
