package net.myriantics.klaxon.compat;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class KlaxonCompat {
    public static final String CREATE_MOD_ID = "create";

    public static Item CREATE_IRON_SHEET = null;
    public static Item CREATE_COPPER_SHEET = null;
    public static Item CREATE_GOLD_SHEET = null;
    public static Item CREATE_BRASS_SHEET = null;
    public static Item CREATE_BRASS_INGOT = null;
    public static Item CREATE_PRECISION_MECHANISM = null;

    // do not EVER call this in the actual mod - just for datagen
    public static void registerPhantomItemsForDatagen() {
        CREATE_IRON_SHEET = registerItem(CREATE_MOD_ID, "iron_sheet");
        CREATE_COPPER_SHEET = registerItem(CREATE_MOD_ID, "copper_sheet");
        CREATE_BRASS_SHEET = registerItem(CREATE_MOD_ID, "brass_sheet");
        CREATE_BRASS_INGOT = registerItem(CREATE_MOD_ID, "brass_ingot");
        CREATE_GOLD_SHEET = registerItem(CREATE_MOD_ID, "gold_sheet");
        CREATE_PRECISION_MECHANISM = registerItem(CREATE_MOD_ID, "precision_mechanism");
    };

    private static Item registerItem(String modId, String name) {
        return Registry.register(Registries.ITEM, Identifier.of(modId, name), new Item(new Item.Settings()));
    }
}
