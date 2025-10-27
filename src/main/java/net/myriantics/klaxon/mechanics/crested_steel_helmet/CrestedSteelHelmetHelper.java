package net.myriantics.klaxon.mechanics.crested_steel_helmet;

import net.minecraft.entity.passive.SnifferEntity;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;

public abstract class CrestedSteelHelmetHelper {
    public static RegistryKey<LootTable> SNIFFER_DIGGING_CRESTED_STEEL_HELMET_GAMEPLAY = register("gameplay/sniffer_digging_crested_steel_helmet");

    public static boolean validate(SnifferEntity snifferEntity, Text text) {
        if (snifferEntity.getWorld().isClient()) {
            return false;
        }

        if (!testText(text)) {
            return false;
        }

        // make sure the loot table is actually loaded before proccing the sniffer sniffing behavior
        return ((ServerWorld) snifferEntity.getWorld()).getServer().getReloadableRegistries().getRegistryManager().get(RegistryKeys.LOOT_TABLE).contains(SNIFFER_DIGGING_CRESTED_STEEL_HELMET_GAMEPLAY);
    }

    private static boolean testText(Text text) {
        return text.getString().toLowerCase().contains("gerald");
    }

    private static RegistryKey<LootTable> register(String name) {
        return RegistryKey.of(RegistryKeys.LOOT_TABLE, KlaxonCommon.locate(name));
    }
}
