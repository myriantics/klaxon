package net.myriantics.klaxon.datagen.lang.providers.tag;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.myriantics.klaxon.datagen.lang.KlaxonEnUsLanguageProvider;
import net.myriantics.klaxon.datagen.lang.KlaxonEnUsLanguageSubProvider;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;

public final class KlaxonEnUsItemTagLanguageProvider extends KlaxonEnUsLanguageSubProvider {
    public KlaxonEnUsItemTagLanguageProvider(KlaxonEnUsLanguageProvider provider, FabricLanguageProvider.TranslationBuilder builder) {
        super(provider, builder);
    }

    @Override
    public void generate() {
        generateMakeshiftCraftingTags();
        generateEnchantableTagTranslations();
        generateCraftingLogisticsTags();
        generateAdvancementLogisticsTags();
        generateMechanicsTags();
        generateCategoryTags();
    }

    private void generateEnchantableTagTranslations() {
        addTag(KlaxonItemTags.STREAMLINE_ENCHANTABLE, "Streamline Enchantable");
    }

    private void generateMakeshiftCraftingTags() {
        addTag(KlaxonItemTags.MAKESHIFT_CRAFTING_INGREDIENTS, "Makeshift Crafting Ingredients");
        addTag(KlaxonItemTags.MAKESHIFT_REPAIR_MATERIALS, "Makeshift Repair Materials");

        addTag(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_INGOTS, "Crude-Inclusive Steel Ingots");
        addTag(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_BLOCKS, "Crude-Inclusive Steel Blocks");
        addTag(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_NUGGETS, "Crude-Inclusive Steel Nuggets");
        addTag(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_PLATES, "Crude-Inclusive Steel Plates");
        addTag(KlaxonItemTags.CRUDE_INCLUSIVE_STEEL_CASING, "Crude-Inclusive Steel Casings");
    }

    private void generateCraftingLogisticsTags() {
        // crafting
        addTag(KlaxonItemTags.OVERWORLD_RUBBER_EXTRACTABLE_LOGS, "Overworld Rubber-Extractable Logs");
        addTag(KlaxonItemTags.NETHER_RUBBER_EXTRACTABLE_LOGS, "Nether Rubber-Extractable Logs");
        addTag(KlaxonItemTags.HALLNOX_STEMS, "Hallnox Stems");
        addTag(KlaxonItemTags.GEAR_GRIP_MATERIALS, "Gear Grip Materials");

        // blast processing
        addTag(KlaxonItemTags.BEDLIKE_EXPLODABLES, "Bedlike Explodables");

        // tool usage
        addTag(KlaxonItemTags.RECIPE_PROCESSING_WIRECUTTERS, "Recipe Processing Wirecutters");
        addTag(KlaxonItemTags.RECIPE_PROCESSING_HAMMERS, "Recipe Processing Hammers");
    }

    private void generateAdvancementLogisticsTags() {
        addTag(KlaxonItemTags.MAKESHIFT_CRAFTED_EQUIPMENT, "Makeshift Crafted Equipment");
        addTag(KlaxonItemTags.KLAXON_ROOT_ADVANCEMENT_GRANTING_ITEMS, "KLAXON - Initialization Root Advancement Granting Items");
    }

    private void generateMechanicsTags() {
        // entity weight
        addTag(KlaxonItemTags.HEAVY_EQUIPMENT, "Heavy Equipment");

        // enchantment & anvils
        addTag(KlaxonItemTags.INFINITELY_REPAIRABLE, "Infinitely Repairable Items");
        addTag(KlaxonItemTags.NO_XP_COST_REPAIRABLE, "Items Repairable With No EXP");
        addTag(KlaxonItemTags.UNENCHANTABLE, "Unenchantable Items");

        // ferromagnetism
        addTag(KlaxonItemTags.FERROMAGNETIC_ITEMS, "Ferromagnetic Items");
        addTag(KlaxonItemTags.FERROMAGNETIC_ITEM_BLACKLIST, "Ferromagnetic Item Blacklist");
    }

    private void generateCategoryTags() {
        // equipment categories
        addTag(KlaxonItemTags.STEEL_ARMOR, "Steel Armor");
        addTag(KlaxonItemTags.STEEL_EQUIPMENT, "Steel Equipment");
        addTag(KlaxonItemTags.GRAPPLE_CLAWS, "Grapple Claws");
        addTag(KlaxonItemTags.CABLE_SHEARS, "Cable Shears");

        // fractured materials
        addTag(KlaxonItemTags.FRACTURED_MATERIALS, "Fractured Materials");
        addTag(KlaxonItemTags.FRACTURED_COALS, "Fractured Coal");
        addTag(KlaxonItemTags.FRACTURED_IRON, "Fractured Iron");
        addTag(KlaxonItemTags.FRACTURED_GOLD, "Fractured Gold");
        addTag(KlaxonItemTags.FRACTURED_COPPER, "Fractured Copper");
        addTag(KlaxonItemTags.FRACTURED_RAW_IRON, "Fractured Raw Iron");
        addTag(KlaxonItemTags.FRACTURED_RAW_GOLD, "Fractured Raw Gold");
        addTag(KlaxonItemTags.FRACTURED_RAW_COPPER, "Fractured Raw Copper");
    }
}
