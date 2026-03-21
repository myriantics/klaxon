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

public abstract class KlaxonItemGroups {

    public static final ResourceKey<CreativeModeTab> KLAXON_BUILDING_BLOCKS_ID = locateItemGroupId("building_blocks");
    public static final ResourceKey<CreativeModeTab> KLAXON_EQUIPMENT_ID = locateItemGroupId("equipment");
    public static final ResourceKey<CreativeModeTab> KLAXON_MACHINES_ID = locateItemGroupId("machines");
    public static final ResourceKey<CreativeModeTab> KLAXON_MATERIALS_ID = locateItemGroupId("materials");

    public static final CreativeModeTab KLAXON_EQUIPMENT = FabricItemGroup.builder()
            .icon(() -> new ItemStack(KlaxonItems.STEEL_HAMMER))
            .title(Component.translatable("itemGroup.klaxon.equipment"))
            .displayItems(((displayContext, entries) -> {
                entries.accept(KlaxonItems.STEEL_HAMMER);
                entries.accept(KlaxonItems.STEEL_CABLE_SHEARS);
                entries.accept(KlaxonItems.STEEL_WRENCH);
                entries.accept(KlaxonItems.STEEL_CLEAVER);
                entries.accept(KlaxonItems.REINFORCED_FLINT_AND_STEEL);

                entries.accept(KlaxonItems.STEEL_HELMET);
                entries.accept(KlaxonItems.STEEL_CHESTPLATE);
                entries.accept(KlaxonItems.STEEL_LEGGINGS);
                entries.accept(KlaxonItems.STEEL_BOOTS);
                entries.accept(KlaxonItems.CRESTED_STEEL_HELMET);

                entries.accept(KlaxonItems.GRAPPLE_WINCH);
                entries.accept(KlaxonItems.STEEL_GRAPPLE_CLAW);
            })).build();

    public static final CreativeModeTab KLAXON_MACHINES = FabricItemGroup.builder()
            .icon(() -> new ItemStack(KlaxonItems.DEEPSLATE_BLAST_PROCESSOR))
            .title(Component.translatable("itemGroup.klaxon.machines"))
            .displayItems(((displayContext, entries) -> {
                entries.accept(KlaxonItems.DEEPSLATE_BLAST_PROCESSOR);
                entries.accept(KlaxonItems.NETHER_REACTOR_CORE);
                entries.accept(KlaxonItems.CRUDE_NETHER_REACTOR_CORE);
                entries.accept(KlaxonItems.STEEL_CASING);
                entries.accept(KlaxonItems.CRUDE_STEEL_CASING);
                // entries.add(KlaxonItems.WAXED_COPPER_PIPE_MATRIX);
            })).build();

    public static final CreativeModeTab KLAXON_MATERIALS = FabricItemGroup.builder()
            .icon(() -> new ItemStack(KlaxonItems.STEEL_PLATE))
            .title(Component.translatable("itemGroup.klaxon.materials"))
            .displayItems(((displayContext, entries) -> {
                // storage blocks
                entries.accept(KlaxonItems.STEEL_BLOCK);
                entries.accept(KlaxonItems.CRUDE_STEEL_BLOCK);
                entries.accept(KlaxonItems.RUBBER_BLOCK);
                // entries.add(KlaxonItems.MOLTEN_RUBBER_BLOCK);

                // plating storage blocks
                entries.accept(KlaxonItems.STEEL_PLATING_BLOCK);
                entries.accept(KlaxonItems.CRUDE_STEEL_PLATING_BLOCK);
                entries.accept(KlaxonItems.IRON_PLATING_BLOCK);
                entries.accept(KlaxonItems.GOLD_PLATING_BLOCK);
                entries.accept(KlaxonItems.COPPER_PLATING_BLOCK);
                entries.accept(KlaxonItems.RUBBER_SHEET_BLOCK);

                // wire spools / wire storage blocks
                entries.accept(KlaxonItems.STEEL_WIRE_SPOOL_BLOCK);
                entries.accept(KlaxonItems.IRON_WIRE_SPOOL_BLOCK);
                entries.accept(KlaxonItems.GOLD_WIRE_SPOOL_BLOCK);
                entries.accept(KlaxonItems.COPPER_WIRE_SPOOL_BLOCK);

                // casings and hulls
                entries.accept(KlaxonItems.STEEL_CASING);
                entries.accept(KlaxonItems.CRUDE_STEEL_CASING);

                // ingots
                entries.accept(KlaxonItems.STEEL_INGOT);
                entries.accept(KlaxonItems.CRUDE_STEEL_INGOT);

                // nuggets
                entries.accept(KlaxonItems.STEEL_NUGGET);
                entries.accept(KlaxonItems.CRUDE_STEEL_NUGGET);
                entries.accept(KlaxonItems.COPPER_NUGGET);

                // raw materials
                entries.accept(KlaxonItems.RUBBER_GLOB);

                // hallnox
                entries.accept(KlaxonItems.HALLNOX_POD);

                // plates
                entries.accept(KlaxonItems.IRON_PLATE);
                entries.accept(KlaxonItems.COPPER_PLATE);
                entries.accept(KlaxonItems.GOLD_PLATE);
                entries.accept(KlaxonItems.CRUDE_STEEL_PLATE);
                entries.accept(KlaxonItems.STEEL_PLATE);
                entries.accept(KlaxonItems.RUBBER_SHEET);

                // wires
                entries.accept(KlaxonItems.STEEL_WIRE);
                entries.accept(KlaxonItems.IRON_WIRE);
                entries.accept(KlaxonItems.GOLD_WIRE);
                entries.accept(KlaxonItems.COPPER_WIRE);

                // blends
                entries.accept(KlaxonItems.CRUDE_STEEL_MIXTURE);

                // fractured items
                entries.accept(KlaxonItems.FRACTURED_IRON);
                entries.accept(KlaxonItems.FRACTURED_COPPER);
                entries.accept(KlaxonItems.FRACTURED_GOLD);
                entries.accept(KlaxonItems.FRACTURED_COAL);
                entries.accept(KlaxonItems.FRACTURED_CHARCOAL);
                entries.accept(KlaxonItems.FRACTURED_RAW_IRON);
                entries.accept(KlaxonItems.FRACTURED_RAW_COPPER);
                entries.accept(KlaxonItems.FRACTURED_RAW_GOLD);
            })).build();

    public static final CreativeModeTab KLAXON_BUILDING_BLOCKS = FabricItemGroup.builder()
            .icon(() -> new ItemStack(KlaxonItems.STEEL_BLOCK))
            .title(Component.translatable("itemGroup.klaxon.building_blocks"))
            .displayItems(((displayContext, entries) -> {
                // storage blocks
                entries.accept(KlaxonItems.STEEL_BLOCK);
                entries.accept(KlaxonItems.CRUDE_STEEL_BLOCK);
                entries.accept(KlaxonItems.RUBBER_BLOCK);
                entries.accept(KlaxonItems.MOLTEN_RUBBER_BLOCK);

                // plating blocks
                entries.accept(KlaxonItems.STEEL_PLATING_BLOCK);
                entries.accept(KlaxonItems.CRUDE_STEEL_PLATING_BLOCK);
                entries.accept(KlaxonItems.RUBBER_SHEET_BLOCK);
                entries.accept(KlaxonItems.IRON_PLATING_BLOCK);
                entries.accept(KlaxonItems.GOLD_PLATING_BLOCK);
                entries.accept(KlaxonItems.COPPER_PLATING_BLOCK);
                entries.accept(KlaxonItems.EXPOSED_COPPER_PLATING_BLOCK);
                entries.accept(KlaxonItems.WEATHERED_COPPER_PLATING_BLOCK);
                entries.accept(KlaxonItems.OXIDIZED_COPPER_PLATING_BLOCK);
                entries.accept(KlaxonItems.WAXED_COPPER_PLATING_BLOCK);
                entries.accept(KlaxonItems.WAXED_EXPOSED_COPPER_PLATING_BLOCK);
                entries.accept(KlaxonItems.WAXED_WEATHERED_COPPER_PLATING_BLOCK);
                entries.accept(KlaxonItems.WAXED_OXIDIZED_COPPER_PLATING_BLOCK);

                // wire spool blocks
                entries.accept(KlaxonItems.STEEL_WIRE_SPOOL_BLOCK);
                entries.accept(KlaxonItems.IRON_WIRE_SPOOL_BLOCK);
                entries.accept(KlaxonItems.GOLD_WIRE_SPOOL_BLOCK);
                entries.accept(KlaxonItems.COPPER_WIRE_SPOOL_BLOCK);
                entries.accept(KlaxonItems.EXPOSED_COPPER_WIRE_SPOOL_BLOCK);
                entries.accept(KlaxonItems.WEATHERED_COPPER_WIRE_SPOOL_BLOCK);
                entries.accept(KlaxonItems.OXIDIZED_COPPER_WIRE_SPOOL_BLOCK);
                entries.accept(KlaxonItems.WAXED_COPPER_WIRE_SPOOL_BLOCK);
                entries.accept(KlaxonItems.WAXED_EXPOSED_COPPER_WIRE_SPOOL_BLOCK);
                entries.accept(KlaxonItems.WAXED_WEATHERED_COPPER_WIRE_SPOOL_BLOCK);
                entries.accept(KlaxonItems.WAXED_OXIDIZED_COPPER_WIRE_SPOOL_BLOCK);

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
                entries.accept(KlaxonItems.STEEL_CASING);
                entries.accept(KlaxonItems.CRUDE_STEEL_CASING);

                // hallnox
                entries.accept(KlaxonItems.HALLNOX_STEM);
                entries.accept(KlaxonItems.HALLNOX_HYPHAE);
                entries.accept(KlaxonItems.STRIPPED_HALLNOX_STEM);
                entries.accept(KlaxonItems.STRIPPED_HALLNOX_HYPHAE);
                entries.accept(KlaxonItems.HALLNOX_PLANKS);
                entries.accept(KlaxonItems.HALLNOX_STAIRS);
                entries.accept(KlaxonItems.HALLNOX_SLAB);
                entries.accept(KlaxonItems.HALLNOX_FENCE);
                entries.accept(KlaxonItems.HALLNOX_FENCE_GATE);
                entries.accept(KlaxonItems.HALLNOX_DOOR);
                entries.accept(KlaxonItems.HALLNOX_TRAPDOOR);
                entries.accept(KlaxonItems.HALLNOX_PRESSURE_PLATE);
                entries.accept(KlaxonItems.HALLNOX_BUTTON);
                entries.accept(KlaxonItems.HALLNOX_POD);
                entries.accept(KlaxonItems.HALLNOX_WART_BLOCK);
                entries.accept(KlaxonItems.HALLNOX_SIGN);
                entries.accept(KlaxonItems.HALLNOX_HANGING_SIGN);

                // decor
                entries.accept(KlaxonItems.HALLNOX_BULB);

                // misc
                entries.accept(KlaxonItems.STEEL_DOOR);
                entries.accept(KlaxonItems.STEEL_TRAPDOOR);
                entries.accept(KlaxonItems.CRUDE_STEEL_DOOR);
                entries.accept(KlaxonItems.CRUDE_STEEL_TRAPDOOR);
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
