package net.myriantics.klaxon.util;

import net.myriantics.klaxon.mechanics.wrench.WrenchActionContext;

import java.util.List;

public final class BlockFaceRegion {

    public static final BlockFaceRegion FULL_BLOCK = of(0, 0, 1, 1);

    public final float minX;
    public final float minY;
    public final float maxX;
    public final float maxY;

    private BlockFaceRegion(float minX, float minY, float maxX, float maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public static BlockFaceRegion of(float minX, float minY, float maxX, float maxY) {
        return new BlockFaceRegion(minX, minY, maxX, maxY);
    }

    public static Builder builder() {
        return new Builder();
    }

    public float width() {
        return this.maxX - this.minX;
    }

    public float height() {
        return this.maxY - this.minY;
    }

    public boolean contains(float x, float y) {
        return x >= this.minX && x <= this.maxX && y >= this.minY && y <= this.maxY;
    }

    public boolean intersects(float minX, float minY, float maxX, float maxY) {
        return contains(minX, minY) || contains(minX, maxY) || contains(maxX, minY) || contains(maxX, maxY);
    }

    public boolean intersects(BlockFaceRegion region) {
        return intersects(region.minX, region.minY, region.maxX, region.maxY);
    }

    public static class Builder {
        private float minX;
        private float minY;
        private float maxX;
        private float maxY;

        private boolean init = false;

        private Builder() {
        }

        private boolean canMergeWith(float otherMinX, float otherMinY, float otherMaxX, float otherMaxY) {
            return otherMinX <= this.minX || otherMaxX >= this.maxX || otherMinY <= this.minY || otherMaxY >= this.maxY;
        }

        public Builder tryMerge(BlockFaceRegion region) {
            this.tryMerge(region.minX, region.minY, region.maxX, region.maxY);
            return this;
        }

        public boolean tryMerge(float otherMinX, float otherMinY, float otherMaxX, float otherMaxY) {
            if (canMergeWith(otherMinX, otherMinY, otherMaxX, otherMaxY)) {
                this.merge(otherMinX, otherMinY, otherMaxX, otherMaxY);
                return true;
            } else {
                return false;
            }
        }

        public Builder mergeOrThrow(float otherMinX, float otherMinY, float otherMaxX, float otherMaxY) {
            if (!canMergeWith(otherMinX, otherMinY, otherMaxX, otherMaxY)) {
                throw new AssertionError("");
            } else {
                this.merge(otherMinX, otherMinY, otherMaxX, otherMaxY);
                return this;
            }
        }

        private void merge(float otherMinX, float otherMinY, float otherMaxX, float otherMaxY) {
            if (!this.init) {
                this.minX = otherMinX;
                this.minY = otherMinY;
                this.maxX = otherMaxX;
                this.maxY = otherMaxY;
                this.init = true;
            } else {
                this.minX = Math.min(this.minX, otherMinX);
                this.minY = Math.min(this.minY, otherMinY);
                this.maxX = Math.max(this.maxX, otherMaxX);
                this.maxY = Math.max(this.maxY, otherMaxY);
            }
        }

        public BlockFaceRegion build() {
            return new BlockFaceRegion(this.minX, this.minY, this.maxX, this.maxY);
        }
    }
}
