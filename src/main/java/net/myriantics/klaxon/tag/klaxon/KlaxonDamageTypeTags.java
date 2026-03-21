package net.myriantics.klaxon.tag.klaxon;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.myriantics.klaxon.KlaxonCommon;

public abstract class KlaxonDamageTypeTags {
    public static final TagKey<DamageType> CLEAVING_DAMAGE = createTag("cleaving_damage");

    public static final TagKey<DamageType> STREAMLINE_ENCHANTMENT_CANCELS_VELOCITY_UPDATE = createTag("gyro_enchantment_cancels_velocity_update");

    public static final TagKey<DamageType> GRAPPLE_CLAW_DAMAGE_TYPES = createTag("grapple_winch_projectile");
    public static final TagKey<DamageType> ELECTRICAL = createTag("electrical");
    public static final TagKey<DamageType> GRAPPLE_WINCH_CABLE_TRANSMISSIBLE = createTag("grapple_winch_cable_transmissible");

    private static TagKey<DamageType> createTag(String name) {
        return TagKey.create(Registries.DAMAGE_TYPE, KlaxonCommon.locate(name));
    }
}
