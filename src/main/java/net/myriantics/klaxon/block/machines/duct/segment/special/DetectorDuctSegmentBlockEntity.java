package net.myriantics.klaxon.block.machines.duct.segment.special;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.block.machines.duct.segment.DuctSegmentBlockEntity;
import net.myriantics.klaxon.mechanics.logistics.itemduct.DuctPayload;
import net.myriantics.klaxon.registry.block.KlaxonBlockEntityTypes;
import org.jetbrains.annotations.Nullable;

public class DetectorDuctSegmentBlockEntity extends DuctSegmentBlockEntity {
    protected DetectorDuctSegmentBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public DetectorDuctSegmentBlockEntity(BlockPos pos, BlockState state) {
        super(KlaxonBlockEntityTypes.DETECTOR_DUCT_SEGMENT.value(), pos, state);
    }

    @Override
    public void setPayload(@Nullable DuctPayload payload) {
        super.setPayload(payload);
        boolean isPayloadEmpty = payload == null || payload.isEmpty();
        // check if we need to update state
        if (this.level != null && this.getBlockState().getValue(DetectorDuctSegmentBlock.POWERED) == (isPayloadEmpty)) {
            this.level.setBlockAndUpdate(this.worldPosition, this.getBlockState().setValue(DetectorDuctSegmentBlock.POWERED, !isPayloadEmpty));
        }
    }
}
