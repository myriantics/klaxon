package net.myriantics.klaxon.registry.behavior;

import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.item.Item;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.registry.item.KlaxonItems;

public abstract class KlaxonCauldronBehaviors {

    static {
        registerWater(KlaxonItems.CRESTED_STEEL_HELMET, CauldronBehavior.CLEAN_DYEABLE_ITEM);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Cauldron Behaviors!");
    }

    private static void registerWater(Item item, CauldronBehavior behavior) {
        CauldronBehavior.WATER_CAULDRON_BEHAVIOR.map().put(item, behavior);
    }
}
