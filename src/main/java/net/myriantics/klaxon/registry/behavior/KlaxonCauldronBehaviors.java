package net.myriantics.klaxon.registry.behavior;

import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.item.Item;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.registry.item.KlaxonItems;

public abstract class KlaxonCauldronBehaviors {

    static {
        registerWater(KlaxonItems.CRESTED_STEEL_HELMET, CauldronInteraction.DYED_ITEM);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Cauldron Behaviors!");
    }

    private static void registerWater(Item item, CauldronInteraction behavior) {
        CauldronInteraction.WATER.map().put(item, behavior);
    }
}
