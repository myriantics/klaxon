package net.myriantics.klaxon.mechanics.wrench;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.util.CommonColors;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Objects;

public class BlockFaceGroup {
    private final Direction facing;
    private final Vector3f clickedPos;
    private static final float TOLERANCE = 0.01f;

    private final ArrayList<Segment> segments = new ArrayList<>();

    public BlockFaceGroup(Direction facing, Vector3f clickedPos) {
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
            Segment segment = switch (facing) {
                case UP -> new Segment(1 - x1, 1 - z1, 1 - x2, 1 - z2);
                case DOWN -> new Segment(1 - x1, z1, 1 - x2, z2);
                case NORTH -> new Segment(x1, y1, x2, y2);
                case SOUTH -> new Segment(1 - x1, y1,1 - x2, y2);
                case EAST -> new Segment(z1, y1, z2, y2);
                case WEST -> new Segment(1 - z1, y1, 1 - z2, y2);
            };

            if (!segment.validate()) {
                return;
            }

            if (this.segments.isEmpty()) {
                this.segments.add(segment);
            } else {
                for (Segment existing : this.segments) {
                    if (existing.canMergeWith(segment)) {
                        existing.merge(segment);
                        return;
                    }
                }
            }

            this.segments.add(segment);
        }
    }

    private static boolean isBetweenInc(float val, float min, float max) {
        return val >= min - TOLERANCE && val <= max + TOLERANCE;
    }

    private static boolean testWithAllowance(float a, float b) {
        return isBetweenInc(a, b - TOLERANCE, b + TOLERANCE);
    }

    public void renderSelected(PoseStack.Pose pose, VertexConsumer consumer, int light) {
        float x = switch (facing) {
            case DOWN -> 1 - this.clickedPos.x;
            case UP -> 1 - this.clickedPos.x;
            case NORTH -> this.clickedPos.x;
            case SOUTH -> 1 -this.clickedPos.x;
            case WEST -> 1 - this.clickedPos.z;
            case EAST -> this.clickedPos.z;
        };
        float y = switch (facing) {
            case DOWN -> this.clickedPos.z;
            case UP -> 1 - this.clickedPos.z;
            case NORTH -> this.clickedPos.y;
            case SOUTH -> this.clickedPos.y;
            case WEST -> this.clickedPos.y;
            case EAST -> this.clickedPos.y;
        };

        for (Segment segment : this.segments) {
            if (segment.contains(x, y)) {
                segment.render(pose, consumer, light);
                break;
            }
        }
    }

    private static final class Segment {
        private float xMin;
        private float yMin;
        private float xMax;
        private float yMax;

            private Segment(float xMin, float yMin, float xMax, float yMax) {
                this.xMin = Math.min(xMin, xMax);
                this.yMin = Math.min(yMin, yMax);
                this.xMax = Math.max(xMin, xMax);
                this.yMax = Math.max(yMin, yMax);
            }

            private boolean validate() {
                return this.xMin != this.yMin && this.xMax != this.yMax;
            }

            private boolean contains(float x, float y) {
                return isBetweenInc(x, this.xMin, this.xMax) && isBetweenInc(y, this.yMin, this.yMax);
            }

            private boolean canMergeWith(Segment other) {
                return other.xMin < this.xMin || other.xMax > this.xMax || other.yMin < this.yMin || other.yMax > this.yMax;
            }

            private boolean intersects(Segment other) {
                return (isBetweenInc(other.xMin, this.xMin, this.xMax) || isBetweenInc(other.xMax, this.xMin, this.xMax)) && (isBetweenInc(other.yMin, this.yMin, this.yMax) || isBetweenInc(other.yMax, this.yMin, this.yMax));
            }

            private void merge(Segment other) {
                this.xMin = Math.min(this.xMin, other.xMin);
                this.yMin = Math.min(this.yMin, other.yMin);
                this.xMax = Math.max(this.xMax, other.xMax);
                this.yMax = Math.max(this.yMax, other.yMax);
            }

            public void render(PoseStack.Pose pose, VertexConsumer consumer, int light) {
                vertex(pose, consumer, xMin - 0.5f, yMax - 0.5f, 0, xMin, yMin, 0, 0, 0, light);
                vertex(pose, consumer, xMax - 0.5f, yMax - 0.5f, 0, xMax, yMin, 0, 0, 0, light);
                vertex(pose, consumer, xMax - 0.5f, yMin - 0.5f, 0, xMax, yMax, 0, 0, 0, light);
                vertex(pose, consumer, xMin - 0.5f, yMin - 0.5f, 0, xMin, yMax, 0, 0, 0, light);
            }
    }

    private static void vertex(
            PoseStack.Pose poseStack, VertexConsumer vertexConsumer, float x, float y, float z, float u, float v, float normalX, float normalZ, float normalY, int light
    ) {
        vertexConsumer.addVertex(poseStack, x, y, z)
                .setColor(CommonColors.WHITE)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(poseStack, normalX, normalY, normalZ);
    }
}
