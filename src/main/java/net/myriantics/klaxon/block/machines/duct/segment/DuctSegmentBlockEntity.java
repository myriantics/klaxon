package net.myriantics.klaxon.block.machines.duct.segment;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
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
        if (remainingDepth == 0) {
            return false;
        } else if (this.getOrInitHandler().push(payload, inputFace, remainingDepth - 1)) {
            this.setPayload(payload);
            return true;
        }

        return false;
    }
}
