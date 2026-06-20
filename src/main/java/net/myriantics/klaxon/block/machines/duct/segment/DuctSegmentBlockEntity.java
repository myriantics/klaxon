package net.myriantics.klaxon.block.machines.duct.segment;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.machines.duct.BaseDuctComponentBlockEntity;
import net.myriantics.klaxon.mechanics.logistics.itemduct.DuctInteractionHandler;
import net.myriantics.klaxon.mechanics.logistics.itemduct.DuctNode;
import net.myriantics.klaxon.mechanics.logistics.itemduct.DuctPayload;
import net.myriantics.klaxon.registry.block.KlaxonBlockEntityTypes;
import org.jetbrains.annotations.Nullable;

public class DuctSegmentBlockEntity extends BaseDuctComponentBlockEntity {

    private DuctInteractionHandler handler;

    protected DuctSegmentBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public DuctSegmentBlockEntity(BlockPos pos, BlockState state) {
        super(KlaxonBlockEntityTypes.DUCT_SEGMENT.value(), pos, state);
    }

    public void updateDirections(Direction changed, boolean faceStatus) {
        this.getOrInitHandler().updateDirection(this.level, this.worldPosition, changed, faceStatus);
    }

    protected DuctInteractionHandler getOrInitHandler() {
        return this.handler == null ? this.handler = DuctInteractionHandler.initialize(this.level, this.worldPosition, this.getBlockState(), this) : this.handler;
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
    }

    @Override
    public boolean push(DuctPayload payload, Direction inputFace, int remainingDepth) {
        // if we can fully push our contents into output and there's recursion left to do, return true
        if (remainingDepth != 0 && this.getOrInitHandler().push(this.getPayload(), inputFace.getOpposite(), remainingDepth - 1)) {
            return true;
        }
        if (payload != null && remainingDepth <= 0) {
            KlaxonCommon.LOGGER.info("has payload with dept " + remainingDepth);
        }
        // if we can't move forwards, check if we're fully empty and replace payload if so
        if (!this.hasNonEmptyPayload()) {
            return true;
        }
        // if we were unable to fully push our contents out, return false to signify that a new payload should not replace content
        return false;
    }

    @Override
    public String getStatusForDirection(Direction direction) {
        return this.getOrInitHandler().getStatusForDirection(direction);
    }
}
