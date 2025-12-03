package net.myriantics.klaxon.mixin.minecraft.feature_renderers;

import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.myriantics.klaxon.render.HelmetCrestFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorFeatureRenderer.class)
public abstract class ArmorFeatureRendererMixin<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {
    public ArmorFeatureRendererMixin(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @Unique
    private HelmetCrestFeatureRenderer<T, M, A> klaxon$helmetCrestRenderer;

    @Inject(
            method = "<init>",
            at = @At(value = "TAIL")
    )
    private void klaxon$initHelmetCrestRenderer(FeatureRendererContext<T, M> context, A innerModel, A outerModel, BakedModelManager bakery, CallbackInfo ci) {
        this.klaxon$helmetCrestRenderer = new HelmetCrestFeatureRenderer<>(context, (ArmorFeatureRenderer<T, M, A>) (Object) this);
    }

    @Inject(
            method = "setVisible",
            at = @At(value = "TAIL")
    )
    private void klaxon$setHelmetCrestVisible(A bipedModel, EquipmentSlot slot, CallbackInfo ci) {
        if (slot.equals(EquipmentSlot.HEAD)) {
            this.klaxon$helmetCrestRenderer.setVisible(true);
        }
    }


}
