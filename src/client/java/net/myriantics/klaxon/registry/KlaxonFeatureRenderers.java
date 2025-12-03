package net.myriantics.klaxon.registry;

import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.myriantics.klaxon.KlaxonCommon;

import java.util.ArrayList;

public abstract class KlaxonFeatureRenderers {
    public static final ArrayList<Supplier<FeatureRenderer<?, ?>>> ADDITIONAL_ARMOR_FEATURES = new ArrayList<>();

    public static void init() {
        KlaxonCommon.LOGGER.info("Initialized KLAXON's Feature Renderers!");
    }

    private static void register() {

    }

    static {

    }

    private interface FeatureRendererSupplier {
        public FeatureRenderer<?, ?> create();
    }
}
