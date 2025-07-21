package net.myriantics.klaxon.registry.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.util.DamageSourceMixinAccess;

import java.util.Optional;

public abstract class KlaxonDamageTypes {

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

    // to be added - will be used with Coring Drill
    public static final RegistryKey<DamageType> MINCING =
            createDamageType("mincing");
    public static final RegistryKey<DamageType> LOBOTOMY =
            createDamageType("lobotomy");

    private static RegistryKey<DamageType> createDamageType(String name) {
        return RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(KlaxonCommon.MOD_ID, name));
    }

    public static DamageSource getAttackingDamageSource(Entity attacker, RegistryKey<DamageType> damageType) {
        return new DamageSource(attacker.getWorld().getDamageSources().registry.entryOf(damageType), attacker);
    }

    /**
     * Utility method to swap out damage types of damage sources.
     *
     * @param damageSource The damage source that you're overwriting the damage type of.
     * @param damageType   The damage type key to write to the damage source. Nothing happens if key is invalid.
     */
    public static void modifyDamageSourceType(DamageSource damageSource, RegistryKey<DamageType> damageType) {
        if (damageSource.getSource() instanceof Entity attacker) {
            Optional<RegistryEntry.Reference<DamageType>> entry = attacker.getWorld().getDamageSources().registry.getEntry(damageType);
            if (entry.isPresent()) ((DamageSourceMixinAccess) damageSource).klaxon$setDamageType(entry.get());
        }
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Damage Types!");
    }
}
