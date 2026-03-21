package net.myriantics.klaxon.registry.dynamic;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.myriantics.klaxon.KlaxonCommon;

public abstract class KlaxonEnchantments {
    public static final ResourceKey<Enchantment> STREAMLINE = register("streamline");

    private static ResourceKey<Enchantment> register(String name) {
        return ResourceKey.create(Registries.ENCHANTMENT, KlaxonCommon.locate(name));
    }
}
