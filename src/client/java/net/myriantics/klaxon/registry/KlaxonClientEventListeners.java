package net.myriantics.klaxon.registry;

import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;

public class KlaxonClientEventListeners {
    public static void initClient() {
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register(KlaxonFeatureRenderers::init);
    }
}
