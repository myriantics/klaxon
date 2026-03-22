package net.myriantics.klaxon.registry;

import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.myriantics.klaxon.registry.render.KlaxonFeatureRenderers;

public class KlaxonClientEventListeners {
    public static void initClient() {
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register(KlaxonFeatureRenderers::init);
    }
}
