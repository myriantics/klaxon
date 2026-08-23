package net.myriantics.klaxon.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.myriantics.klaxon.entity.entities.projectile.explosive_deepslate_chunk.ExplosiveDeepslateChunkEntity;
import net.myriantics.klaxon.registry.render.KlaxonEntityModelPartNames;

public class ExplosiveDeepslateChunkEntityModel extends EntityModel<ExplosiveDeepslateChunkEntity> {

    public final ModelPart staticPart;
    public final ModelPart tintedPart;

    public ExplosiveDeepslateChunkEntityModel(ModelPart root) {
        this.staticPart = root.getChild(KlaxonEntityModelPartNames.EXPLOSIVE_DEEPSLATE_CHUNK_STATIC);
        this.tintedPart = root.getChild(KlaxonEntityModelPartNames.EXPLOSIVE_DEEPSLATE_CHUNK_TINTED);
    }

    @Override
    public void setupAnim(ExplosiveDeepslateChunkEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        this.staticPart.render(poseStack, buffer, packedLight, packedOverlay);
        this.tintedPart.render(poseStack, buffer, packedLight, packedOverlay, color);
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition modelPartData = meshDefinition.getRoot();
        CubeDeformation deformation = new CubeDeformation(0.01f);
        PartPose partPose = PartPose.offset(0f, 8f, 0f);

        CubeListBuilder staticPartBuilder = CubeListBuilder.create();
        staticPartBuilder
                .texOffs(0, 0)
                .addBox(-4, -4, -4, 8, 8, 8);
        modelPartData.addOrReplaceChild(
                KlaxonEntityModelPartNames.EXPLOSIVE_DEEPSLATE_CHUNK_STATIC,
                staticPartBuilder,
                partPose
        );

        CubeListBuilder tintedPartBuilder = CubeListBuilder.create();
        tintedPartBuilder
                .texOffs(0, 16)
                .addBox(
                        -4, -4, -4,
                        8, 8, 8,
                        deformation
                );
        modelPartData.addOrReplaceChild(
                KlaxonEntityModelPartNames.EXPLOSIVE_DEEPSLATE_CHUNK_TINTED,
                tintedPartBuilder,
                partPose
        );

        return LayerDefinition.create(meshDefinition, 32, 32);
    }
}
