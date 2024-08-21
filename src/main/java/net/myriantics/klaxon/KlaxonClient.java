package net.myriantics.klaxon;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.myriantics.klaxon.block.blockentities.blast_processor.BlastProcessorScreen;
import net.myriantics.klaxon.entity.KlaxonEntities;
import net.myriantics.klaxon.networking.KlaxonS2CPacketReciever;

public class KlaxonClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        HandledScreens.register(KlaxonMain.BLAST_PROCESSOR_SCREEN_HANDLER, BlastProcessorScreen::new);

        EntityRendererRegistry.register(KlaxonEntities.ENDER_PEARL_PLATE_ENTITY_TYPE, (context) ->
                new FlyingItemEntityRenderer<>(context));

        KlaxonS2CPacketReciever.registerS2CRecievers();
    }
}
