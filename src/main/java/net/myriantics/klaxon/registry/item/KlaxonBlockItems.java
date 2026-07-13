package net.myriantics.klaxon.registry.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.block.Block;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.component.configuration.ModularExplosiveBlockConfigComponent;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystData;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;

import java.util.List;

// KlaxonItems inherits this so you can access all the item fields from KlaxonItems.
public abstract class KlaxonBlockItems {

    // steel
    public static final Holder<Item> STEEL_BLOCK = registerBlockItem(KlaxonBlocks.STEEL_BLOCK);
    public static final Holder<Item> STEEL_PLATING_BLOCK = registerBlockItem(KlaxonBlocks.STEEL_PLATING_BLOCK);
    public static final Holder<Item> STEEL_CASING = registerBlockItem(KlaxonBlocks.STEEL_CASING);
    public static final Holder<Item> STEEL_WIRE_SPOOL_BLOCK = registerBlockItem(KlaxonBlocks.STEEL_WIRE_SPOOL_BLOCK);
    public static final Holder<Item> STEEL_DOOR = registerBlockItem(KlaxonBlocks.STEEL_DOOR);
    public static final Holder<Item> STEEL_TRAPDOOR = registerBlockItem(KlaxonBlocks.STEEL_TRAPDOOR);
    public static final Holder<Item> HEAVY_GATED_PRESSURE_PLATE = registerBlockItem(KlaxonBlocks.HEAVY_GATED_PRESSURE_PLATE);

    // crude steel
    public static final Holder<Item> CRUDE_STEEL_BLOCK = registerBlockItem(KlaxonBlocks.CRUDE_STEEL_BLOCK);
    public static final Holder<Item> CRUDE_STEEL_PLATING_BLOCK = registerBlockItem(KlaxonBlocks.CRUDE_STEEL_PLATING_BLOCK);
    public static final Holder<Item> CRUDE_STEEL_CASING = registerBlockItem(KlaxonBlocks.CRUDE_STEEL_CASING);
    public static final Holder<Item> CRUDE_STEEL_DOOR = registerBlockItem(KlaxonBlocks.CRUDE_STEEL_DOOR);
    public static final Holder<Item> CRUDE_STEEL_TRAPDOOR = registerBlockItem(KlaxonBlocks.CRUDE_STEEL_TRAPDOOR);
    public static final Holder<Item> FAULTY_HEAVY_GATED_PRESSURE_PLATE = registerBlockItem(KlaxonBlocks.FAULTY_HEAVY_GATED_PRESSURE_PLATE);

    // iron
    public static final Holder<Item> IRON_PLATING_BLOCK = registerBlockItem(KlaxonBlocks.IRON_PLATING_BLOCK);
    public static final Holder<Item> IRON_WIRE_SPOOL_BLOCK = registerBlockItem(KlaxonBlocks.IRON_WIRE_SPOOL_BLOCK);

    // gold
    public static final Holder<Item> GOLD_PLATING_BLOCK = registerBlockItem(KlaxonBlocks.GOLD_PLATING_BLOCK);
    public static final Holder<Item> GOLD_WIRE_SPOOL_BLOCK = registerBlockItem(KlaxonBlocks.GOLD_WIRE_SPOOL_BLOCK);

    // copper
    public static final Holder<Item> COPPER_PLATING_BLOCK = registerBlockItem(KlaxonBlocks.COPPER_PLATING_BLOCK);
    public static final Holder<Item> EXPOSED_COPPER_PLATING_BLOCK = registerBlockItem(KlaxonBlocks.EXPOSED_COPPER_PLATING_BLOCK);
    public static final Holder<Item> WEATHERED_COPPER_PLATING_BLOCK = registerBlockItem(KlaxonBlocks.WEATHERED_COPPER_PLATING_BLOCK);
    public static final Holder<Item> OXIDIZED_COPPER_PLATING_BLOCK = registerBlockItem(KlaxonBlocks.OXIDIZED_COPPER_PLATING_BLOCK);
    public static final Holder<Item> WAXED_COPPER_PLATING_BLOCK = registerBlockItem(KlaxonBlocks.WAXED_COPPER_PLATING_BLOCK);
    public static final Holder<Item> WAXED_EXPOSED_COPPER_PLATING_BLOCK = registerBlockItem(KlaxonBlocks.WAXED_EXPOSED_COPPER_PLATING_BLOCK);
    public static final Holder<Item> WAXED_WEATHERED_COPPER_PLATING_BLOCK = registerBlockItem(KlaxonBlocks.WAXED_WEATHERED_COPPER_PLATING_BLOCK);
    public static final Holder<Item> WAXED_OXIDIZED_COPPER_PLATING_BLOCK = registerBlockItem(KlaxonBlocks.WAXED_OXIDIZED_COPPER_PLATING_BLOCK);

    public static final Holder<Item> COPPER_WIRE_SPOOL_BLOCK = registerBlockItem(KlaxonBlocks.COPPER_WIRE_SPOOL_BLOCK);
    public static final Holder<Item> EXPOSED_COPPER_WIRE_SPOOL_BLOCK = registerBlockItem(KlaxonBlocks.EXPOSED_COPPER_WIRE_SPOOL_BLOCK);
    public static final Holder<Item> WEATHERED_COPPER_WIRE_SPOOL_BLOCK = registerBlockItem(KlaxonBlocks.WEATHERED_COPPER_WIRE_SPOOL_BLOCK);
    public static final Holder<Item> OXIDIZED_COPPER_WIRE_SPOOL_BLOCK = registerBlockItem(KlaxonBlocks.OXIDIZED_COPPER_WIRE_SPOOL_BLOCK);
    public static final Holder<Item> WAXED_COPPER_WIRE_SPOOL_BLOCK = registerBlockItem(KlaxonBlocks.WAXED_COPPER_WIRE_SPOOL_BLOCK);
    public static final Holder<Item> WAXED_EXPOSED_COPPER_WIRE_SPOOL_BLOCK = registerBlockItem(KlaxonBlocks.WAXED_EXPOSED_COPPER_WIRE_SPOOL_BLOCK);
    public static final Holder<Item> WAXED_WEATHERED_COPPER_WIRE_SPOOL_BLOCK = registerBlockItem(KlaxonBlocks.WAXED_WEATHERED_COPPER_WIRE_SPOOL_BLOCK);
    public static final Holder<Item> WAXED_OXIDIZED_COPPER_WIRE_SPOOL_BLOCK = registerBlockItem(KlaxonBlocks.WAXED_OXIDIZED_COPPER_WIRE_SPOOL_BLOCK);

    // rubber
    public static final Holder<Item> RUBBER_BLOCK = registerBlockItem(KlaxonBlocks.RUBBER_BLOCK);
    public static final Holder<Item> RUBBER_SHEET_BLOCK = registerBlockItem(KlaxonBlocks.RUBBER_SHEET_BLOCK);

    // molten rubber
    public static final Holder<Item> MOLTEN_RUBBER_BLOCK = registerBlockItem(KlaxonBlocks.MOLTEN_RUBBER_BLOCK);

    // machines
    public static final Holder<Item> DEEPSLATE_BLAST_PROCESSOR = registerBlockItem(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR);
    public static final Holder<Item> STEEL_BLAST_PROCESSOR = registerBlockItem(KlaxonBlocks.STEEL_BLAST_PROCESSOR);
    public static final Holder<Item> PRECISION_DISPENSER = registerBlockItem(KlaxonBlocks.PRECISION_DISPENSER);
    public static final Holder<Item> NETHER_REACTOR_CORE = registerBlockItem(KlaxonBlocks.NETHER_REACTOR_CORE);
    public static final Holder<Item> CRUDE_NETHER_REACTOR_CORE = registerBlockItem(KlaxonBlocks.CRUDE_NETHER_REACTOR_CORE);
    public static final Holder<Item> MODULAR_EXPLOSIVE_BLOCK = registerBlockItem(KlaxonBlocks.MODULAR_EXPLOSIVE_BLOCK, new BlockItem(
            KlaxonBlocks.MODULAR_EXPLOSIVE_BLOCK.value(),
            new KlaxonItemProperties()
                    .component(KlaxonDataComponentTypes.MODULAR_EXPLOSIVE_BLOCK_CONFIG, ModularExplosiveBlockConfigComponent.DEFAULT)
                    .component(KlaxonDataComponentTypes.EXPLOSIVE_CATALYST_DATA, ExplosiveCatalystData.ZERO)
                    .getProperties()
    ));
    public static final Holder<Item> CREATIVE_CONTACT_CHARGER_BLOCK = registerBlockItem(KlaxonBlocks.CREATIVE_CONTACT_CHARGER_BLOCK);

    // ducts
    public static final Holder<Item> AIO_DUCT_DRIVER = registerBlockItem(KlaxonBlocks.AIO_DUCT_DRIVER);
    public static final Holder<Item> DUCT_SEGMENT = registerBlockItem(KlaxonBlocks.DUCT_SEGMENT);

    // workstations
    public static final Holder<Item> STEEL_WORKBENCH = registerBlockItem(KlaxonBlocks.STEEL_WORKBENCH);

    // copper pipe matrices
    public static final Holder<Item> COPPER_PIPE_MATRIX = registerBlockItem("copper_pipe_matrix", KlaxonBlocks.COPPER_PIPE_MATRIX_SEGMENT);
    public static final Holder<Item> EXPOSED_COPPER_PIPE_MATRIX = registerBlockItem("exposed_copper_pipe_matrix", KlaxonBlocks.EXPOSED_COPPER_PIPE_MATRIX_SEGMENT);
    public static final Holder<Item> WEATHERED_COPPER_PIPE_MATRIX = registerBlockItem("weathered_copper_pipe_matrix", KlaxonBlocks.WEATHERED_COPPER_PIPE_MATRIX_SEGMENT);
    public static final Holder<Item> OXIDIZED_COPPER_PIPE_MATRIX = registerBlockItem("oxidized_copper_pipe_matrix", KlaxonBlocks.OXIDIZED_COPPER_PIPE_MATRIX_SEGMENT);
    public static final Holder<Item> WAXED_COPPER_PIPE_MATRIX = registerBlockItem("waxed_copper_pipe_matrix", KlaxonBlocks.WAXED_COPPER_PIPE_MATRIX_SEGMENT);
    public static final Holder<Item> WAXED_EXPOSED_COPPER_PIPE_MATRIX = registerBlockItem("waxed_exposed_copper_pipe_matrix", KlaxonBlocks.WAXED_EXPOSED_COPPER_PIPE_MATRIX_SEGMENT);
    public static final Holder<Item> WAXED_WEATHERED_COPPER_PIPE_MATRIX = registerBlockItem("waxed_weathered_copper_pipe_matrix", KlaxonBlocks.WAXED_WEATHERED_COPPER_PIPE_MATRIX_SEGMENT);
    public static final Holder<Item> WAXED_OXIDIZED_COPPER_PIPE_MATRIX = registerBlockItem("waxed_oxidized_copper_pipe_matrix", KlaxonBlocks.WAXED_OXIDIZED_COPPER_PIPE_MATRIX_SEGMENT);

    // hallnox
    public static final Holder<Item> HALLNOX_POD = registerBlockItem(KlaxonBlocks.HALLNOX_POD);
    public static final Holder<Item> HALLNOX_WART_BLOCK = registerBlockItem(KlaxonBlocks.HALLNOX_WART_BLOCK);
    public static final Holder<Item> HALLNOX_STEM = registerBlockItem(KlaxonBlocks.HALLNOX_STEM);
    public static final Holder<Item> STRIPPED_HALLNOX_STEM = registerBlockItem(KlaxonBlocks.STRIPPED_HALLNOX_STEM);
    public static final Holder<Item> HALLNOX_HYPHAE = registerBlockItem(KlaxonBlocks.HALLNOX_HYPHAE);
    public static final Holder<Item> STRIPPED_HALLNOX_HYPHAE = registerBlockItem(KlaxonBlocks.STRIPPED_HALLNOX_HYPHAE);
    public static final Holder<Item> HALLNOX_PLANKS = registerBlockItem(KlaxonBlocks.HALLNOX_PLANKS);
    public static final Holder<Item> HALLNOX_STAIRS = registerBlockItem(KlaxonBlocks.HALLNOX_STAIRS);
    public static final Holder<Item> HALLNOX_SLAB = registerBlockItem(KlaxonBlocks.HALLNOX_SLAB);
    public static final Holder<Item> HALLNOX_BUTTON = registerBlockItem(KlaxonBlocks.HALLNOX_BUTTON);
    public static final Holder<Item> HALLNOX_PRESSURE_PLATE = registerBlockItem(KlaxonBlocks.HALLNOX_PRESSURE_PLATE);
    public static final Holder<Item> HALLNOX_DOOR = registerBlockItem(KlaxonBlocks.HALLNOX_DOOR);
    public static final Holder<Item> HALLNOX_TRAPDOOR = registerBlockItem(KlaxonBlocks.HALLNOX_TRAPDOOR);
    public static final Holder<Item> HALLNOX_FENCE = registerBlockItem(KlaxonBlocks.HALLNOX_FENCE);
    public static final Holder<Item> HALLNOX_FENCE_GATE = registerBlockItem(KlaxonBlocks.HALLNOX_FENCE_GATE);
    public static final Holder<Item> HALLNOX_SIGN = registerBlockItem(KlaxonBlocks.HALLNOX_SIGN,
            new SignItem(
                    new Item.Properties().stacksTo(16),
                    KlaxonBlocks.HALLNOX_SIGN.value(),
                    KlaxonBlocks.HALLNOX_WALL_SIGN.value()
            )
    );
    public static final Holder<Item> HALLNOX_HANGING_SIGN = registerBlockItem(KlaxonBlocks.HALLNOX_HANGING_SIGN,
            new HangingSignItem(
                    KlaxonBlocks.HALLNOX_HANGING_SIGN.value(),
                    KlaxonBlocks.HALLNOX_WALL_HANGING_SIGN.value(),
                    new Item.Properties().stacksTo(16)
            )
    );
    public static final Holder<Item> HALLNOX_BULB = registerBlockItem(KlaxonBlocks.HALLNOX_BULB);

    private static Holder<Item> registerBlockItem(String name, Holder<Block> holder) {
        return registerBlockItem(name, holder.value());
    }

    private static Holder<Item> registerBlockItem(String name, Block block) {
        return registerBlockItem(name, new ItemNameBlockItem(block, new Item.Properties()));
    }

    private static Holder<Item> registerBlockItem(Holder<Block> holder) {
        return registerBlockItem(holder.value());
    }

    private static Holder<Item> registerBlockItem(Block block) {
        return registerBlockItem(block, new BlockItem(block, new Item.Properties()));
    }

    private static Holder<Item> registerBlockItem(Holder<Block> holder, BlockItem blockItem) {
        return registerBlockItem(holder.value(), blockItem);
    }

    private static Holder<Item> registerBlockItem(Block block, BlockItem blockItem) {
        return registerBlockItem(BuiltInRegistries.BLOCK.getKey(block).getPath(), blockItem);
    }

    private static Holder<Item> registerBlockItem(String name, BlockItem blockItem) {
        return Registry.registerForHolder(BuiltInRegistries.ITEM, KlaxonCommon.locate(name), blockItem);
    }

    /**
     * Creates an ItemStack that illustrates the given block, for use in recipe viewers and machine output displays.
     * @param block The block to attempt item yonkage on.
     * @return New ItemStack of block's item with size 1 if block has an associated BlockItem, and outputs a barrier with lore if no BlockItem is present.
     */
    public static ItemStack getBlockDisplayStack(Block block) {
        Item item = block.asItem();

        // try to yonk the pick stack
        if (item == null || item.equals(Items.AIR)) {
            try {
                item = block.getCloneItemStack(null, null, null).getItem();
            } catch (Exception ignored) {

            }
        }

        // create items based off of blockitems if possible.
        // if a block doesn't have a blockitem, fall back to barrier with lore
        if (item == null || item.equals(Items.AIR)) {
            ItemStack displayStack = new ItemStack(Items.BARRIER);

            // apply components
            displayStack.applyComponents(DataComponentMap.builder()
                    .set(DataComponents.ITEM_NAME, block.getName().withStyle(ChatFormatting.RED))
                    .set(DataComponents.LORE, new ItemLore(
                            List.of(Component.translatable("klaxon.text.tooltip.missing_block_item")
                                    .withStyle(ChatFormatting.BOLD))
                    )).build()
            );

            return displayStack;
        } else {
            ItemStack displayStack = new ItemStack(item);
            displayStack.applyComponents(DataComponentMap.builder()
                    .set(DataComponents.ITEM_NAME, block.getName())
                    .build()
            );
            return displayStack;
        }
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's BlockItems!");
    }
}
