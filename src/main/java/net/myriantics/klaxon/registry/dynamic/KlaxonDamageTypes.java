package net.myriantics.klaxon.registry.dynamic;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.util.DamageSourceMixinAccess;

public abstract class KlaxonDamageTypes {

    // melee
    public static final RegistryKey<DamageType> HAMMER_BONKING =
            createDamageType("hammer_bonking");
    public static final RegistryKey<DamageType> HAMMER_WALLOPING =
            createDamageType("hammer_walloping");
    public static final RegistryKey<DamageType> CLEAVING =
            createDamageType("cleaving");
    public static final RegistryKey<DamageType> FLINT_AND_STEEELING =
            createDamageType("flint_and_steeeling");
    public static final RegistryKey<DamageType> WRENCH_OVERTUNING =
            createDamageType("wrench_overtuning");
    public static final RegistryKey<DamageType> BLUDGEONING =
            createDamageType("bludgeoning");

    // electrical
    public static final RegistryKey<DamageType> GRAPPLE_CABLE_CONDUCTION =
            createDamageType("grapple_cable_conduction");

    // projectile
    public static final RegistryKey<DamageType> GRAPPLING =
            createDamageType("grappling");
    public static final RegistryKey<DamageType> RENDING =
            createDamageType("rending");

    // environmental
    public static final RegistryKey<DamageType> HALLNOX_POD_DOMED =
            createDamageType("hallnox_pod_domed");

    // to be added - will be used with Coring Drill
    public static final RegistryKey<DamageType> MINCING =
            createDamageType("mincing");
    public static final RegistryKey<DamageType> LOBOTOMY =
            createDamageType("lobotomy");

    private static RegistryKey<DamageType> createDamageType(String name) {
        return RegistryKey.of(RegistryKeys.DAMAGE_TYPE, KlaxonCommon.locate(name));
    }

    public static DamageSource getAttackingDamageSource(Entity attacker, RegistryKey<DamageType> damageType) {
        return new DamageSource(attacker.getWorld().getDamageSources().registry.entryOf(damageType), attacker);
    }

    /**
     * Utility method to swap out damage types of damage sources.
     *
     * @param damageSource The damage source that you're overwriting the damage type of.
     * @param entry   The damage type key to write to the damage source. Nothing happens if key is invalid.
     */
    public static void modifyDamageSourceType(DamageSource damageSource, RegistryEntry<DamageType> entry) {
        ((DamageSourceMixinAccess) damageSource).klaxon$setDamageType(entry);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Damage Types!");
    }
}
