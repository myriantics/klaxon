package net.myriantics.klaxon.block.machines.duct;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.myriantics.klaxon.mechanics.logistics.itemduct.DuctPayload;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class BaseDuctComponentBlockEntity extends BlockEntity {

    private static final float ITEM_DROPPING_HORIZ_OFFSET = 0.5f + (EntityType.ITEM.getWidth() / 2);
    private static final float ITEM_DROPPING_VERTICAL_OFFSET = 0.5f + (EntityType.ITEM.getHeight() / 2);
    private static final float DEFAULT_EJECTED_ITEM_OUTPUT_VELOCITY = 3f/20;

    private @Nullable DuctPayload payload;

    public BaseDuctComponentBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    protected void clearPayload() {
        this.payload = null;
    }

    protected void setPayload(@Nullable DuctPayload payload) {
        this.payload = payload;
    }

    protected boolean hasPayload() {
        return this.payload != null;
    }

    protected @Nullable DuctPayload getPayload() {
        return this.payload;
    }

    protected float getEjectedItemOutputVelocity() {
        return DEFAULT_EJECTED_ITEM_OUTPUT_VELOCITY;
    }

    protected void dumpPayloadOnSide(Direction direction) {
        Vec3 centerPos = this.worldPosition.getCenter();
        float ejectedItemOutputVelocity = this.getEjectedItemOutputVelocity();
        if (this.level instanceof ServerLevel serverLevel && this.payload != null) {
            for (ItemStack stack : this.payload.stacks) {
                if (!stack.isEmpty()) {
                    ItemEntity droppedItem = new ItemEntity(
                            serverLevel,
                            centerPos.x + (direction.getStepX() * ITEM_DROPPING_HORIZ_OFFSET),
                            centerPos.y + (direction.getStepY() * ITEM_DROPPING_VERTICAL_OFFSET),
                            centerPos.z + (direction.getStepZ() * ITEM_DROPPING_HORIZ_OFFSET),
                            stack,
                            direction.getStepX() * ejectedItemOutputVelocity,
                            direction.getStepY() * ejectedItemOutputVelocity,
                            direction.getStepZ() * ejectedItemOutputVelocity
                    );
                    serverLevel.addFreshEntity(droppedItem);
                }
            }
            this.clearPayload();
        }
    }
}
