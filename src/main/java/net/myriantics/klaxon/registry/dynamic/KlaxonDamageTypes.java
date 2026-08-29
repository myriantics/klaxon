package net.myriantics.klaxon.registry.dynamic;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.util.DamageSourceMixinAccess;

public abstract class KlaxonDamageTypes {

    // melee
    public static final ResourceKey<DamageType> HAMMER_BONKING =
            createDamageType("hammer_bonking");
    public static final ResourceKey<DamageType> HAMMER_WALLOPING =
            createDamageType("hammer_walloping");
    public static final ResourceKey<DamageType> CLEAVING =
            createDamageType("cleaving");
    public static final ResourceKey<DamageType> FLINT_AND_STEEELING =
            createDamageType("flint_and_steeeling");
    public static final ResourceKey<DamageType> WRENCH_OVERTUNING =
            createDamageType("wrench_overtuning");
    public static final ResourceKey<DamageType> BLUDGEONING =
            createDamageType("bludgeoning");

    // electrical
    public static final ResourceKey<DamageType> GRAPPLE_CABLE_CONDUCTION =
            createDamageType("grapple_cable_conduction");

    // projectile
    public static final ResourceKey<DamageType> GRAPPLING =
            createDamageType("grappling");
    public static final ResourceKey<DamageType> RENDING =
            createDamageType("rending");

    // environmental
    public static final ResourceKey<DamageType> HALLNOX_POD_DOMED =
            createDamageType("hallnox_pod_domed");

    // machine
    public static final ResourceKey<DamageType> FORCEFUL_EXHAUST =
            createDamageType("forceful_exhaust");

    // mob
    public static final ResourceKey<DamageType> WIND_RAMMING =
            createDamageType("wind_ramming");

    // to be added - will be used with Coring Drill
    public static final ResourceKey<DamageType> MINCING =
            createDamageType("mincing");
    public static final ResourceKey<DamageType> LOBOTOMY =
            createDamageType("lobotomy");

    private static ResourceKey<DamageType> createDamageType(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, KlaxonCommon.locate(name));
    }

    public static DamageSource getAttackingDamageSource(Entity attacker, ResourceKey<DamageType> damageType) {
        return new DamageSource(attacker.level().damageSources().damageTypes.getHolderOrThrow(damageType), attacker);
    }

    /**
     * Utility method to swap out damage types of damage sources.
     *
     * @param damageSource The damage source that you're overwriting the damage type of.
     * @param entry   The damage type key to write to the damage source. Nothing happens if key is invalid.
     */
    public static void modifyDamageSourceType(DamageSource damageSource, Holder<DamageType> entry) {
        ((DamageSourceMixinAccess) damageSource).klaxon$setDamageType(entry);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Damage Types!");
    }
}
