package net.myriantics.klaxon.render.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.LivingEntity;
import net.myriantics.klaxon.registry.render.KlaxonEntityModelPartNames;

public class HelmetCrestEntityModel<T extends LivingEntity> extends AgeableListModel<T> {

    public final ImmutableList<ModelPart> parts;

    public final ModelPart dyeableCrest;
    public final ModelPart staticCrestSupports;

    public HelmetCrestEntityModel(ModelPart root) {
        this.dyeableCrest = root.getChild(KlaxonEntityModelPartNames.HELMET_CREST_DYEABLE);
        this.staticCrestSupports = root.getChild(KlaxonEntityModelPartNames.HELMET_CREST_STATIC_SUPPORT);

        this.parts = ImmutableList.of(
                this.dyeableCrest, this.staticCrestSupports
        );
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        CubeDeformation dilation = CubeDeformation.NONE;
        // go go gadget guess and check in spectator
        // magic numbers go brrr
        // also they're the '99s to stop z fighting
        PartPose transform = PartPose.offset(0f, 9.201f, -2.999f);

        // init builder for dyeable crest parts
        CubeListBuilder dyeableCrestBuilder = CubeListBuilder.create();

        // dimensions for upper crest
        final float crestUpperX = 2f;
        final float crestUpperY = 3f;
        final float crestUpperZ = 12f;

        // add upper crest to builder
        dyeableCrestBuilder
                .texOffs(0, 0)
                .addBox(
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
                .texOffs(0, 0)
                .addBox(
                        -(crestBackX / 2), -crestBackY, crestUpperZ - crestBackZ,
                        crestBackX, crestBackY, crestBackZ,
                        dilation
                );

        // commit the dyed crest segments to the model part data
        modelPartData.addOrReplaceChild(
                KlaxonEntityModelPartNames.HELMET_CREST_DYEABLE,
                dyeableCrestBuilder,
                transform
        );

        // init the builder for the non dyed crest segments
        CubeListBuilder staticCrestSupportBuilder = CubeListBuilder.create();

        // dimensions for upper crest base
        final float crestBaseUpperX = 4f;
        final float crestBaseUpperY = 1f;
        final float crestBaseUpperZ = 9f;

        // add upper crest base to crest support builder
        staticCrestSupportBuilder
                .texOffs(0, 22)
                .addBox(
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
                .texOffs(17, 0)
                .addBox(
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
                .texOffs(11, 16)
                .addBox(
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
                .texOffs(18, 22)
                .addBox(
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
                .texOffs(0, 16)
                .addBox(
                        -(crestBackSupportX / 2), -crestBaseBackY, crestUpperZ - crestBackZ - crestBaseBackZ - crestBackSupportZ,
                        crestBackSupportX, crestBackSupportY, crestBackSupportZ,
                        dilation
                );

        // commit the static crest supports to the model part data
        modelPartData.addOrReplaceChild(
                KlaxonEntityModelPartNames.HELMET_CREST_STATIC_SUPPORT,
                staticCrestSupportBuilder,
                transform
        );

        return LayerDefinition.create(modelData, 32, 32);
    }

    @Override
    public void renderToBuffer(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        matrices.pushPose();

        this.dyeableCrest.render(matrices, vertices, light, overlay, color);
        this.staticCrestSupports.render(matrices, vertices, light, overlay);
        matrices.popPose();
    }

    @Override
    protected Iterable<ModelPart> headParts() {
        // probably good practice to still have this return something in case something uses it
        // even though it's not used by me for rendering
        return this.parts;
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of();
    }
}
