package net.myriantics.klaxon.datagen.lang.providers;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.myriantics.klaxon.datagen.lang.KlaxonEnUsLanguageProvider;
import net.myriantics.klaxon.datagen.lang.KlaxonEnUsLanguageSubProvider;
import net.myriantics.klaxon.registry.item.KlaxonItems;

public final class KlaxonEnUsItemLanguageProvider extends KlaxonEnUsLanguageSubProvider {
    public KlaxonEnUsItemLanguageProvider(KlaxonEnUsLanguageProvider provider, FabricLanguageProvider.TranslationBuilder builder) {
        super(provider, builder);
    }

    public void generate() {
        generateEquipmentTranslations();
        generateMaterialTranslations();
        generateAliasedBlockItemTranslations();
    }

    private void generateEquipmentTranslations() {
        // tools
        addItem(KlaxonItems.STEEL_HAMMER, "Steel Hammer");
        addItem(KlaxonItems.STEEL_CABLE_SHEARS, "Steel Cable Shears");
        addItem(KlaxonItems.STEEL_CLEAVER, "Steel Cleaver");
        addItem(KlaxonItems.STEEL_WRENCH, "Steel Wrench");
        addItem(KlaxonItems.GRAPPLE_WINCH, "Grapple Winch");

        // ammo
        addItem(KlaxonItems.STEEL_GRAPPLE_CLAW, "Steel Grapple Claw");

        // armor
        addItem(KlaxonItems.STEEL_HELMET, "Steel Helmet");
        addItem(KlaxonItems.STEEL_CHESTPLATE, "Steel Chestplate");
        addItem(KlaxonItems.STEEL_LEGGINGS, "Steel Leggings");
        addItem(KlaxonItems.STEEL_BOOTS, "Steel Boots");
    }

    private void generateAliasedBlockItemTranslations() {
        // pipe matrices
        addItem(KlaxonItems.COPPER_PIPE_MATRIX, "Copper Pipe Matrix");
        addItem(KlaxonItems.EXPOSED_COPPER_PIPE_MATRIX, "Exposed Copper Pipe Matrix");
        addItem(KlaxonItems.WEATHERED_COPPER_PIPE_MATRIX, "Weathered Copper Pipe Matrix");
        addItem(KlaxonItems.OXIDIZED_COPPER_PIPE_MATRIX, "Oxidized Copper Pipe Matrix");
        addItem(KlaxonItems.WAXED_COPPER_PIPE_MATRIX, "Waxed Copper Pipe Matrix");
        addItem(KlaxonItems.WAXED_EXPOSED_COPPER_PIPE_MATRIX, "Waxed Exposed Copper Pipe Matrix");
        addItem(KlaxonItems.WAXED_WEATHERED_COPPER_PIPE_MATRIX, "Waxed Weathered Copper Pipe Matrix");
        addItem(KlaxonItems.WAXED_OXIDIZED_COPPER_PIPE_MATRIX, "Waxed Oxidized Copper Pipe Matrix");
    }

    private void generateMaterialTranslations() {
        // fractured ores
        addItem(KlaxonItems.FRACTURED_RAW_IRON, "Fractured Raw Iron Chunks");
        addItem(KlaxonItems.FRACTURED_RAW_GOLD, "Fractured Raw Gold Chunks");
        addItem(KlaxonItems.FRACTURED_RAW_COPPER, "Fractured Raw Copper Chunks");
        addItem(KlaxonItems.FRACTURED_IRON, "Fractured Iron Shards");
        addItem(KlaxonItems.FRACTURED_COPPER, "Fractured Copper Shards");
        addItem(KlaxonItems.FRACTURED_GOLD, "Fractured Gold Shards");
        addItem(KlaxonItems.FRACTURED_COAL, "Fractured Coal Chunks");
        addItem(KlaxonItems.FRACTURED_CHARCOAL, "Fractured Charcoal Chunks");

        // alloy blends
        addItem(KlaxonItems.CRUDE_STEEL_MIXTURE, "Crude Steel Mixture");

        // ingots
        addItem(KlaxonItems.STEEL_INGOT, "Steel Ingot");
        addItem(KlaxonItems.CRUDE_STEEL_INGOT, "Crude Steel Ingot");

        // nuggets
        addItem(KlaxonItems.STEEL_NUGGET, "Steel Nugget");
        addItem(KlaxonItems.CRUDE_STEEL_NUGGET, "Crude Steel Nugget");
        addItem(KlaxonItems.COPPER_NUGGET, "Copper Nugget");

        // plates
        addItem(KlaxonItems.STEEL_PLATE, "Steel Plate");
        addItem(KlaxonItems.CRUDE_STEEL_PLATE, "Crude Steel Plate");
        addItem(KlaxonItems.IRON_PLATE, "Iron Plate");
        addItem(KlaxonItems.GOLD_PLATE, "Gold Plate");
        addItem(KlaxonItems.COPPER_PLATE, "Copper Plate");

        // globs
        addItem(KlaxonItems.RUBBER_GLOB, "Rubber Glob");

        // sheets
        addItem(KlaxonItems.RUBBER_SHEET, "Rubber Sheet");

        // wires
        addItem(KlaxonItems.STEEL_WIRE, "Steel Wire");
        addItem(KlaxonItems.IRON_WIRE, "Iron Wire");
        addItem(KlaxonItems.COPPER_WIRE, "Copper Wire");
        addItem(KlaxonItems.GOLD_WIRE, "Gold Wire");
    }
}
