package net.myriantics.klaxon.mixin.minecraft.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CommonColors;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.myriantics.klaxon.registry.render.KlaxonEntityModelLayers;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import net.myriantics.klaxon.registry.render.KlaxonTextures;
import net.myriantics.klaxon.render.model.HelmetCrestEntityModel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public abstract class ArmorFeatureRendererMixin<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends RenderLayer<T, M> {
    @Unique
    private static final ResourceLocation TEXTURE = KlaxonTextures.decorate(KlaxonTextures.HELMET_CREST);
    @Unique
    private static final float ARMOR_SCALE = 1.25f;

    @Shadow
    protected abstract void setPartVisibility(A bipedModel, EquipmentSlot slot);

    @Shadow
    @Final
    private A innerModel;
    @Unique
    private HelmetCrestEntityModel<T> klaxon$helmetCrestModel = null;

    public ArmorFeatureRendererMixin(RenderLayerParent<T, M> context) {
        super(context);
    }

    @Inject(
            method = "<init>",
            at = @At(value = "TAIL")
    )
    private void klaxon$initHelmetCrestModel(RenderLayerParent<T, M> context, HumanoidModel<T> innerModel, HumanoidModel<T> outerModel, ModelManager bakery, CallbackInfo ci) {
        this.klaxon$helmetCrestModel = new HelmetCrestEntityModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(KlaxonEntityModelLayers.HELMET_CREST));
    }

    @Inject(
            method = "renderArmorPiece",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;hasFoil()Z")
    )
    private void klaxon$renderHelmetCrest(
            PoseStack matrices,
            MultiBufferSource vertexConsumers,
            T entity,
            EquipmentSlot armorSlot,
            int light,
            A model,
            CallbackInfo ci
    ) {
        ItemStack stack = entity.getItemBySlot(armorSlot);
        if (armorSlot != EquipmentSlot.HEAD || !stack.has(KlaxonDataComponentTypes.HELMET_CREST_COMPONENT)) {
            return;
        }

        matrices.pushPose();

        if (this.getParentModel().young && ((AgeableListModelAccessor) this.getParentModel()).klaxon$headScaled()) {
            float f = 1.5F / ((AgeableListModelAccessor) this.getParentModel()).klaxon$invertedChildHeadScale();
            matrices.scale(f, f, f);

            matrices.translate(0.0F, ((AgeableListModelAccessor) this.getParentModel()).klaxon$childHeadYOffset() / 16.0F, ((AgeableListModelAccessor) this.getParentModel()).klaxon$childHeadZOffset() / 16.0F);
        }

        this.innerModel.copyPropertiesTo(this.klaxon$helmetCrestModel);
        this.getParentModel().head.translateAndRotate(matrices);
        // matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90f));
        matrices.scale(ARMOR_SCALE, -ARMOR_SCALE, ARMOR_SCALE);

        this.klaxon$helmetCrestModel.renderToBuffer(
                matrices,
                ItemRenderer.getArmorFoilBuffer(
                        vertexConsumers, RenderType.armorCutoutNoCull(TEXTURE), stack.hasFoil()
                ),
                light,
                OverlayTexture.NO_OVERLAY,
                FastColor.ARGB32.opaque(DyedItemColor.getOrDefault(stack, CommonColors.WHITE))
        );

        matrices.popPose();
    }
}
