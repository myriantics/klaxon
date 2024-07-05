package net.myriantics.klaxon;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.myriantics.klaxon.block.blockentities.blast_chamber.BlastChamberScreen;
import net.myriantics.klaxon.entity.KlaxonEntities;

public class KlaxonClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        HandledScreens.register(KlaxonMain.BLAST_CHAMBER_SCREEN_HANDLER, BlastChamberScreen::new);

        EntityRendererRegistry.register(KlaxonEntities.ENDER_PEARL_PLATE_ENTITY_TYPE, (context) ->
                new FlyingItemEntityRenderer<>(context));
    }
}
