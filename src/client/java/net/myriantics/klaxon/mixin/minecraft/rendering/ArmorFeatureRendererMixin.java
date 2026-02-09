package net.myriantics.klaxon.mixin.minecraft.rendering;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.RotationAxis;
import net.myriantics.klaxon.registry.KlaxonEntityModelLayers;
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

@Mixin(ArmorFeatureRenderer.class)
public abstract class ArmorFeatureRendererMixin<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {
    @Unique
    private static final Identifier TEXTURE = KlaxonTextures.decorate(KlaxonTextures.HELMET_CREST);
    @Unique
    private static final float ARMOR_SCALE = 1.25f;

    @Shadow
    protected abstract void setVisible(A bipedModel, EquipmentSlot slot);

    @Shadow
    @Final
    private A innerModel;
    @Unique
    private HelmetCrestEntityModel<T> klaxon$helmetCrestModel = null;

    public ArmorFeatureRendererMixin(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @Inject(
            method = "<init>",
            at = @At(value = "TAIL")
    )
    private void klaxon$initHelmetCrestModel(FeatureRendererContext<T, M> context, BipedEntityModel<T> innerModel, BipedEntityModel<T> outerModel, BakedModelManager bakery, CallbackInfo ci) {
        this.klaxon$helmetCrestModel = new HelmetCrestEntityModel<>(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(KlaxonEntityModelLayers.HELMET_CREST));
    }

    @Inject(
            method = "renderArmor",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;hasGlint()Z")
    )
    private void klaxon$renderHelmetCrest(
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            T entity,
            EquipmentSlot armorSlot,
            int light,
            A model,
            CallbackInfo ci
    ) {
        ItemStack stack = entity.getEquippedStack(armorSlot);
        if (armorSlot != EquipmentSlot.HEAD || !stack.contains(KlaxonDataComponentTypes.HELMET_CREST_COMPONENT)) {
            return;
        }

        matrices.push();

        if (this.getContextModel().child && ((AnimalModelAccessor) this.getContextModel()).klaxon$headScaled()) {
            float f = 1.5F / ((AnimalModelAccessor) this.getContextModel()).klaxon$invertedChildHeadScale();
            matrices.scale(f, f, f);

            matrices.translate(0.0F, ((AnimalModelAccessor) this.getContextModel()).klaxon$childHeadYOffset() / 16.0F, ((AnimalModelAccessor) this.getContextModel()).klaxon$childHeadZOffset() / 16.0F);
        }

        this.innerModel.copyStateTo(this.klaxon$helmetCrestModel);
        this.getContextModel().head.rotate(matrices);
        // matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90f));
        matrices.scale(ARMOR_SCALE, -ARMOR_SCALE, ARMOR_SCALE);

        this.klaxon$helmetCrestModel.render(
                matrices,
                ItemRenderer.getArmorGlintConsumer(
                        vertexConsumers, RenderLayer.getArmorCutoutNoCull(TEXTURE), stack.hasGlint()
                ),
                light,
                OverlayTexture.DEFAULT_UV,
                ColorHelper.Argb.fullAlpha(DyedColorComponent.getColor(stack, Colors.WHITE))
        );

        matrices.pop();
    }
}
