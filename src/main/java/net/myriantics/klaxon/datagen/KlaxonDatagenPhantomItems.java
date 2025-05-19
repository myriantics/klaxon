package net.myriantics.klaxon.datagen;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;

public class KlaxonDatagenPhantomItems {
    public static final String CREATE_MOD_ID = "create";

    public static final Item CREATE_BRASS_SHEET = registerItem(CREATE_MOD_ID, "brass_sheet");
    public static final Item CREATE_BRASS_INGOT = registerItem(CREATE_MOD_ID, "brass_ingot");
    public static final Item CREATE_PRECISION_MECHANISM = registerItem(CREATE_MOD_ID, "precision_mechanism");

    // do not EVER call this in the actual mod - just for datagen
    public static void registerPhantomItemsForDatagen() {
        KlaxonCommon.LOGGER.warn("Registered phantom items for Datagen. If you are a player and you see this in your logs, PLEASE make a github issue.");
    };

    private static Item registerItem(String modId, String name) {
        return Registry.register(Registries.ITEM, Identifier.of(modId, name), new Item(new Item.Settings()));
    }
}
