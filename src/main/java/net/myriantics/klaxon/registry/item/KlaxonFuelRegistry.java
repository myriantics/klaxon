package net.myriantics.klaxon.registry.item;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.myriantics.klaxon.KlaxonCommon;

public abstract class KlaxonFuelRegistry {

    static {
        add(KlaxonItems.FRACTURED_COAL, 400);
        add(KlaxonItems.FRACTURED_CHARCOAL, 400);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Furnace Fuels!");
    }

    private static void add(Holder<Item> itemHolder, int burnTimeTicks) {
        add(itemHolder.value(), burnTimeTicks);
    }

    private static void add(ItemLike item, int burnTimeTicks) {
        FuelRegistry.INSTANCE.add(item, burnTimeTicks);
    }
}
