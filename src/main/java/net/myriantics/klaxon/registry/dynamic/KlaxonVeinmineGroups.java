package net.myriantics.klaxon.registry.dynamic;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.mechanics.grapple_winch.VeinmineGroup;
import net.myriantics.klaxon.registry.KlaxonRegistryKeys;

public abstract class KlaxonVeinmineGroups {
    public static final RegistryKey<VeinmineGroup> GLASS = register("glass");
    public static final RegistryKey<VeinmineGroup> GLOWSTONE = register("glowstone");
    public static final RegistryKey<VeinmineGroup> ICE = register("ice");
    public static final RegistryKey<VeinmineGroup> CHORUS = register("chorus");

    private static RegistryKey<VeinmineGroup> register(String name) {
        return RegistryKey.of(KlaxonRegistryKeys.VEINMINE_GROUP, KlaxonCommon.locate(name));
    }
}
