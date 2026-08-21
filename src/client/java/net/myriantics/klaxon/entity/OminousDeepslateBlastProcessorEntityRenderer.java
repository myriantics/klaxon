package net.myriantics.klaxon.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlock;
import net.myriantics.klaxon.entity.entities.mob.ominous_deepslate_blast_processor.OminousDeepslateBlastProcessorEntity;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;

public class OminousDeepslateBlastProcessorEntityRenderer extends EntityRenderer<OminousDeepslateBlastProcessorEntity> {

    protected final BlockState DEEPSLATE_BLAST_PROCESSOR_LIT = KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR.value().defaultBlockState().setValue(DeepslateBlastProcessorBlock.LIT, true);
    protected BlockRenderDispatcher blockRenderDispatcher;

    public OminousDeepslateBlastProcessorEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.blockRenderDispatcher = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(OminousDeepslateBlastProcessorEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        float headYRotation = Mth.rotLerp(partialTick, entity.yHeadRotO, entity.yHeadRot);
        float headXRotation = Mth.rotLerp(partialTick, entity.xRotO, entity.getXRot());
        poseStack.pushPose();
        poseStack.translate(-0.5, 0, -0.5);
        poseStack.rotateAround(Axis.YN.rotationDegrees(headYRotation + 180), 0.5f, 0.5f, 0.5f);
        poseStack.rotateAround(Axis.XN.rotationDegrees(headXRotation), 0.5f, 0.5f, 0.5f);
        this.blockRenderDispatcher.renderSingleBlock(DEEPSLATE_BLAST_PROCESSOR_LIT, poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }

    protected int getBlockLightLevel(OminousDeepslateBlastProcessorEntity entity, BlockPos pos) {
        return 15;
    }

    @Override
    public ResourceLocation getTextureLocation(OminousDeepslateBlastProcessorEntity entity) {
        return null;
    }
}
