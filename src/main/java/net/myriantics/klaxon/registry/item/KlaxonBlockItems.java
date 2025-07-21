package net.myriantics.klaxon.registry.item;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.SignItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;

import java.util.ArrayList;

public abstract class KlaxonBlockItems {
    public static ArrayList<Item> simpleBlockItems = new ArrayList<>();
    public static ArrayList<BlockItem> flatBlockItems = new ArrayList<>();

    public static Item HALLNOX_SIGN = registerFlatBlockItem(KlaxonBlocks.HALLNOX_SIGN,
            new SignItem(new Item.Settings(), KlaxonBlocks.HALLNOX_SIGN, KlaxonBlocks.HALLNOX_WALL_SIGN)
    );
    public static Item HALLNOX_HANGING_SIGN = registerFlatBlockItem(KlaxonBlocks.HALLNOX_HANGING_SIGN,
            new SignItem(new Item.Settings(), KlaxonBlocks.HALLNOX_HANGING_SIGN, KlaxonBlocks.HALLNOX_WALL_HANGING_SIGN)
    );

    private static Item registerSimpleFlatBlockItem(Block block) {
        return registerFlatBlockItem(block, new BlockItem(block, new Item.Settings()));
    }

    private static Item registerFlatBlockItem(Block block, BlockItem blockItem) {
        flatBlockItems.add(blockItem);
        return registerBlockItem(block, blockItem);
    }

    private static Item registerBlockItem(Block block, BlockItem blockItem) {
        return registerBlockItem(Registries.BLOCK.getId(block).getPath(), blockItem);
    }

    private static Item registerBlockItem(String name, BlockItem blockItem) {
        return Registry.register(Registries.ITEM, KlaxonCommon.locate(name), blockItem);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's BlockItems!");
    }
}
