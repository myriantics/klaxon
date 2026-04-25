package net.myriantics.klaxon.mechanics.wrench;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.myriantics.klaxon.util.BlockFaceRegion;
import org.joml.Vector3f;

import java.util.ArrayList;

public class SelectedFaceCalculator {
    private final Direction facing;
    private final Vector3f clickedPos;
    private static final float TOLERANCE = 0.01f;
    private static final int ALPHA = Math.round(255.0F * 0.5f);

    private final BlockFaceRegion.Builder builder = BlockFaceRegion.builder();

    public SelectedFaceCalculator(Direction facing, Vector3f clickedPos) {
        this.facing = facing;
        this.clickedPos = clickedPos;
    }

    public void tryAdd(double x1, double y1, double z1, double x2, double y2, double z2) {
        this.tryAdd((float) x1, (float) y1, (float) z1, (float) x2, (float) y2, (float) z2);
    }

    public void tryAdd(float x1, float y1, float z1, float x2, float y2, float z2) {
        if (switch (this.facing.getAxis()) {
            case X -> (testWithAllowance(this.clickedPos.x, x1) || testWithAllowance(this.clickedPos.x, x2));
            case Y -> (testWithAllowance(this.clickedPos.y, y1) || testWithAllowance(this.clickedPos.y, y2));
            case Z -> (testWithAllowance(this.clickedPos.z, z1) || testWithAllowance(this.clickedPos.z, z2));
        }) {
            switch (this.facing) {
                case UP -> this.builder.tryMerge(1 - x1, 1 - z1, 1 - x2, 1 - z2);
                case DOWN -> this.builder.tryMerge(1 - x1, z1, 1 - x2, z2);
                case NORTH -> this.builder.tryMerge(x1, y1, x2, y2);
                case SOUTH -> this.builder.tryMerge(1 - x1, y1,1 - x2, y2);
                case EAST -> this.builder.tryMerge(z1, y1, z2, y2);
                case WEST -> this.builder.tryMerge(1 - z1, y1, 1 - z2, y2);
            };
        }
    }

    public BlockFaceRegion get() {
        return this.builder.build();
    }

    public static boolean isBetweenInc(float val, float min, float max) {
        return val >= min - TOLERANCE && val <= max + TOLERANCE;
    }

    public static boolean testWithAllowance(float a, float b) {
        return isBetweenInc(a, b, b);
    }
}
