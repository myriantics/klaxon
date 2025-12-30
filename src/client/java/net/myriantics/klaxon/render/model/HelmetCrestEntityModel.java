package net.myriantics.klaxon.render.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.myriantics.klaxon.registry.KlaxonEntityModelPartNames;

public class HelmetCrestEntityModel<T extends LivingEntity> extends AnimalModel<T> {

    private final ImmutableList<ModelPart> allParts;
    private final ImmutableList<ModelPart> crestParts; // parts to be dyed
    private final ImmutableList<ModelPart> supportAndBaseParts; // parts that shalt not be dyed

    private final ModelPart crestUpper;
    private final ModelPart crestBack;
    private final ModelPart crestBaseUpper;
    private final ModelPart crestBaseBack;
    private final ModelPart upperSupport;
    private final ModelPart edgeSupport;
    private final ModelPart backSupport;

    public HelmetCrestEntityModel(ModelPart root) {
        this.crestUpper = root.getChild(KlaxonEntityModelPartNames.HELMET_CREST_UPPER);
        this.crestBack = root.getChild(KlaxonEntityModelPartNames.HELMET_CREST_BACK);
        this.crestBaseUpper = root.getChild(KlaxonEntityModelPartNames.HELMET_CREST_BASE_UPPER);
        this.crestBaseBack = root.getChild(KlaxonEntityModelPartNames.HELMET_CREST_BASE_BACK);
        this.upperSupport = root.getChild(KlaxonEntityModelPartNames.HELMET_CREST_UPPER_SUPPORT);
        this.edgeSupport = root.getChild(KlaxonEntityModelPartNames.HELMET_CREST_EDGE_SUPPORT);
        this.backSupport = root.getChild(KlaxonEntityModelPartNames.HELMET_CREST_BACK_SUPPORT);
        this.allParts = ImmutableList.of(
                this.crestUpper, this.crestBack,
                this.crestBaseUpper, this.crestBaseBack,
                this.upperSupport, this.edgeSupport, this.backSupport
        );
        this.crestParts = ImmutableList.of(
                this.crestUpper, this.crestBack
        );
        this.supportAndBaseParts = ImmutableList.of(
                this.crestBaseUpper, this.crestBaseBack,
                this.upperSupport, this.edgeSupport, this.backSupport
        );
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        Dilation dilation = new Dilation(1.0F);
        modelPartData.addChild(
                KlaxonEntityModelPartNames.HELMET_CREST_UPPER,
                ModelPartBuilder.create()
                        .uv(0, 32)
                        .cuboid(-1.501f, 2.8122f, -7.8115f, 3.002f, 3.751f, 15f, dilation),
                ModelTransform.NONE
        );
        modelPartData.addChild(
                KlaxonEntityModelPartNames.HELMET_CREST_BACK,
                ModelPartBuilder.create()
                        .uv(0, 32)
                        .cuboid(-1.5f, -7.1888f, 3.4375f, 3f, 13.751f, 3.75f, dilation),
                ModelTransform.NONE
        );
        modelPartData.addChild(
                KlaxonEntityModelPartNames.HELMET_CREST_BASE_UPPER,
                ModelPartBuilder.create()
                        .uv(0, 32)
                        .cuboid(-2.501f, 1.5622f, -7.8125f, 5f, 1.251f, 11.251f, dilation),
                ModelTransform.NONE
        );
        modelPartData.addChild(
                KlaxonEntityModelPartNames.HELMET_CREST_BASE_BACK,
                ModelPartBuilder.create()
                        .uv(0, 32)
                        .cuboid(-2.5f, -7.1878f, 2.1875f, 5f, 10f, 1.25f, dilation),
                ModelTransform.NONE
        );
        modelPartData.addChild(
                KlaxonEntityModelPartNames.HELMET_CREST_UPPER_SUPPORT,
                ModelPartBuilder.create()
                        .uv(0, 32)
                        .cuboid(-2.5f, 0.3113f, -7.8125f, 5f, 1.25f, 5f, dilation),
                ModelTransform.NONE
        );
        modelPartData.addChild(
                KlaxonEntityModelPartNames.HELMET_CREST_EDGE_SUPPORT,
                ModelPartBuilder.create()
                        .uv(0, 32)
                        .cuboid(-2.499f, -0.9378f, -0.3125f, 5f, 3.75f, 3.75f, dilation),
                ModelTransform.NONE
        );
        modelPartData.addChild(
                KlaxonEntityModelPartNames.HELMET_CREST_BACK_SUPPORT,
                ModelPartBuilder.create()
                        .uv(0, 32)
                        .cuboid(-2.5f, -7.1878f, 0.9365f, 5f, 5f, 1.25f, dilation),
                ModelTransform.NONE
        );

        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        // rendered dyed
        this.crestParts.forEach(modelPart -> modelPart.render(matrices, vertices, light, overlay, color));
        // not rendered dyed
        this.supportAndBaseParts.forEach(modelPart -> modelPart.render(matrices, vertices, light, overlay));
    }

    @Override
    protected Iterable<ModelPart> getHeadParts() {
        // probably good practice to still have this return something in case something uses it
        // even though it's not used by me for rendering
        return this.allParts;
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of();
    }
}
