package net.myriantics.klaxon.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonMain;
import net.myriantics.klaxon.block.customblocks.CrudeExtrapolatorBlock;

public class ModBlocks {

    public static final Block CRUDE_EXTRAPOLATOR = registerBlock("crude_extrapolator",
            new CrudeExtrapolatorBlock(FabricBlockSettings.copyOf(Blocks.OBSIDIAN).strength(20.0f, 50.0f)));

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(KlaxonMain.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, new Identifier(KlaxonMain.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings()));
    }

    public static final void registerModBlocks() {
        KlaxonMain.LOGGER.info("Registering Blocks for Klaxon");
    }

}
