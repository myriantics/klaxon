package net.myriantics.klaxon.registry.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;

public abstract class KlaxonCreativeModeTabs {

    public static final ResourceKey<CreativeModeTab> KLAXON_BUILDING_BLOCKS_ID = locateItemGroupId("building_blocks");
    public static final ResourceKey<CreativeModeTab> KLAXON_EQUIPMENT_ID = locateItemGroupId("equipment");
    public static final ResourceKey<CreativeModeTab> KLAXON_MACHINES_ID = locateItemGroupId("machines");
    public static final ResourceKey<CreativeModeTab> KLAXON_MATERIALS_ID = locateItemGroupId("materials");

    public static final CreativeModeTab KLAXON_EQUIPMENT = FabricItemGroup.builder()
            .icon(() -> new ItemStack(KlaxonItems.STEEL_HAMMER))
            .title(Component.translatable("itemGroup.klaxon.equipment"))
            .displayItems(((displayContext, entries) -> {
                entries.accept(KlaxonItems.STEEL_HAMMER.value());
                entries.accept(KlaxonItems.STEEL_CABLE_SHEARS.value());
                entries.accept(KlaxonItems.STEEL_WRENCH.value());
                entries.accept(KlaxonItems.STEEL_CLEAVER.value());
                entries.accept(KlaxonItems.STEEL_LIGHTER.value());

                entries.accept(KlaxonItems.STEEL_HELMET.value());
                entries.accept(KlaxonItems.STEEL_CHESTPLATE.value());
                entries.accept(KlaxonItems.STEEL_LEGGINGS.value());
                entries.accept(KlaxonItems.STEEL_BOOTS.value());
                entries.accept(KlaxonItems.CRESTED_STEEL_HELMET.value());

                entries.accept(KlaxonItems.GRAPPLE_WINCH.value());
                entries.accept(KlaxonItems.STEEL_GRAPPLE_CLAW.value());
            })).build();

    public static final CreativeModeTab KLAXON_MACHINES = FabricItemGroup.builder()
            .icon(() -> new ItemStack(KlaxonItems.DEEPSLATE_BLAST_PROCESSOR))
            .title(Component.translatable("itemGroup.klaxon.machines"))
            .displayItems(((displayContext, entries) -> {
                entries.accept(KlaxonItems.DEEPSLATE_BLAST_PROCESSOR.value());
                entries.accept(KlaxonItems.STEEL_BLAST_PROCESSOR.value());
                entries.accept(KlaxonItems.PRECISION_DISPENSER.value());
                entries.accept(KlaxonItems.NETHER_REACTOR_CORE.value());
                entries.accept(KlaxonItems.CRUDE_NETHER_REACTOR_CORE.value());
                entries.accept(KlaxonItems.STEEL_CASING.value());
                entries.accept(KlaxonItems.CRUDE_STEEL_CASING.value());
                entries.accept(KlaxonItems.STEEL_WORKBENCH.value());
                entries.accept(KlaxonItems.MODULAR_EXPLOSIVE_BLOCK.value());
                entries.accept(KlaxonItems.HEAVY_GATED_PRESSURE_PLATE.value());
                entries.accept(KlaxonItems.FAULTY_HEAVY_GATED_PRESSURE_PLATE.value());
                // entries.add(KlaxonItems.WAXED_COPPER_PIPE_MATRIX);
            })).build();

    public static final CreativeModeTab KLAXON_MATERIALS = FabricItemGroup.builder()
            .icon(() -> new ItemStack(KlaxonItems.STEEL_PLATE))
            .title(Component.translatable("itemGroup.klaxon.materials"))
            .displayItems(((displayContext, entries) -> {
                // storage blocks
                entries.accept(KlaxonItems.STEEL_BLOCK.value());
                entries.accept(KlaxonItems.CRUDE_STEEL_BLOCK.value());
                entries.accept(KlaxonItems.RUBBER_BLOCK.value());
                // entries.add(KlaxonItems.MOLTEN_RUBBER_BLOCK);

                // plating storage blocks
                entries.accept(KlaxonItems.STEEL_PLATING_BLOCK.value());
                entries.accept(KlaxonItems.CRUDE_STEEL_PLATING_BLOCK.value());
                entries.accept(KlaxonItems.IRON_PLATING_BLOCK.value());
                entries.accept(KlaxonItems.GOLD_PLATING_BLOCK.value());
                entries.accept(KlaxonItems.COPPER_PLATING_BLOCK.value());
                entries.accept(KlaxonItems.RUBBER_SHEET_BLOCK.value());

                // wire spools / wire storage blocks
                entries.accept(KlaxonItems.STEEL_WIRE_SPOOL_BLOCK.value());
                entries.accept(KlaxonItems.IRON_WIRE_SPOOL_BLOCK.value());
                entries.accept(KlaxonItems.GOLD_WIRE_SPOOL_BLOCK.value());
                entries.accept(KlaxonItems.COPPER_WIRE_SPOOL_BLOCK.value());

                // casings and hulls
                entries.accept(KlaxonItems.STEEL_CASING.value());
                entries.accept(KlaxonItems.CRUDE_STEEL_CASING.value());

                // ingots
                entries.accept(KlaxonItems.STEEL_INGOT.value());
                entries.accept(KlaxonItems.CRUDE_STEEL_INGOT.value());

                // nuggets
                entries.accept(KlaxonItems.STEEL_NUGGET.value());
                entries.accept(KlaxonItems.CRUDE_STEEL_NUGGET.value());
                entries.accept(KlaxonItems.COPPER_NUGGET.value());

                // raw materials
                entries.accept(KlaxonItems.RUBBER_GLOB.value());

                // hallnox
                entries.accept(KlaxonItems.HALLNOX_POD.value());

                // plates
                entries.accept(KlaxonItems.IRON_PLATE.value());
                entries.accept(KlaxonItems.COPPER_PLATE.value());
                entries.accept(KlaxonItems.GOLD_PLATE.value());
                entries.accept(KlaxonItems.CRUDE_STEEL_PLATE.value());
                entries.accept(KlaxonItems.STEEL_PLATE.value());
                entries.accept(KlaxonItems.RUBBER_SHEET.value());

                // wires
                entries.accept(KlaxonItems.STEEL_WIRE.value());
                entries.accept(KlaxonItems.IRON_WIRE.value());
                entries.accept(KlaxonItems.GOLD_WIRE.value());
                entries.accept(KlaxonItems.COPPER_WIRE.value());

                // blends
                entries.accept(KlaxonItems.CRUDE_STEEL_MIXTURE.value());

                // fractured items
                entries.accept(KlaxonItems.FRACTURED_IRON.value());
                entries.accept(KlaxonItems.FRACTURED_COPPER.value());
                entries.accept(KlaxonItems.FRACTURED_GOLD.value());
                entries.accept(KlaxonItems.FRACTURED_COAL.value());
                entries.accept(KlaxonItems.FRACTURED_CHARCOAL.value());
                entries.accept(KlaxonItems.FRACTURED_RAW_IRON.value());
                entries.accept(KlaxonItems.FRACTURED_RAW_COPPER.value());
                entries.accept(KlaxonItems.FRACTURED_RAW_GOLD.value());
            })).build();

    public static final CreativeModeTab KLAXON_BUILDING_BLOCKS = FabricItemGroup.builder()
            .icon(() -> new ItemStack(KlaxonItems.STEEL_BLOCK))
            .title(Component.translatable("itemGroup.klaxon.building_blocks"))
            .displayItems(((displayContext, entries) -> {
                // storage blocks
                entries.accept(KlaxonItems.STEEL_BLOCK.value());
                entries.accept(KlaxonItems.CRUDE_STEEL_BLOCK.value());
                entries.accept(KlaxonItems.RUBBER_BLOCK.value());
                entries.accept(KlaxonItems.MOLTEN_RUBBER_BLOCK.value());

                // plating blocks
                entries.accept(KlaxonItems.STEEL_PLATING_BLOCK.value());
                entries.accept(KlaxonItems.CRUDE_STEEL_PLATING_BLOCK.value());
                entries.accept(KlaxonItems.RUBBER_SHEET_BLOCK.value());
                entries.accept(KlaxonItems.IRON_PLATING_BLOCK.value());
                entries.accept(KlaxonItems.GOLD_PLATING_BLOCK.value());
                entries.accept(KlaxonItems.COPPER_PLATING_BLOCK.value());
                entries.accept(KlaxonItems.EXPOSED_COPPER_PLATING_BLOCK.value());
                entries.accept(KlaxonItems.WEATHERED_COPPER_PLATING_BLOCK.value());
                entries.accept(KlaxonItems.OXIDIZED_COPPER_PLATING_BLOCK.value());
                entries.accept(KlaxonItems.WAXED_COPPER_PLATING_BLOCK.value());
                entries.accept(KlaxonItems.WAXED_EXPOSED_COPPER_PLATING_BLOCK.value());
                entries.accept(KlaxonItems.WAXED_WEATHERED_COPPER_PLATING_BLOCK.value());
                entries.accept(KlaxonItems.WAXED_OXIDIZED_COPPER_PLATING_BLOCK.value());

                // wire spool blocks
                entries.accept(KlaxonItems.STEEL_WIRE_SPOOL_BLOCK.value());
                entries.accept(KlaxonItems.IRON_WIRE_SPOOL_BLOCK.value());
                entries.accept(KlaxonItems.GOLD_WIRE_SPOOL_BLOCK.value());
                entries.accept(KlaxonItems.COPPER_WIRE_SPOOL_BLOCK.value());
                entries.accept(KlaxonItems.EXPOSED_COPPER_WIRE_SPOOL_BLOCK.value());
                entries.accept(KlaxonItems.WEATHERED_COPPER_WIRE_SPOOL_BLOCK.value());
                entries.accept(KlaxonItems.OXIDIZED_COPPER_WIRE_SPOOL_BLOCK.value());
                entries.accept(KlaxonItems.WAXED_COPPER_WIRE_SPOOL_BLOCK.value());
                entries.accept(KlaxonItems.WAXED_EXPOSED_COPPER_WIRE_SPOOL_BLOCK.value());
                entries.accept(KlaxonItems.WAXED_WEATHERED_COPPER_WIRE_SPOOL_BLOCK.value());
                entries.accept(KlaxonItems.WAXED_OXIDIZED_COPPER_WIRE_SPOOL_BLOCK.value());

                // pipe matrices
                /*
                entries.add(KlaxonItems.COPPER_PIPE_MATRIX);
                entries.add(KlaxonItems.EXPOSED_COPPER_PIPE_MATRIX);
                entries.add(KlaxonItems.WEATHERED_COPPER_PIPE_MATRIX);
                entries.add(KlaxonItems.OXIDIZED_COPPER_PIPE_MATRIX);
                entries.add(KlaxonItems.WAXED_COPPER_PIPE_MATRIX);
                entries.add(KlaxonItems.WAXED_EXPOSED_COPPER_PIPE_MATRIX);
                entries.add(KlaxonItems.WAXED_WEATHERED_COPPER_PIPE_MATRIX);
                entries.add(KlaxonItems.WAXED_OXIDIZED_COPPER_PIPE_MATRIX);
                 */

                // casings
                entries.accept(KlaxonItems.STEEL_CASING.value());
                entries.accept(KlaxonItems.CRUDE_STEEL_CASING.value());

                // hallnox
                entries.accept(KlaxonItems.HALLNOX_STEM.value());
                entries.accept(KlaxonItems.HALLNOX_HYPHAE.value());
                entries.accept(KlaxonItems.STRIPPED_HALLNOX_STEM.value());
                entries.accept(KlaxonItems.STRIPPED_HALLNOX_HYPHAE.value());
                entries.accept(KlaxonItems.HALLNOX_PLANKS.value());
                entries.accept(KlaxonItems.HALLNOX_STAIRS.value());
                entries.accept(KlaxonItems.HALLNOX_SLAB.value());
                entries.accept(KlaxonItems.HALLNOX_FENCE.value());
                entries.accept(KlaxonItems.HALLNOX_FENCE_GATE.value());
                entries.accept(KlaxonItems.HALLNOX_DOOR.value());
                entries.accept(KlaxonItems.HALLNOX_TRAPDOOR.value());
                entries.accept(KlaxonItems.HALLNOX_PRESSURE_PLATE.value());
                entries.accept(KlaxonItems.HALLNOX_BUTTON.value());
                entries.accept(KlaxonItems.HALLNOX_POD.value());
                entries.accept(KlaxonItems.HALLNOX_WART_BLOCK.value());
                entries.accept(KlaxonItems.HALLNOX_SIGN.value());
                entries.accept(KlaxonItems.HALLNOX_HANGING_SIGN.value());

                // decor
                entries.accept(KlaxonItems.HALLNOX_BULB.value());

                // misc
                entries.accept(KlaxonItems.STEEL_DOOR.value());
                entries.accept(KlaxonItems.STEEL_TRAPDOOR.value());
                entries.accept(KlaxonItems.CRUDE_STEEL_DOOR.value());
                entries.accept(KlaxonItems.CRUDE_STEEL_TRAPDOOR.value());
            })).build();


    private static ResourceKey<CreativeModeTab> locateItemGroupId(String name) {
        return ResourceKey.create(Registries.CREATIVE_MODE_TAB, KlaxonCommon.locate(name + "_item_group"));
    }

    public static void init() {
        register(KLAXON_EQUIPMENT_ID, KLAXON_EQUIPMENT);
        register(KLAXON_MACHINES_ID, KLAXON_MACHINES);
        register(KLAXON_MATERIALS_ID, KLAXON_MATERIALS);
        register(KLAXON_BUILDING_BLOCKS_ID, KLAXON_BUILDING_BLOCKS);

        KlaxonCommon.LOGGER.info("Registered KLAXON's Item Groups!");
    }

    private static void register(ResourceKey<CreativeModeTab> key, CreativeModeTab group) {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, key, group);
    }
}
