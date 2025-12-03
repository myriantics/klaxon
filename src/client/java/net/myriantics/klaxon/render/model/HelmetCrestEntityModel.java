package net.myriantics.klaxon.render.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.myriantics.klaxon.registry.KlaxonEntityModelPartNames;

public class HelmetCrestEntityModel<T extends LivingEntity> extends AnimalModel<T> {

    private final ModelPart crestUpper;
    private final ModelPart crestBack;
    private final ModelPart crestModelUpper;
    private final ModelPart crestModelBack;
    private final ModelPart frontSupport;
    private final ModelPart backSupport;

    public HelmetCrestEntityModel(ModelPart root) {
        this.crestUpper = root.getChild(KlaxonEntityModelPartNames.HELMET_CREST_UPPER);
        this.crestBack = root.getChild(KlaxonEntityModelPartNames.HELMET_CREST_UPPER);
        this.crestModelUpper = root.getChild(KlaxonEntityModelPartNames.HELMET_CREST_UPPER);
        this.crestModelBack = root.getChild(KlaxonEntityModelPartNames.HELMET_CREST_UPPER);
        this.frontSupport = root.getChild(KlaxonEntityModelPartNames.HELMET_CREST_UPPER);
        this.backSupport = root.getChild(KlaxonEntityModelPartNames.HELMET_CREST_UPPER);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {

    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        Dilation dilation = new Dilation(1.0F);
        modelPartData.addChild(
                KlaxonEntityModelPartNames.HELMET_CREST_UPPER,
                ModelPartBuilder.create(),
                ModelTransform.NONE
        );
        modelPartData.addChild(
                KlaxonEntityModelPartNames.HELMET_CREST_BACK,
                ModelPartBuilder.create(),
                ModelTransform.NONE
        );
        modelPartData.addChild(
                KlaxonEntityModelPartNames.HELMET_CREST_BASE_UPPER,
                ModelPartBuilder.create(),
                ModelTransform.NONE
        );
        modelPartData.addChild(
                KlaxonEntityModelPartNames.HELMET_CREST_BASE_BACK,
                ModelPartBuilder.create(),
                ModelTransform.NONE
        );
        modelPartData.addChild(
                KlaxonEntityModelPartNames.HELMET_CREST_UPPER_SUPPORT,
                ModelPartBuilder.create(),
                ModelTransform.NONE
        );
        modelPartData.addChild(
                KlaxonEntityModelPartNames.HELMET_CREST_BACK_SUPPORT,
                ModelPartBuilder.create(),
                ModelTransform.NONE
        );

        return TexturedModelData.of(modelData, 64, 32);
    }

    @Override
    protected Iterable<ModelPart> getHeadParts() {
        return null;
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of();
    }
}
