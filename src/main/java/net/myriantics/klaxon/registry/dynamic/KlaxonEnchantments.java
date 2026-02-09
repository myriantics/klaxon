package net.myriantics.klaxon.registry.dynamic;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.myriantics.klaxon.KlaxonCommon;

public abstract class KlaxonEnchantments {
    public static final RegistryKey<Enchantment> STREAMLINE = register("streamline");

    private static RegistryKey<Enchantment> register(String name) {
        return RegistryKey.of(RegistryKeys.ENCHANTMENT, KlaxonCommon.locate(name));
    }
}
