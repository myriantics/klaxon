package net.myriantics.klaxon.render.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.myriantics.klaxon.registry.KlaxonEntityModelPartNames;

public class HelmetCrestEntityModel<T extends LivingEntity> extends AnimalModel<T> {

    private final ImmutableList<ModelPart> parts;

    private final ModelPart dyeableCrest;
    private final ModelPart staticCrestSupports;

    public HelmetCrestEntityModel(ModelPart root) {
        this.dyeableCrest = root.getChild(KlaxonEntityModelPartNames.HELMET_CREST_DYEABLE);
        this.staticCrestSupports = root.getChild(KlaxonEntityModelPartNames.HELMET_CREST_STATIC_SUPPORT);

        this.parts = ImmutableList.of(
                this.dyeableCrest, this.staticCrestSupports
        );
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        Dilation dilation = Dilation.NONE;
        // go go gadget guess and check in spectator
        // magic numbers go brrr
        // also they're the '99s to stop z fighting
        ModelTransform transform = ModelTransform.pivot(0f, 2.601f, -2.999f);

        // init builder for dyeable crest parts
        ModelPartBuilder dyeableCrestBuilder = ModelPartBuilder.create();

        // dimensions for upper crest
        final float crestUpperX = 2f;
        final float crestUpperY = 3f;
        final float crestUpperZ = 12f;

        // add upper crest to builder
        dyeableCrestBuilder
                .uv(0, 0)
                .cuboid(
                        -(crestUpperX / 2), 0f, 0f,
                        crestUpperX, crestUpperY, crestUpperZ,
                        dilation
                );

        // dimensions for back crest
        final float crestBackX = 2f;
        final float crestBackY = 8f;
        final float crestBackZ = 3f;

        // add back crest to builder
        dyeableCrestBuilder
                .uv(0, 0)
                .cuboid(
                        -(crestBackX / 2), -crestBackY, crestUpperZ - crestBackZ,
                        crestBackX, crestBackY, crestBackZ,
                        dilation
                );

        // commit the dyed crest segments to the model part data
        modelPartData.addChild(
                KlaxonEntityModelPartNames.HELMET_CREST_DYEABLE,
                dyeableCrestBuilder,
                transform
        );

        // init the builder for the non dyed crest segments
        ModelPartBuilder staticCrestSupportBuilder = ModelPartBuilder.create();

        // dimensions for upper crest base
        final float crestBaseUpperX = 4f;
        final float crestBaseUpperY = 1f;
        final float crestBaseUpperZ = 9f;

        // add upper crest base to crest support builder
        staticCrestSupportBuilder
                .uv(0, 0)
                .cuboid(
                        -(crestBaseUpperX / 2), -crestBaseUpperY, 0f,
                        crestBaseUpperX, crestBaseUpperY, crestBaseUpperZ,
                        dilation
                );

        // dimensions for back crest base
        final float crestBaseBackX = 4f;
        final float crestBaseBackY = 8f;
        final float crestBaseBackZ = 1f;

        // add back crest base to crest support builder
        staticCrestSupportBuilder
                .uv(0, 0)
                .cuboid(
                        -(crestBaseBackX / 2), -crestBaseBackY, crestUpperZ - crestBackZ - crestBaseBackZ,
                        crestBaseBackX, crestBaseBackY, crestBaseBackZ,
                        dilation
                );

        // dimensions for upper crest support
        final float crestUpperSupportX = 4f;
        final float crestUpperSupportY = 1f;
        final float crestUpperSupportZ = 4f;

        // add upper crest support to crest support builder
        staticCrestSupportBuilder
                .uv(0, 0)
                .cuboid(
                        -(crestUpperSupportX / 2), -(crestBaseUpperY + crestUpperSupportY), 0f,
                        crestUpperSupportX, crestUpperSupportY, crestUpperSupportZ,
                        dilation
                );

        // dimensions for edge crest support
        final float crestEdgeSupportX = 4f;
        final float crestEdgeSupportY = 3f;
        final float crestEdgeSupportZ = 3f;

        // add edge crest support to crest support builder
        staticCrestSupportBuilder
                .uv(0, 0)
                .cuboid(
                        -(crestEdgeSupportX / 2), -crestEdgeSupportY, crestBaseUpperZ - crestEdgeSupportZ,
                        crestEdgeSupportX, crestEdgeSupportY, crestEdgeSupportZ,
                        dilation
                );

        // dimensions for back crest support
        final float crestBackSupportX = 4f;
        final float crestBackSupportY = 4f;
        final float crestBackSupportZ = 1f;

        // add back crest support to crest support builder
        staticCrestSupportBuilder
                .uv(0, 0)
                .cuboid(
                        -(crestBackSupportX / 2), -crestBaseBackY, crestUpperZ - crestBackZ - crestBaseBackZ - crestBackSupportZ,
                        crestBackSupportX, crestBackSupportY, crestBackSupportZ,
                        dilation
                );

        // commit the static crest supports to the model part data
        modelPartData.addChild(
                KlaxonEntityModelPartNames.HELMET_CREST_STATIC_SUPPORT,
                staticCrestSupportBuilder,
                transform
        );

        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        this.dyeableCrest.render(matrices, vertices, light, overlay, color);
        this.staticCrestSupports.render(matrices, vertices, light, overlay);
    }

    @Override
    protected Iterable<ModelPart> getHeadParts() {
        // probably good practice to still have this return something in case something uses it
        // even though it's not used by me for rendering
        return this.parts;
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of();
    }
}
