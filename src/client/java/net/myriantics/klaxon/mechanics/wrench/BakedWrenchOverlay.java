package net.myriantics.klaxon.mechanics.wrench;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.myriantics.klaxon.mechanics.wrench.interaction.WrenchInteraction;
import net.myriantics.klaxon.mechanics.wrench.interaction.WrenchInteractionMap;
import net.myriantics.klaxon.mechanics.wrench.interaction.segments.InteractionMapSegment;
import net.myriantics.klaxon.util.BlockFaceRegion;

import java.util.ArrayList;
import java.util.List;

public class BakedWrenchOverlay {
    private final List<Entry> entries;

    private BakedWrenchOverlay(List<Entry> entries) {
        this.entries = entries;
    }

    public static BakedWrenchOverlay of(BlockFaceRegion bounds, WrenchActionContext.Manual manual) {

        WrenchInteractionMap map = WrenchUtil.getInteractionMap(manual);
        BlockFaceRegion.Rotation rotation = map.getRotation(manual.getTargetState(), manual.getGuiOrientation());

        float x = manual.getGuiOrientation().getClickedX(rotation);
        float y = manual.getGuiOrientation().getClickedY(rotation);

        ArrayList<Entry> entries = new ArrayList<>(map.segments.size());
        for (InteractionMapSegment segment : map.segments) {
            WrenchInteraction interaction = segment.getInteraction();
            BlockFaceRegion region = segment.getRegion();
            if (interaction != null && bounds.intersects(region)) {
                entries.add(Entry.of(region, bounds, interaction.getType(), rotation, x, y));
            }
        }

        return new BakedWrenchOverlay(entries);
    }

    public void render(PoseStack.Pose pose, VertexConsumer consumer, int alpha, int light) {
        for (Entry entry : this.entries) {
            entry.render(pose, consumer, alpha, light);
        }
    }

    private static class Entry {
        private final BlockFaceRegion scaled;
        private final WrenchActionType type;
        private final boolean selected;

        private Entry(BlockFaceRegion scaled, WrenchActionType type, boolean selected) {
            this.scaled = scaled;
            this.type = type;
            this.selected = selected;
        }

        private static Entry of(BlockFaceRegion untrimmed, BlockFaceRegion bounds, WrenchActionType type, BlockFaceRegion.Rotation rotation, float x, float y) {
            BlockFaceRegion region = BlockFaceRegion.of(
                    Math.max(bounds.minX(), untrimmed.minX(rotation)),
                    Math.max(bounds.minY(), untrimmed.minY(rotation)),
                    Math.min(bounds.maxX(), untrimmed.maxX(rotation)),
                    Math.min(bounds.maxY(), untrimmed.maxY(rotation))
            );
            return new Entry(
                    region,
                    type,
                    region.contains(x, y)
            );
        }

        private void render(PoseStack.Pose pose, VertexConsumer consumer, int alpha, int light) {
            final float texCornerSliceLength = 2f/16;

            alpha = (int) (alpha * (this.selected ? 1 : 0.5f));
            int color = this.type.color;
            color &= 0x00FFFFFF; // clear alpha
            alpha <<= 24; // prep alpha
            color |= alpha; // apply alpha

            final float cornerSliceWidth = Math.min(texCornerSliceLength, scaled.width() / 2);
            final float cornerSliceHeight = Math.min(texCornerSliceLength, scaled.height() / 2);
            final float sideSliceWidth = Math.clamp(scaled.width() - (cornerSliceWidth * 2), 0, 1 - (texCornerSliceLength * 2));
            final float sideSliceHeight = Math.clamp(scaled.height() - (cornerSliceHeight * 2), 0, 1 - (texCornerSliceLength * 2));

            // top left - GOOD
            quad(pose, consumer, scaled.minX(), scaled.maxY() - cornerSliceHeight, scaled.minX() + cornerSliceWidth, scaled.maxY(), 0, 0, cornerSliceWidth, cornerSliceHeight, color, light);
            // top right - GOOD
            quad(pose, consumer, scaled.maxX() - cornerSliceWidth, scaled.maxY() - cornerSliceHeight, scaled.maxX(), scaled.maxY(), 1f - cornerSliceWidth, 0, 1f, cornerSliceHeight, color, light);
            // bottom left - GOOD
            quad(pose, consumer, scaled.minX(), scaled.minY(), scaled.minX() + cornerSliceWidth, scaled.minY() + cornerSliceHeight, 0, 1f - cornerSliceHeight, cornerSliceWidth, 1f, color, light);
            // bottom right - GOOD
            quad(pose, consumer, scaled.maxX() - cornerSliceWidth, scaled.minY(), scaled.maxX(), scaled.minY() + cornerSliceHeight, 1f - cornerSliceWidth, 1f - cornerSliceHeight, 1f, 1f, color, light);

            if (sideSliceHeight > 0) {
                // left side - GOOD
                quad(pose, consumer, scaled.minX(), scaled.minY() + cornerSliceHeight, scaled.minX() + cornerSliceWidth, scaled.maxY() - cornerSliceHeight, 0, cornerSliceHeight, cornerSliceWidth, cornerSliceHeight + sideSliceHeight, color, light);
                // right side - GOOD
                quad(pose, consumer, scaled.maxX() - cornerSliceWidth, scaled.minY() + cornerSliceHeight, scaled.maxX(), scaled.maxY() - cornerSliceHeight, 1f - cornerSliceWidth, cornerSliceHeight, 1, cornerSliceHeight + sideSliceHeight, color, light);
            }
            if (sideSliceWidth > 0) {
                // bottom side
                quad(pose, consumer, scaled.minX() + cornerSliceWidth, scaled.minY(), scaled.maxX() - cornerSliceWidth, scaled.minY() + cornerSliceHeight, cornerSliceWidth, 1 - cornerSliceHeight, cornerSliceWidth + sideSliceWidth, 1, color, light);
                // top side
                quad(pose, consumer, scaled.minX() + cornerSliceWidth, scaled.maxY() - cornerSliceHeight, scaled.maxX()  - cornerSliceWidth, scaled.maxY(), cornerSliceWidth, 0, cornerSliceWidth + sideSliceWidth, cornerSliceHeight, color, light);
            }
            if (sideSliceWidth > 0 && sideSliceHeight > 0) {
                quad(pose, consumer, scaled.minX() + cornerSliceWidth, scaled.minY() + cornerSliceHeight, scaled.maxX() - cornerSliceWidth, scaled.maxY() - cornerSliceWidth, texCornerSliceLength, texCornerSliceLength, texCornerSliceLength + sideSliceWidth, texCornerSliceLength + sideSliceHeight, color, light);
            }
        }

        private void quad(PoseStack.Pose poseStack, VertexConsumer vertexConsumer, float xMin, float yMin, float xMax, float yMax, float uMin, float vMin, float uMax, float vMax, int color, int light) {
            vertex(poseStack, vertexConsumer, xMin - 0.5f, yMax - 0.5f, 0, uMin, vMin, 0, 0, 0, color, light);
            vertex(poseStack, vertexConsumer, xMax - 0.5f, yMax - 0.5f, 0, uMax, vMin, 0, 0, 0, color, light);
            vertex(poseStack, vertexConsumer, xMax - 0.5f, yMin - 0.5f, 0, uMax, vMax, 0, 0, 0, color, light);
            vertex(poseStack, vertexConsumer, xMin - 0.5f, yMin - 0.5f, 0, uMin, vMax, 0, 0, 0, color, light);
        }

        private void vertex(
                PoseStack.Pose poseStack, VertexConsumer vertexConsumer, float x, float y, float z, float u, float v, float normalX, float normalZ, float normalY, int color, int light
        ) {
            vertexConsumer.addVertex(poseStack, x, y, z)
                    .setColor(color)
                    .setUv(u, v)
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(light)
                    .setNormal(poseStack, normalX, normalY, normalZ);
        }
    }
}
