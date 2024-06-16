package net.myriantics.klaxon;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.myriantics.klaxon.entity.KlaxonEntities;

public class KlaxonClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(KlaxonEntities.ENDER_PEARL_PLATE_ENTITY_TYPE, (context) ->
                new FlyingItemEntityRenderer<>(context));
    }
}
