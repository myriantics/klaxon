package net.myriantics.klaxon.registry;

import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.render.HelmetCrestFeatureRenderer;

import java.util.ArrayList;
import java.util.function.Supplier;

public abstract class KlaxonFeatureRenderers {

    public static <T extends LivingEntity, L extends EntityModel<T>>  void init(EntityType<? extends LivingEntity> entityType, LivingEntityRenderer<T, L> livingEntityRenderer, LivingEntityFeatureRendererRegistrationCallback.RegistrationHelper registrationHelper, EntityRendererFactory.Context context) {
        if (livingEntityRenderer.getModel() instanceof ModelWithHead) {
            registrationHelper.register(new HelmetCrestFeatureRenderer<T, L>(livingEntityRenderer, context.getModelLoader()));
        }
    }
}
