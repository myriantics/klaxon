package net.myriantics.klaxon.registry.dynamic;

import net.minecraft.resources.ResourceKey;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.mechanics.grapple_winch.VeinmineGroup;
import net.myriantics.klaxon.registry.KlaxonRegistryKeys;

public abstract class KlaxonVeinmineGroups {
    public static final ResourceKey<VeinmineGroup> GLASS = register("glass");
    public static final ResourceKey<VeinmineGroup> GLOWSTONE = register("glowstone");
    public static final ResourceKey<VeinmineGroup> ICE = register("ice");
    public static final ResourceKey<VeinmineGroup> CHORUS = register("chorus");

    private static ResourceKey<VeinmineGroup> register(String name) {
        return ResourceKey.create(KlaxonRegistryKeys.VEINMINE_GROUP, KlaxonCommon.locate(name));
    }
}
