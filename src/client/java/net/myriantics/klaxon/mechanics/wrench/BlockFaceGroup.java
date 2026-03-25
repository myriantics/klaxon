package net.myriantics.klaxon.mechanics.wrench;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.util.CommonColors;
import org.joml.Vector3f;

import javax.swing.text.Segment;
import java.util.ArrayList;

public class BlockFaceGroup {
    private final Direction facing;
    private final Vector3f clickedPos;
    private static final float TOLERANCE = 0.01f;

    private final ArrayList<Group> groups = new ArrayList<>();

    public BlockFaceGroup(Direction facing, Vector3f clickedPos) {
        this.facing = facing;
        this.clickedPos = clickedPos;
        this.groups.add(new Group());
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
            GroupSegment segment = switch (facing) {
                case UP -> new GroupSegment(1 - x2, 1 - z2, 1 - x1, 1 - z1);
                case DOWN -> new GroupSegment(1 - x2, z1, 1 - x1, z2);
                case NORTH -> new GroupSegment(x1, y1, x2, y2);
                case SOUTH -> new GroupSegment(1 - x2, y1, 1 - x1, y2);
                case EAST -> new GroupSegment(z1, y1, z2, y2);
                case WEST -> new GroupSegment(1 - z2, y1, 1 - z1, y2);
            };

            for (Group group : this.groups) {
                if (group.tryAdd(segment)) {
                    return;
                }
            }

            this.groups.add(new Group(segment));
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
            case SOUTH -> 1 - this.clickedPos.x;
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

        Group[] renderables = new Group[this.groups.size()];

        // crusty 3am code that stitches the groups together
        for (int i = 0; i < this.groups.size(); i++) {
            Group group = this.groups.get(i);
            if (renderables[i] == group || group.contains(x, y)) {
                for (int j = 0; j < this.groups.size(); j++) {
                    Group group2 = this.groups.get(j);
                    for (GroupSegment segment : group.segments) {
                        if (group2.overlapsWith(segment)) {
                            renderables[j] = group2;
                        }
                    }
                }
                renderables[i] = group;
            }
        }

        for (Group group : renderables) {
            if (group != null) {
                group.render(pose, consumer, light);
            }
        }
    }

    private record GroupSegment(float xMin, float yMin, float xMax, float yMax) {
        private GroupSegment {
        }

        private boolean contains(float x, float y) {
            return isBetweenInc(x, this.xMin, this.xMax) && isBetweenInc(y, this.yMin, this.yMax);
        }

        private boolean overlapsWith(GroupSegment other) {
            return (isBetweenInc(other.xMin, this.xMin, this.xMax) || isBetweenInc(other.xMax, this.xMin, this.xMax)) && (isBetweenInc(other.yMin, this.yMin, this.yMax) || isBetweenInc(other.yMax, this.yMin, this.yMax));
        }

        public void render(PoseStack.Pose pose, VertexConsumer consumer, int light) {
            vertex(pose, consumer, xMin - 0.5f,yMax - 0.5f, 0, xMin ,yMin, 0, 0, 0, light);
            vertex(pose, consumer, xMax - 0.5f,yMax - 0.5f, 0, xMax ,yMin, 0, 0, 0, light);
            vertex(pose, consumer, xMax - 0.5f,yMin - 0.5f, 0, xMax ,yMax, 0, 0, 0, light);
            vertex(pose, consumer, xMin - 0.5f,yMin - 0.5f, 0, xMin , yMax, 0, 0, 0, light);
        }
    }

    private static class Group {

        private final ArrayList<GroupSegment> segments = new ArrayList<>();

        private Group() {
        }

        private Group(GroupSegment segment) {
            this.segments.add(segment);
        }

        private boolean contains(float x, float y) {
            for (GroupSegment segment : this.segments) {
                if (segment.contains(x, y)) {
                    return true;
                }
            }
            return false;
        }

        private boolean overlapsWith(GroupSegment segment) {
            for (GroupSegment existing : this.segments) {
                if (existing.overlapsWith(segment)) {
                    return true;
                }
            }
            return false;
        }

        private boolean tryAdd(GroupSegment segment) {
            if (this.segments.isEmpty()) {
                this.segments.add(segment);
                return true;
            }

            if (overlapsWith(segment)) {
                this.segments.add(segment);
                return true;
            } else {
                return false;
            }
        }

        public void render(PoseStack.Pose pose, VertexConsumer consumer, int light) {
            for (GroupSegment segment : this.segments) {
                segment.render(pose, consumer, light);
            }
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
