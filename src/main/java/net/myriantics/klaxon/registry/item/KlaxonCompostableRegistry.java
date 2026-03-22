package net.myriantics.klaxon.registry.item;

import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.myriantics.klaxon.KlaxonCommon;

public abstract class KlaxonCompostableRegistry {
    static {
        register(KlaxonItems.HALLNOX_POD, 1f);
        register(KlaxonItems.HALLNOX_WART_BLOCK, 0.95f);
    }

    private static void register(Holder<Item> itemHolder, float chance) {
        register(itemHolder.value(), chance);
    }

    private static void register(ItemLike item, float chance) {
        CompostingChanceRegistry.INSTANCE.add(item, chance);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Compostable Items!");
    }
}
