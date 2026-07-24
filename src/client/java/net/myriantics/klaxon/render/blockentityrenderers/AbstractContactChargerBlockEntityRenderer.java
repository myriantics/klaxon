package net.myriantics.klaxon.render.blockentityrenderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.myriantics.klaxon.block.machines.energy.contact_charger.BaseContactChargerBlockEntity;
import net.myriantics.klaxon.block.machines.energy.contact_charger.ContactChargerBlock;
import org.jetbrains.annotations.Nullable;

public class AbstractContactChargerBlockEntityRenderer<T extends BaseContactChargerBlockEntity> implements BlockEntityRenderer<T> {
    private final ItemRenderer itemRenderer;

    protected AbstractContactChargerBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(T blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (blockEntity.getLevel() != null) {
            @Nullable ItemStack stack = blockEntity.getChargingStack();
            if (stack != null && !stack.isEmpty()) {
                BlockState state = blockEntity.getBlockState();
                Direction facing = state.getValue(ContactChargerBlock.FACING);
                AttachFace attachedFace = state.getValue(ContactChargerBlock.FACE);

                Direction frontDirection = switch (attachedFace) {
                    case FLOOR -> Direction.UP;
                    case WALL -> facing;
                    case CEILING -> Direction.DOWN;
                };
                Direction topDirection = switch (attachedFace) {
                    case FLOOR, CEILING -> facing;
                    case WALL -> Direction.UP;
                };

                poseStack.pushPose();
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.translate(frontDirection.getStepX() * -0.33, frontDirection.getStepY() * -0.33, frontDirection.getStepZ() * -0.33);
                poseStack.scale(0.5f, 0.5f, 0.5f);
                float yRot = 180 - switch (attachedFace) {
                    case FLOOR, CEILING -> topDirection.getOpposite().toYRot();
                    case WALL -> frontDirection.toYRot();
                };
                poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
                float xRot = switch (attachedFace) {
                    case FLOOR -> 90f;
                    case WALL -> 0.0f;
                    case CEILING -> -90f;
                };
                if (xRot != 0) {
                    poseStack.mulPose(Axis.XP.rotationDegrees(xRot));
                }


                this.renderItemStack(blockEntity, stack, poseStack, partialTick, bufferSource, packedLight, packedOverlay);

                poseStack.popPose();
            }
        }
    }

    protected void renderItemStack(T blockEntity, ItemStack stack, PoseStack poseStack, float partialTick, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        this.itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, blockEntity.getLevel(), 0);
    }
}
