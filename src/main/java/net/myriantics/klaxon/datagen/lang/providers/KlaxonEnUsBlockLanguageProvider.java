package net.myriantics.klaxon.datagen.lang.providers;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.myriantics.klaxon.datagen.lang.KlaxonEnUsLanguageProvider;
import net.myriantics.klaxon.datagen.lang.KlaxonEnUsLanguageSubProvider;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;

public final class KlaxonEnUsBlockLanguageProvider extends KlaxonEnUsLanguageSubProvider {
    public KlaxonEnUsBlockLanguageProvider(KlaxonEnUsLanguageProvider provider, FabricLanguageProvider.TranslationBuilder builder) {
        super(provider, builder);
    }

    @Override
    public void generate() {
        generateMachineTranslations();
        generateBuildingBlockTranslations();
    }
    
    private void generateMachineTranslations() {
        // machines
        addBlock(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR, "Deepslate Blast Processor");
        addBlock(KlaxonBlocks.STEEL_BLAST_PROCESSOR, "Steel Blast Processor");
        addBlock(KlaxonBlocks.PRECISION_DISPENSER, "Precision Dispenser");
        addBlock(KlaxonBlocks.NETHER_REACTOR_CORE, "Nether Reactor Core");
        addBlock(KlaxonBlocks.CRUDE_NETHER_REACTOR_CORE, "Crude Nether Reactor Core");
        addBlock(KlaxonBlocks.STEEL_WORKBENCH, "Steel Workbench");
        addBlock(KlaxonBlocks.MODULAR_EXPLOSIVE_BLOCK, "Modular Explosive Block");

        // filing cabinets
        addBlock(KlaxonBlocks.FILING_CABINET_BASE, "Filing Cabinet Base");
        addBlock(KlaxonBlocks.FILING_CABINET_DRAWER, "Filing Cabinet Drawer");

        // casing
        addBlock(KlaxonBlocks.STEEL_CASING, "Steel Casing");
        addBlock(KlaxonBlocks.CRUDE_STEEL_CASING, "Crude Steel Casing");

        // pipe matrices
        addBlock(KlaxonBlocks.COPPER_PIPE_MATRIX_U_BEND, "Copper Pipe Matrix U-Bend");
        addBlock(KlaxonBlocks.EXPOSED_COPPER_PIPE_MATRIX_U_BEND, "Exposed Copper Pipe Matrix U-Bend");
        addBlock(KlaxonBlocks.WEATHERED_COPPER_PIPE_MATRIX_U_BEND, "Weathered Copper Pipe Matrix U-Bend");
        addBlock(KlaxonBlocks.OXIDIZED_COPPER_PIPE_MATRIX_U_BEND, "Oxidized Copper Pipe Matrix U-Bend");
        addBlock(KlaxonBlocks.WAXED_COPPER_PIPE_MATRIX_U_BEND, "Waxed Copper Pipe Matrix U-Bend");
        addBlock(KlaxonBlocks.WAXED_EXPOSED_COPPER_PIPE_MATRIX_U_BEND, "Waxed Exposed Copper Pipe Matrix U-Bend");
        addBlock(KlaxonBlocks.WAXED_WEATHERED_COPPER_PIPE_MATRIX_U_BEND, "Waxed Weathered Copper Pipe Matrix U-Bend");
        addBlock(KlaxonBlocks.WAXED_OXIDIZED_COPPER_PIPE_MATRIX_U_BEND, "Waxed Oxidized Copper Pipe Matrix U-Bend");
        addBlock(KlaxonBlocks.COPPER_PIPE_MATRIX_SEGMENT, "Copper Pipe Matrix Segment");
        addBlock(KlaxonBlocks.EXPOSED_COPPER_PIPE_MATRIX_SEGMENT, "Exposed Copper Pipe Matrix Segment");
        addBlock(KlaxonBlocks.WEATHERED_COPPER_PIPE_MATRIX_SEGMENT, "Weathered Copper Pipe Matrix Segment");
        addBlock(KlaxonBlocks.OXIDIZED_COPPER_PIPE_MATRIX_SEGMENT, "Oxidized Copper Pipe Matrix Segment");
        addBlock(KlaxonBlocks.WAXED_COPPER_PIPE_MATRIX_SEGMENT, "Waxed Copper Pipe Matrix Segment");
        addBlock(KlaxonBlocks.WAXED_EXPOSED_COPPER_PIPE_MATRIX_SEGMENT, "Waxed Exposed Copper Pipe Matrix Segment");
        addBlock(KlaxonBlocks.WAXED_WEATHERED_COPPER_PIPE_MATRIX_SEGMENT, "Waxed Weathered Copper Pipe Matrix Segment");
        addBlock(KlaxonBlocks.WAXED_OXIDIZED_COPPER_PIPE_MATRIX_SEGMENT, "Waxed Oxidized Copper Pipe Matrix Segment");
    }
    
    private void generateBuildingBlockTranslations() {
        // storage blocks
        addBlock(KlaxonBlocks.STEEL_BLOCK, "Block of Steel");
        addBlock(KlaxonBlocks.CRUDE_STEEL_BLOCK, "Block of Crude Steel");
        addBlock(KlaxonBlocks.RUBBER_BLOCK, "Block of Rubber");
        addBlock(KlaxonBlocks.MOLTEN_RUBBER_BLOCK, "Block of Molten Rubber");
        addBlock(KlaxonBlocks.RUBBER_SHEET_BLOCK, "Rubber Sheet Block");

        // plating blocks
        addBlock(KlaxonBlocks.STEEL_PLATING_BLOCK, "Steel Plating Block");
        addBlock(KlaxonBlocks.CRUDE_STEEL_PLATING_BLOCK, "Crude Steel Plating Block");
        addBlock(KlaxonBlocks.IRON_PLATING_BLOCK, "Iron Plating Block");
        addBlock(KlaxonBlocks.GOLD_PLATING_BLOCK, "Gold Plating Block");
        addBlock(KlaxonBlocks.COPPER_PLATING_BLOCK, "Copper Plating Block");
        addBlock(KlaxonBlocks.EXPOSED_COPPER_PLATING_BLOCK, "Exposed Copper Plating Block");
        addBlock(KlaxonBlocks.WEATHERED_COPPER_PLATING_BLOCK, "Weathered Copper Plating Block");
        addBlock(KlaxonBlocks.OXIDIZED_COPPER_PLATING_BLOCK, "Oxidized Copper Plating Block");
        addBlock(KlaxonBlocks.WAXED_COPPER_PLATING_BLOCK, "Waxed Copper Plating Block");
        addBlock(KlaxonBlocks.WAXED_EXPOSED_COPPER_PLATING_BLOCK, "Waxed Exposed Copper Plating Block");
        addBlock(KlaxonBlocks.WAXED_WEATHERED_COPPER_PLATING_BLOCK, "Waxed Weathered Copper Plating Block");
        addBlock(KlaxonBlocks.WAXED_OXIDIZED_COPPER_PLATING_BLOCK, "Waxed Oxidized Copper Plating Block");

        // wire spools
        addBlock(KlaxonBlocks.STEEL_WIRE_SPOOL_BLOCK, "Steel Wire Spool");
        addBlock(KlaxonBlocks.IRON_WIRE_SPOOL_BLOCK, "Iron Wire Spool");
        addBlock(KlaxonBlocks.GOLD_WIRE_SPOOL_BLOCK, "Gold Wire Spool");
        addBlock(KlaxonBlocks.COPPER_WIRE_SPOOL_BLOCK, "Copper Wire Spool");
        addBlock(KlaxonBlocks.EXPOSED_COPPER_WIRE_SPOOL_BLOCK, "Exposed Copper Wire Spool");
        addBlock(KlaxonBlocks.WEATHERED_COPPER_WIRE_SPOOL_BLOCK, "Weathered Copper Wire Spool");
        addBlock(KlaxonBlocks.OXIDIZED_COPPER_WIRE_SPOOL_BLOCK, "Oxidized Copper Wire Spool");
        addBlock(KlaxonBlocks.WAXED_COPPER_WIRE_SPOOL_BLOCK, "Waxed Copper Wire Spool");
        addBlock(KlaxonBlocks.WAXED_EXPOSED_COPPER_WIRE_SPOOL_BLOCK, "Waxed Exposed Copper Wire Spool");
        addBlock(KlaxonBlocks.WAXED_WEATHERED_COPPER_WIRE_SPOOL_BLOCK, "Waxed Weathered Copper Wire Spool");
        addBlock(KlaxonBlocks.WAXED_OXIDIZED_COPPER_WIRE_SPOOL_BLOCK, "Waxed Oxidized Copper Wire Spool");

        // decor
        addBlock(KlaxonBlocks.STEEL_DOOR, "Steel Door");
        addBlock(KlaxonBlocks.STEEL_TRAPDOOR, "Steel Trapdoor");
        addBlock(KlaxonBlocks.HEAVY_GATED_PRESSURE_PLATE, "Heavy Gated Pressure Plate");
        addBlock(KlaxonBlocks.CRUDE_STEEL_DOOR, "Crude Steel Door");
        addBlock(KlaxonBlocks.CRUDE_STEEL_TRAPDOOR, "Crude Steel Trapdoor");
        addBlock(KlaxonBlocks.FAULTY_HEAVY_GATED_PRESSURE_PLATE, "Faulty Heavy Gated Pressure Plate");

        // hallnox
        addBlock(KlaxonBlocks.HALLNOX_POD, "Hallnox Pod");
        addBlock(KlaxonBlocks.POTTED_HALLNOX_POD, "Potted Hallnox Pod");
        addBlock(KlaxonBlocks.HALLNOX_BULB, "Hallnox Bulb");
        addBlock(KlaxonBlocks.HALLNOX_WART_BLOCK, "Hallnox Wart Block");
        addBlock(KlaxonBlocks.HALLNOX_STEM, "Hallnox Stem");
        addBlock(KlaxonBlocks.STRIPPED_HALLNOX_STEM, "Stripped Hallnox Stem");
        addBlock(KlaxonBlocks.HALLNOX_HYPHAE, "Hallnox Hyphae");
        addBlock(KlaxonBlocks.STRIPPED_HALLNOX_HYPHAE, "Stripped Hallnox Hyphae");
        addBlock(KlaxonBlocks.HALLNOX_PLANKS, "Hallnox Planks");
        addBlock(KlaxonBlocks.HALLNOX_STAIRS, "Hallnox Stairs");
        addBlock(KlaxonBlocks.HALLNOX_SLAB, "Hallnox Slab");
        addBlock(KlaxonBlocks.HALLNOX_PRESSURE_PLATE, "Hallnox Pressure Plate");
        addBlock(KlaxonBlocks.HALLNOX_BUTTON, "Hallnox Button");
        addBlock(KlaxonBlocks.HALLNOX_TRAPDOOR, "Hallnox Trapdoor");
        addBlock(KlaxonBlocks.HALLNOX_DOOR, "Hallnox Door");
        addBlock(KlaxonBlocks.HALLNOX_SIGN, "Hallnox Sign");
        addBlock(KlaxonBlocks.HALLNOX_HANGING_SIGN, "Hallnox Hanging Sign");
        addBlock(KlaxonBlocks.HALLNOX_FENCE, "Hallnox Fence");
        addBlock(KlaxonBlocks.HALLNOX_FENCE_GATE, "Hallnox Fence Gate");
    }
}
