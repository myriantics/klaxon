package net.myriantics.klaxon.util;

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

    private static RegistryKey<DamageType> createDamageType(String name) {
        return RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(KlaxonCommon.MOD_ID, name));
    }

    public static DamageSource hammerBonking(Entity attacker) {
        return new DamageSource(attacker.getWorld().getDamageSources().registry.entryOf(HAMMER_BONKING), attacker);
    }
    public static DamageSource hammerWalloping(Entity attacker) {
        return new DamageSource(attacker.getWorld().getDamageSources().registry.entryOf(HAMMER_WALLOPING), attacker);
    }

    public static void registerModDamageTypes() {
        KlaxonCommon.LOGGER.info("NOW THAT'S A LOTTA DAMAGE");
    }
}
