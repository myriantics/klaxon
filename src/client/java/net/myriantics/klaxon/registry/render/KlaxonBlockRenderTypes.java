package net.myriantics.klaxon.registry.render;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;

public abstract class KlaxonBlockRenderTypes {

    static {
        register(KlaxonBlocks.HALLNOX_BULB, RenderType.cutout());
        register(KlaxonBlocks.HALLNOX_DOOR, RenderType.cutout());
        register(KlaxonBlocks.HALLNOX_TRAPDOOR, RenderType.cutout());
        register(KlaxonBlocks.STEEL_DOOR, RenderType.cutout());
        register(KlaxonBlocks.STEEL_TRAPDOOR, RenderType.cutout());
        register(KlaxonBlocks.CRUDE_STEEL_DOOR, RenderType.cutout());
        register(KlaxonBlocks.CRUDE_STEEL_TRAPDOOR, RenderType.cutout());
        register(KlaxonBlocks.NETHER_REACTOR_CORE, RenderType.cutout());
        register(KlaxonBlocks.CRUDE_NETHER_REACTOR_CORE, RenderType.cutout());
        register(KlaxonBlocks.MODULAR_EXPLOSIVE_BLOCK, RenderType.cutout());
    }


    private static void register(Holder<Block> holder, RenderType renderType) {
        register(holder.value(), renderType);
    }

    private static void register(Block block, RenderType renderType) {
        BlockRenderLayerMap.INSTANCE.putBlock(block, renderType);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Initialized KLAXON's Block Render Types!");
    }
}
