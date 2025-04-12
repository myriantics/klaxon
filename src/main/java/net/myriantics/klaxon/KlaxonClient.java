package net.myriantics.klaxon;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.myriantics.klaxon.registry.minecraft.KlaxonBlocks;
import net.myriantics.klaxon.block.customblocks.machines.blast_processor.deepslate.DeepslateBlastProcessorScreen;
import net.myriantics.klaxon.registry.minecraft.KlaxonPackets;
import net.myriantics.klaxon.registry.minecraft.KlaxonScreenHandlers;

public class KlaxonClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        BlockRenderLayerMap.INSTANCE.putBlock(KlaxonBlocks.STEEL_DOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(KlaxonBlocks.STEEL_TRAPDOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(KlaxonBlocks.CRUDE_STEEL_DOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(KlaxonBlocks.CRUDE_STEEL_TRAPDOOR, RenderLayer.getCutout());

        HandledScreens.register(KlaxonScreenHandlers.BLAST_PROCESSOR_SCREEN_HANDLER, DeepslateBlastProcessorScreen::new);

        KlaxonPackets.registerS2CPacketRecievers();
    }
}
