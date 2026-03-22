package net.myriantics.klaxon.registry.behavior;

import net.minecraft.core.Holder;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.registry.item.KlaxonItems;

public abstract class KlaxonCauldronBehaviors {

    static {
        register(CauldronInteraction.WATER, KlaxonItems.CRESTED_STEEL_HELMET, CauldronInteraction.DYED_ITEM);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Cauldron Behaviors!");
    }

    private static void register(CauldronInteraction.InteractionMap map, Holder<Item> itemHolder, CauldronInteraction interaction) {
        register(map, itemHolder.value(), interaction);
    }

    private static void register(CauldronInteraction.InteractionMap map, ItemLike item, CauldronInteraction interaction) {
        map.map().put(item.asItem(), interaction);
    }
}
