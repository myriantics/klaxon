package net.myriantics.klaxon.registry.render;

import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.render.model.HelmetCrestEntityModel;

public abstract class KlaxonEntityModelLayers {
    public static ModelLayerLocation HELMET_CREST = register("helmet_crest", HelmetCrestEntityModel::getTexturedModelData);

    private static ModelLayerLocation register(String name, EntityModelLayerRegistry.TexturedModelDataProvider provider) {
        ModelLayerLocation layer = new ModelLayerLocation(KlaxonCommon.locate(name), "main");
        EntityModelLayerRegistry.registerModelLayer(layer, provider);
        return layer;
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Entity Model Layers!");
    }
}
