package net.myriantics.klaxon.registry.render;

import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.render.model.ExplosiveDeepslateChunkEntityModel;
import net.myriantics.klaxon.render.model.HelmetCrestEntityModel;

public abstract class KlaxonEntityModelLayers {
    public static final ModelLayerLocation HELMET_CREST = register("helmet_crest", HelmetCrestEntityModel::getTexturedModelData);
    public static final ModelLayerLocation EXPLOSIVE_DEEPSLATE_CHUNK = register("explosive_deepslate_chunk", ExplosiveDeepslateChunkEntityModel::getTexturedModelData);

    private static ModelLayerLocation register(String name, EntityModelLayerRegistry.TexturedModelDataProvider provider) {
        ModelLayerLocation layer = new ModelLayerLocation(KlaxonCommon.locate(name), "main");
        EntityModelLayerRegistry.registerModelLayer(layer, provider);
        return layer;
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Entity Model Layers!");
    }
}
