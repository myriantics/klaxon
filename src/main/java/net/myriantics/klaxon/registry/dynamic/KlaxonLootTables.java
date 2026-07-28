package net.myriantics.klaxon.registry.dynamic;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.loot.LootTable;
import net.myriantics.klaxon.KlaxonCommon;
import org.jetbrains.annotations.Nullable;

public abstract class KlaxonLootTables {
    public static final ResourceKey<LootTable> GERALD_SNIFFER_GAMEPLAY = register("gameplay/gerald_sniffer");
    public static final ResourceKey<LootTable> OMINOUS_DEEPSLATE_HALL_DROPPER = register("droppers/ominous_deepslate_hall/battle_detector");

    public static boolean isLootTablePresent(@Nullable MinecraftServer server, ResourceKey<LootTable> key) {
        if (server == null) {
            return false;
        }

        return isLootTablePresent(server.reloadableRegistries().get(), key);
    }

    public static boolean isLootTablePresent(RegistryAccess registryManager, ResourceKey<LootTable> key) {
        return registryManager.registryOrThrow(Registries.LOOT_TABLE).containsKey(key);
    }

    private static ResourceKey<LootTable> register(String name) {
        return ResourceKey.create(Registries.LOOT_TABLE, KlaxonCommon.locate(name));
    }
}
