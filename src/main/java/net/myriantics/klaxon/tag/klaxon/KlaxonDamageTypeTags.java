package net.myriantics.klaxon.tag.klaxon;

import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.myriantics.klaxon.KlaxonCommon;

public abstract class KlaxonDamageTypeTags {
    public static final TagKey<DamageType> CLEAVING_DAMAGE = createTag("cleaving_damage");

    public static final TagKey<DamageType> GRAPPLE_CLAW_DAMAGE_TYPES = createTag("grapple_winch_projectile");
    public static final TagKey<DamageType> ELECTRICAL = createTag("electrical");
    public static final TagKey<DamageType> GRAPPLE_WINCH_CABLE_TRANSMISSIBLE = createTag("grapple_winch_cable_transmissible");

    private static TagKey<DamageType> createTag(String name) {
        return TagKey.of(RegistryKeys.DAMAGE_TYPE, KlaxonCommon.locate(name));
    }
}
