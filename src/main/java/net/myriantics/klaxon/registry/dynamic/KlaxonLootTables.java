package net.myriantics.klaxon.registry.dynamic;

import net.minecraft.loot.LootTable;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.myriantics.klaxon.KlaxonCommon;
import org.jetbrains.annotations.Nullable;

public abstract class KlaxonLootTables {
    public static RegistryKey<LootTable> GERALD_SNIFFER_GAMEPLAY = register("gameplay/gerald_sniffer");

    public static boolean isLootTablePresent(@Nullable MinecraftServer server, RegistryKey<LootTable> key) {
        if (server == null) {
            return false;
        }

        return isLootTablePresent(server.getReloadableRegistries().getRegistryManager(), key);
    }

    public static boolean isLootTablePresent(DynamicRegistryManager registryManager, RegistryKey<LootTable> key) {
        return registryManager.get(RegistryKeys.LOOT_TABLE).contains(key);
    }

    private static RegistryKey<LootTable> register(String name) {
        return RegistryKey.of(RegistryKeys.LOOT_TABLE, KlaxonCommon.locate(name));
    }
}
