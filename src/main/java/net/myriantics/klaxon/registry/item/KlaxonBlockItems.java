package net.myriantics.klaxon.registry.item;

import net.minecraft.block.Block;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;

import java.util.List;

// KlaxonItems inherits this so you can access all the item fields from KlaxonItems.
public abstract class KlaxonBlockItems {

    // steel
    public static final Item STEEL_BLOCK = registerBlockItem(KlaxonBlocks.STEEL_BLOCK);
    public static final Item STEEL_PLATING_BLOCK = registerBlockItem(KlaxonBlocks.STEEL_PLATING_BLOCK);
    public static final Item STEEL_CASING = registerBlockItem(KlaxonBlocks.STEEL_CASING);
    public static final Item STEEL_WIRE_SPOOL_BLOCK = registerBlockItem(KlaxonBlocks.STEEL_WIRE_SPOOL_BLOCK);
    public static final Item STEEL_DOOR = registerBlockItem(KlaxonBlocks.STEEL_DOOR);
    public static final Item STEEL_TRAPDOOR = registerBlockItem(KlaxonBlocks.STEEL_TRAPDOOR);

    // crude steel
    public static final Item CRUDE_STEEL_BLOCK = registerBlockItem(KlaxonBlocks.CRUDE_STEEL_BLOCK);
    public static final Item CRUDE_STEEL_PLATING_BLOCK = registerBlockItem(KlaxonBlocks.CRUDE_STEEL_PLATING_BLOCK);
    public static final Item CRUDE_STEEL_CASING = registerBlockItem(KlaxonBlocks.CRUDE_STEEL_CASING);
    public static final Item CRUDE_STEEL_DOOR = registerBlockItem(KlaxonBlocks.CRUDE_STEEL_DOOR);
    public static final Item CRUDE_STEEL_TRAPDOOR = registerBlockItem(KlaxonBlocks.CRUDE_STEEL_TRAPDOOR);

    // iron
    public static final Item IRON_PLATING_BLOCK = registerBlockItem(KlaxonBlocks.IRON_PLATING_BLOCK);
    public static final Item IRON_WIRE_SPOOL_BLOCK = registerBlockItem(KlaxonBlocks.IRON_WIRE_SPOOL_BLOCK);

    // gold
    public static final Item GOLD_PLATING_BLOCK = registerBlockItem(KlaxonBlocks.GOLD_PLATING_BLOCK);
    public static final Item GOLD_WIRE_SPOOL_BLOCK = registerBlockItem(KlaxonBlocks.GOLD_WIRE_SPOOL_BLOCK);

    // copper
    public static final Item COPPER_PLATING_BLOCK = registerBlockItem(KlaxonBlocks.COPPER_PLATING_BLOCK);
    public static final Item EXPOSED_COPPER_PLATING_BLOCK = registerBlockItem(KlaxonBlocks.EXPOSED_COPPER_PLATING_BLOCK);
    public static final Item WEATHERED_COPPER_PLATING_BLOCK = registerBlockItem(KlaxonBlocks.WEATHERED_COPPER_PLATING_BLOCK);
    public static final Item OXIDIZED_COPPER_PLATING_BLOCK = registerBlockItem(KlaxonBlocks.OXIDIZED_COPPER_PLATING_BLOCK);
    public static final Item WAXED_COPPER_PLATING_BLOCK = registerBlockItem(KlaxonBlocks.WAXED_COPPER_PLATING_BLOCK);
    public static final Item WAXED_EXPOSED_COPPER_PLATING_BLOCK = registerBlockItem(KlaxonBlocks.WAXED_EXPOSED_COPPER_PLATING_BLOCK);
    public static final Item WAXED_WEATHERED_COPPER_PLATING_BLOCK = registerBlockItem(KlaxonBlocks.WAXED_WEATHERED_COPPER_PLATING_BLOCK);
    public static final Item WAXED_OXIDIZED_COPPER_PLATING_BLOCK = registerBlockItem(KlaxonBlocks.WAXED_OXIDIZED_COPPER_PLATING_BLOCK);

    public static final Item COPPER_WIRE_SPOOL_BLOCK = registerBlockItem(KlaxonBlocks.COPPER_WIRE_SPOOL_BLOCK);
    public static final Item EXPOSED_COPPER_WIRE_SPOOL_BLOCK = registerBlockItem(KlaxonBlocks.EXPOSED_COPPER_WIRE_SPOOL_BLOCK);
    public static final Item WEATHERED_COPPER_WIRE_SPOOL_BLOCK = registerBlockItem(KlaxonBlocks.WEATHERED_COPPER_WIRE_SPOOL_BLOCK);
    public static final Item OXIDIZED_COPPER_WIRE_SPOOL_BLOCK = registerBlockItem(KlaxonBlocks.OXIDIZED_COPPER_WIRE_SPOOL_BLOCK);
    public static final Item WAXED_COPPER_WIRE_SPOOL_BLOCK = registerBlockItem(KlaxonBlocks.WAXED_COPPER_WIRE_SPOOL_BLOCK);
    public static final Item WAXED_EXPOSED_COPPER_WIRE_SPOOL_BLOCK = registerBlockItem(KlaxonBlocks.WAXED_EXPOSED_COPPER_WIRE_SPOOL_BLOCK);
    public static final Item WAXED_WEATHERED_COPPER_WIRE_SPOOL_BLOCK = registerBlockItem(KlaxonBlocks.WAXED_WEATHERED_COPPER_WIRE_SPOOL_BLOCK);
    public static final Item WAXED_OXIDIZED_COPPER_WIRE_SPOOL_BLOCK = registerBlockItem(KlaxonBlocks.WAXED_OXIDIZED_COPPER_WIRE_SPOOL_BLOCK);

    // rubber
    public static final Item RUBBER_BLOCK = registerBlockItem(KlaxonBlocks.RUBBER_BLOCK);
    public static final Item RUBBER_SHEET_BLOCK = registerBlockItem(KlaxonBlocks.RUBBER_SHEET_BLOCK);

    // molten rubber
    public static final Item MOLTEN_RUBBER_BLOCK = registerBlockItem(KlaxonBlocks.MOLTEN_RUBBER_BLOCK);

    // machines
    public static final Item DEEPSLATE_BLAST_PROCESSOR = registerBlockItem(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR);
    public static final Item NETHER_REACTOR_CORE = registerBlockItem(KlaxonBlocks.NETHER_REACTOR_CORE);
    public static final Item CRUDE_NETHER_REACTOR_CORE = registerBlockItem(KlaxonBlocks.CRUDE_NETHER_REACTOR_CORE);

    // copper pipe matrices
    public static final Item COPPER_PIPE_MATRIX = registerBlockItem("copper_pipe_matrix", KlaxonBlocks.COPPER_PIPE_MATRIX_SEGMENT);

    // hallnox
    public static final Item HALLNOX_POD = registerBlockItem(KlaxonBlocks.HALLNOX_POD);
    public static final Item HALLNOX_WART_BLOCK = registerBlockItem(KlaxonBlocks.HALLNOX_WART_BLOCK);
    public static final Item HALLNOX_STEM = registerBlockItem(KlaxonBlocks.HALLNOX_STEM);
    public static final Item STRIPPED_HALLNOX_STEM = registerBlockItem(KlaxonBlocks.STRIPPED_HALLNOX_STEM);
    public static final Item HALLNOX_HYPHAE = registerBlockItem(KlaxonBlocks.HALLNOX_HYPHAE);
    public static final Item STRIPPED_HALLNOX_HYPHAE = registerBlockItem(KlaxonBlocks.STRIPPED_HALLNOX_HYPHAE);
    public static final Item HALLNOX_PLANKS = registerBlockItem(KlaxonBlocks.HALLNOX_PLANKS);
    public static final Item HALLNOX_STAIRS = registerBlockItem(KlaxonBlocks.HALLNOX_STAIRS);
    public static final Item HALLNOX_SLAB = registerBlockItem(KlaxonBlocks.HALLNOX_SLAB);
    public static final Item HALLNOX_BUTTON = registerBlockItem(KlaxonBlocks.HALLNOX_BUTTON);
    public static final Item HALLNOX_PRESSURE_PLATE = registerBlockItem(KlaxonBlocks.HALLNOX_PRESSURE_PLATE);
    public static final Item HALLNOX_DOOR = registerBlockItem(KlaxonBlocks.HALLNOX_DOOR);
    public static final Item HALLNOX_TRAPDOOR = registerBlockItem(KlaxonBlocks.HALLNOX_TRAPDOOR);
    public static final Item HALLNOX_FENCE = registerBlockItem(KlaxonBlocks.HALLNOX_FENCE);
    public static final Item HALLNOX_FENCE_GATE = registerBlockItem(KlaxonBlocks.HALLNOX_FENCE_GATE);
    public static final Item HALLNOX_SIGN = registerBlockItem(KlaxonBlocks.HALLNOX_SIGN,
            new SignItem(
                    new Item.Settings().maxCount(16),
                    KlaxonBlocks.HALLNOX_SIGN,
                    KlaxonBlocks.HALLNOX_WALL_SIGN
            )
    );
    public static final Item HALLNOX_HANGING_SIGN = registerBlockItem(KlaxonBlocks.HALLNOX_HANGING_SIGN,
            new HangingSignItem(
                    KlaxonBlocks.HALLNOX_HANGING_SIGN,
                    KlaxonBlocks.HALLNOX_WALL_HANGING_SIGN,
                    new Item.Settings().maxCount(16)
            )
    );
    public static final Item HALLNOX_BULB = registerBlockItem(KlaxonBlocks.HALLNOX_BULB);

    private static Item registerBlockItem(String name, Block block) {
        return registerBlockItem(name, new BlockItem(block, new Item.Settings()));
    }

    private static Item registerBlockItem(Block block) {
        return registerBlockItem(block, new BlockItem(block, new Item.Settings()));
    }

    private static Item registerBlockItem(Block block, BlockItem blockItem) {
        return registerBlockItem(Registries.BLOCK.getId(block).getPath(), blockItem);
    }

    private static Item registerBlockItem(String name, BlockItem blockItem) {
        return Registry.register(Registries.ITEM, KlaxonCommon.locate(name), blockItem);
    }

    /**
     * Creates an ItemStack that illustrates the given block, for use in recipe viewers and machine output displays.
     * @param block The block to attempt item yonkage on.
     * @return New ItemStack of block's item with size 1 if block has an associated BlockItem, and outputs a barrier with lore if no BlockItem is present.
     */
    public static ItemStack getBlockDisplayStack(Block block) {

        // create items based off of blockitems if possible.
        // if a block doesn't have a blockitem, fall back to barrier with lore
        if (block.asItem().equals(Items.AIR)) {
            ItemStack displayStack = new ItemStack(Items.BARRIER);

            // apply components
            displayStack.applyComponentsFrom(ComponentMap.builder()
                    .add(DataComponentTypes.CUSTOM_NAME, block.getName().formatted(Formatting.RED))
                    .add(DataComponentTypes.LORE, new LoreComponent(
                            List.of(Text.translatable("klaxon.text.tooltip.lore.missing_block_item")
                                    .formatted(Formatting.BOLD))
                    )).build()
            );

            return displayStack;
        } else {
            return new ItemStack(block.asItem());
        }
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's BlockItems!");
    }
}
