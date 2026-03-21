package net.myriantics.klaxon.registry;

import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

public abstract class KlaxonFeatureRenderers {

    public static <T extends LivingEntity, L extends EntityModel<T>>  void init(EntityType<? extends LivingEntity> entityType, LivingEntityRenderer<T, L> livingEntityRenderer, LivingEntityFeatureRendererRegistrationCallback.RegistrationHelper registrationHelper, EntityRendererProvider.Context context) {

    }
}
