package net.myriantics.klaxon.registry.minecraft;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;

public class KlaxonDamageTypes {

    public static final RegistryKey<DamageType> HAMMER_BONKING =
            createDamageType("hammer_bonking");
    public static final RegistryKey<DamageType> HAMMER_WALLOPING =
            createDamageType("hammer_walloping");
    public static final RegistryKey<DamageType> CLEAVING =
            createDamageType("cleaving");
    public static final RegistryKey<DamageType> FLINT_AND_STEEELING =
            createDamageType("flint_and_steeeling");

    private static RegistryKey<DamageType> createDamageType(String name) {
        return RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(KlaxonCommon.MOD_ID, name));
    }

    public static DamageSource getAttackingDamageSource(Entity attacker, RegistryKey<DamageType> damageType) {
        return new DamageSource(attacker.getWorld().getDamageSources().registry.entryOf(damageType), attacker);
    }

    public static void registerModDamageTypes() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Damage Types!");
    }
}
