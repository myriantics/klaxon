package net.myriantics.klaxon.registry;

import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.render.model.HelmetCrestEntityModel;

public abstract class KlaxonEntityModelLayers {
    public static EntityModelLayer HELMET_CREST = register("helmet_crest", HelmetCrestEntityModel::getTexturedModelData);

    private static EntityModelLayer register(String name, EntityModelLayerRegistry.TexturedModelDataProvider provider) {
        EntityModelLayer layer = new EntityModelLayer(KlaxonCommon.locate(name), "main");
        EntityModelLayerRegistry.registerModelLayer(layer, provider);
        return layer;
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Entity Model Layers!");
    }
}
