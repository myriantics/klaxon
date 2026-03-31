package net.myriantics.klaxon.util;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.mechanics.wrench.WrenchActionContext;

public final class BlockFaceRegion {

    public static final BlockFaceRegion FULL_BLOCK = of(0, 0, 1, 1);

    private final float minX;
    private final float minY;
    private final float maxX;
    private final float maxY;

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

    public float minX(Rotation rotation) {
        return switch (rotation) {
            case R0 -> this.minX;
            case R90 -> 1 - this.maxY;
            case R180 -> 1 - this.maxX;
            case R270 -> this.minY;
        };
    }

    public float minX() {
        return this.minX;
    }

    public float minY(Rotation rotation) {
        return switch (rotation) {
            case R0 -> this.minY;
            case R90 -> 1 - this.maxX;
            case R180 -> 1 - this.maxY;
            case R270 -> this.minX;
        };
    }

    public float minY() {
        return this.minY;
    }

    public float maxX(Rotation rotation) {
        return switch (rotation) {
            case R0 -> this.maxX;
            case R90 -> 1 - this.minY;
            case R180 -> 1 - this.minX;
            case R270 -> this.maxY;
        };
    }

    public float maxX() {
        return this.maxX;
    }

    public float maxY(Rotation rotation) {
        return switch (rotation) {
            case R0 -> this.maxY;
            case R90 -> 1 - this.minX;
            case R180 -> 1 - this.minY;
            case R270 -> this.maxX;
        };
    }

    public float maxY() {
        return this.maxY;
    }

    public float width(Rotation rotation) {
        return this.maxX(rotation) - this.minX(rotation);
    }

    public float width() {
        return this.maxX - this.minX;
    }

    public float height(Rotation rotation) {
        return this.maxY(rotation) - this.minY(rotation);
    }

    public float height() {
        return this.maxY - this.minY;
    }

    public boolean contains(float x, float y) {
        return x >= this.minX && x <= this.maxX && y >= this.minY && y <= this.maxY;
    }

    public boolean intersects(float minX, float minY, float maxX, float maxY) {
        return (minX >= this.minX && minX <= this.maxX) || (minY >= this.minY && minY <= this.maxY) || (maxX <= this.maxX && maxX >= this.minX) || (maxY <= this.maxX && maxY >= this.minY);
    }

    public boolean intersects(BlockFaceRegion region) {
        return intersects(region.minX, region.minY, region.maxX, region.maxY);
    }

    public enum Rotation {
        R0,
        R90,
        R180,
        R270;
    }

    public interface State2Rotation {
        Rotation getRotation(BlockState state, WrenchActionContext.GuiOrientation orientation);

        static Rotation topOnly(BlockState state, WrenchActionContext.GuiOrientation orientation) {
            return switch (orientation.getGuiUpDir()) {
                case EAST -> orientation.getFacing().getAxisDirection().equals(Direction.AxisDirection.POSITIVE) ? Rotation.R90 : Rotation.R270;
                case SOUTH -> Rotation.R180;
                case WEST -> orientation.getFacing().getAxisDirection().equals(Direction.AxisDirection.POSITIVE) ? Rotation.R270 : Rotation.R90;
                default -> Rotation.R0;
            };
        }
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
