package net.myriantics.klaxon.registry;

import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;

public abstract class KlaxonFeatureRenderers {

    public static <T extends LivingEntity, L extends EntityModel<T>>  void init(EntityType<? extends LivingEntity> entityType, LivingEntityRenderer<T, L> livingEntityRenderer, LivingEntityFeatureRendererRegistrationCallback.RegistrationHelper registrationHelper, EntityRendererFactory.Context context) {

    }
}
