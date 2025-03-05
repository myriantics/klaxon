package net.myriantics.klaxon;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.myriantics.klaxon.block.KlaxonBlocks;
import net.myriantics.klaxon.block.customblocks.blast_processor.deepslate.DeepslateBlastProcessorScreen;
import net.myriantics.klaxon.entity.KlaxonEntities;
import net.myriantics.klaxon.networking.KlaxonPackets;
import net.myriantics.klaxon.util.KlaxonScreenHandlers;

public class KlaxonClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        BlockRenderLayerMap.INSTANCE.putBlock(KlaxonBlocks.STEEL_DOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(KlaxonBlocks.STEEL_TRAPDOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(KlaxonBlocks.CRUDE_STEEL_DOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(KlaxonBlocks.CRUDE_STEEL_TRAPDOOR, RenderLayer.getCutout());

        HandledScreens.register(KlaxonScreenHandlers.BLAST_PROCESSOR_SCREEN_HANDLER, DeepslateBlastProcessorScreen::new);

        EntityRendererRegistry.register(KlaxonEntities.ENDER_PEARL_PLATE_ENTITY_TYPE, (context) ->
                new FlyingItemEntityRenderer<>(context));


        KlaxonPackets.registerS2CPacketRecievers();
    }
}
