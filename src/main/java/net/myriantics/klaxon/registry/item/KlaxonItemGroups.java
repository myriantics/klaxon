package net.myriantics.klaxon.registry.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;

public abstract class KlaxonItemGroups {

    public static final RegistryKey<ItemGroup> KLAXON_BUILDING_BLOCKS_ID = locateItemGroupId("building_blocks");
    public static final RegistryKey<ItemGroup> KLAXON_EQUIPMENT_ID = locateItemGroupId("equipment");
    public static final RegistryKey<ItemGroup> KLAXON_MACHINES_ID = locateItemGroupId("machines");
    public static final RegistryKey<ItemGroup> KLAXON_MATERIALS_ID = locateItemGroupId("materials");

    public static final ItemGroup KLAXON_EQUIPMENT = FabricItemGroup.builder()
            .icon(() -> new ItemStack(KlaxonItems.STEEL_HAMMER))
            .displayName(Text.translatable("itemGroup.klaxon.equipment"))
            .entries(((displayContext, entries) -> {
                entries.add(KlaxonItems.STEEL_HAMMER);
                entries.add(KlaxonItems.STEEL_CABLE_SHEARS);
                entries.add(KlaxonItems.STEEL_WRENCH);
                entries.add(KlaxonItems.STEEL_CLEAVER);
                entries.add(Items.FLINT_AND_STEEL);

                entries.add(KlaxonItems.STEEL_HELMET);
                entries.add(KlaxonItems.STEEL_CHESTPLATE);
                entries.add(KlaxonItems.STEEL_LEGGINGS);
                entries.add(KlaxonItems.STEEL_BOOTS);

            })).build();

    public static final ItemGroup KLAXON_MACHINES = FabricItemGroup.builder()
            .icon(() -> new ItemStack(KlaxonItems.DEEPSLATE_BLAST_PROCESSOR))
            .displayName(Text.translatable("itemGroup.klaxon.machines"))
            .entries(((displayContext, entries) -> {
                entries.add(KlaxonItems.DEEPSLATE_BLAST_PROCESSOR);
                entries.add(KlaxonItems.NETHER_REACTOR_CORE);
                entries.add(KlaxonItems.CRUDE_NETHER_REACTOR_CORE);
                entries.add(KlaxonItems.STEEL_CASING);
                entries.add(KlaxonItems.CRUDE_STEEL_CASING);
            })).build();

    public static final ItemGroup KLAXON_MATERIALS = FabricItemGroup.builder()
            .icon(() -> new ItemStack(KlaxonItems.STEEL_PLATE))
            .displayName(Text.translatable("itemGroup.klaxon.materials"))
            .entries(((displayContext, entries) -> {
                // storage blocks
                entries.add(KlaxonItems.STEEL_BLOCK);
                entries.add(KlaxonItems.CRUDE_STEEL_BLOCK);
                entries.add(KlaxonItems.RUBBER_BLOCK);
                entries.add(KlaxonItems.MOLTEN_RUBBER_BLOCK);

                // plating storage blocks
                entries.add(KlaxonItems.STEEL_PLATING_BLOCK);
                entries.add(KlaxonItems.CRUDE_STEEL_PLATING_BLOCK);
                entries.add(KlaxonItems.IRON_PLATING_BLOCK);
                entries.add(KlaxonItems.GOLD_PLATING_BLOCK);
                entries.add(KlaxonItems.COPPER_PLATING_BLOCK);
                entries.add(KlaxonItems.RUBBER_SHEET_BLOCK);

                // casings and hulls
                entries.add(KlaxonItems.STEEL_CASING);
                entries.add(KlaxonItems.CRUDE_STEEL_CASING);

                // ingots
                entries.add(KlaxonItems.STEEL_INGOT);
                entries.add(KlaxonItems.CRUDE_STEEL_INGOT);

                // nuggets
                entries.add(KlaxonItems.STEEL_NUGGET);
                entries.add(KlaxonItems.CRUDE_STEEL_NUGGET);

                // raw materials
                entries.add(KlaxonItems.RUBBER_GLOB);
                entries.add(KlaxonItems.MOLTEN_RUBBER_GLOB);

                // hallnox
                entries.add(KlaxonItems.HALLNOX_POD);

                // plates
                entries.add(KlaxonItems.IRON_PLATE);
                entries.add(KlaxonItems.COPPER_PLATE);
                entries.add(KlaxonItems.GOLD_PLATE);
                entries.add(KlaxonItems.CRUDE_STEEL_PLATE);
                entries.add(KlaxonItems.STEEL_PLATE);
                entries.add(KlaxonItems.RUBBER_SHEET);
                entries.add(KlaxonItems.MOLTEN_RUBBER_SHEET);

                // blends
                entries.add(KlaxonItems.CRUDE_STEEL_MIXTURE);

                // fractured items
                entries.add(KlaxonItems.FRACTURED_IRON);
                entries.add(KlaxonItems.FRACTURED_COPPER);
                entries.add(KlaxonItems.FRACTURED_GOLD);
                entries.add(KlaxonItems.FRACTURED_COAL);
                entries.add(KlaxonItems.FRACTURED_CHARCOAL);
                entries.add(KlaxonItems.FRACTURED_RAW_IRON);
                entries.add(KlaxonItems.FRACTURED_RAW_COPPER);
                entries.add(KlaxonItems.FRACTURED_RAW_GOLD);
            })).build();

    public static final ItemGroup KLAXON_BUILDING_BLOCKS = FabricItemGroup.builder()
            .icon(() -> new ItemStack(KlaxonItems.STEEL_BLOCK))
            .displayName(Text.translatable("itemGroup.klaxon.building_blocks"))
            .entries(((displayContext, entries) -> {
                // storage blocks
                entries.add(KlaxonItems.STEEL_BLOCK);
                entries.add(KlaxonItems.CRUDE_STEEL_BLOCK);
                entries.add(KlaxonItems.RUBBER_BLOCK);
                entries.add(KlaxonItems.MOLTEN_RUBBER_BLOCK);

                // plating blocks
                entries.add(KlaxonItems.STEEL_PLATING_BLOCK);
                entries.add(KlaxonItems.CRUDE_STEEL_PLATING_BLOCK);
                entries.add(KlaxonItems.RUBBER_SHEET_BLOCK);
                entries.add(KlaxonItems.IRON_PLATING_BLOCK);
                entries.add(KlaxonItems.GOLD_PLATING_BLOCK);
                entries.add(KlaxonItems.COPPER_PLATING_BLOCK);
                entries.add(KlaxonItems.EXPOSED_COPPER_PLATING_BLOCK);
                entries.add(KlaxonItems.WEATHERED_COPPER_PLATING_BLOCK);
                entries.add(KlaxonItems.OXIDIZED_COPPER_PLATING_BLOCK);
                entries.add(KlaxonItems.WAXED_COPPER_PLATING_BLOCK);
                entries.add(KlaxonItems.WAXED_EXPOSED_COPPER_PLATING_BLOCK);
                entries.add(KlaxonItems.WAXED_WEATHERED_COPPER_PLATING_BLOCK);
                entries.add(KlaxonItems.WAXED_OXIDIZED_COPPER_PLATING_BLOCK);

                // casings
                entries.add(KlaxonItems.STEEL_CASING);
                entries.add(KlaxonItems.CRUDE_STEEL_CASING);

                // hallnox
                entries.add(KlaxonItems.HALLNOX_STEM);
                entries.add(KlaxonItems.HALLNOX_HYPHAE);
                entries.add(KlaxonItems.STRIPPED_HALLNOX_STEM);
                entries.add(KlaxonItems.STRIPPED_HALLNOX_HYPHAE);
                entries.add(KlaxonItems.HALLNOX_PLANKS);
                entries.add(KlaxonItems.HALLNOX_STAIRS);
                entries.add(KlaxonItems.HALLNOX_SLAB);
                entries.add(KlaxonItems.HALLNOX_FENCE);
                entries.add(KlaxonItems.HALLNOX_FENCE_GATE);
                entries.add(KlaxonItems.HALLNOX_DOOR);
                entries.add(KlaxonItems.HALLNOX_TRAPDOOR);
                entries.add(KlaxonItems.HALLNOX_PRESSURE_PLATE);
                entries.add(KlaxonItems.HALLNOX_BUTTON);
                entries.add(KlaxonItems.HALLNOX_POD);
                entries.add(KlaxonItems.HALLNOX_WART_BLOCK);
                entries.add(KlaxonItems.HALLNOX_SIGN);
                entries.add(KlaxonItems.HALLNOX_HANGING_SIGN);

                // decor
                entries.add(KlaxonItems.HALLNOX_BULB);

                // misc
                entries.add(KlaxonItems.STEEL_DOOR);
                entries.add(KlaxonItems.STEEL_TRAPDOOR);
                entries.add(KlaxonItems.CRUDE_STEEL_DOOR);
                entries.add(KlaxonItems.CRUDE_STEEL_TRAPDOOR);
            })).build();


    private static RegistryKey<ItemGroup> locateItemGroupId(String name) {
        return RegistryKey.of(RegistryKeys.ITEM_GROUP, KlaxonCommon.locate(name + "_item_group"));
    }

    public static void init() {
        register(KLAXON_EQUIPMENT_ID, KLAXON_EQUIPMENT);
        register(KLAXON_MACHINES_ID, KLAXON_MACHINES);
        register(KLAXON_MATERIALS_ID, KLAXON_MATERIALS);
        register(KLAXON_BUILDING_BLOCKS_ID, KLAXON_BUILDING_BLOCKS);

        KlaxonCommon.LOGGER.info("Registered KLAXON's Item Groups!");
    }

    private static void register(RegistryKey<ItemGroup> key, ItemGroup group) {
        Registry.register(Registries.ITEM_GROUP, key, group);
    }
}
