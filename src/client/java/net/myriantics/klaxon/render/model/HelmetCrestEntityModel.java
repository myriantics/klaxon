package net.myriantics.klaxon.render.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.myriantics.klaxon.registry.KlaxonEntityModelPartNames;

public class HelmetCrestEntityModel<T extends LivingEntity> extends AnimalModel<T> {

    private final ImmutableList<ModelPart> headParts;

    private final ModelPart crestUpper;
    /*
    private final ModelPart crestBack;
    private final ModelPart crestBaseUpper;
    private final ModelPart crestBaseBack;
    private final ModelPart upperSupport;
    private final ModelPart backSupport;*/

    public HelmetCrestEntityModel(ModelPart root) {
        this.crestUpper = root.getChild(KlaxonEntityModelPartNames.HELMET_CREST_UPPER);
        /*
        this.crestBack = root.getChild(KlaxonEntityModelPartNames.HELMET_CREST_BACK);
        this.crestBaseUpper = root.getChild(KlaxonEntityModelPartNames.HELMET_CREST_BASE_UPPER);
        this.crestBaseBack = root.getChild(KlaxonEntityModelPartNames.HELMET_CREST_BASE_BACK);
        this.upperSupport = root.getChild(KlaxonEntityModelPartNames.HELMET_CREST_UPPER_SUPPORT);
        this.backSupport = root.getChild(KlaxonEntityModelPartNames.HELMET_CREST_BACK_SUPPORT);
         */
        this.headParts = ImmutableList.of(this.crestUpper);
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
                ModelPartBuilder.create().uv(0, 32).cuboid(-1f, 6f, -4f, 2, 3, 16, dilation),
                ModelTransform.NONE
        );
        /*
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


         */

        return TexturedModelData.of(modelData, 32, 32);
    }
    @Override
    protected Iterable<ModelPart> getHeadParts() {
        return this.headParts;
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of();
    }
}
