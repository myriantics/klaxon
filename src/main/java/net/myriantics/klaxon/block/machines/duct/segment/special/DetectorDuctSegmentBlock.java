package net.myriantics.klaxon.block.machines.duct.segment.special;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.myriantics.klaxon.block.machines.duct.segment.AxisDuctSegmentBlock;
import net.myriantics.klaxon.block.machines.duct.segment.DuctSegmentBlockEntity;
import net.myriantics.klaxon.mechanics.logistics.itemduct.DuctPayload;
import net.myriantics.klaxon.mechanics.wire_redirector.KlaxonRedstoneWireRedirector;
import org.jetbrains.annotations.Nullable;

public class DetectorDuctSegmentBlock extends AxisDuctSegmentBlock implements KlaxonRedstoneWireRedirector {

    public static final Property<Direction.Axis> AXIS = AxisDuctSegmentBlock.AXIS;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public DetectorDuctSegmentBlock(Properties properties) {
        super(properties);

        registerDefaultState(this.defaultBlockState()
                .setValue(POWERED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWERED);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof DuctSegmentBlockEntity segmentBlockEntity ? segmentBlockEntity.computeAnalogFullnessStrength() : 0;
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        if (state.getValue(POWERED) && state.getValue(AXIS) != direction.getAxis()) {
            return 15;
        } else {
            return 0;
        }
    }

    @Override
    public boolean shouldRedirect(BlockState state, Direction wireOutboundConnectionDirection) {
        return state.getValue(AXIS) != wireOutboundConnectionDirection.getAxis();
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DetectorDuctSegmentBlockEntity(pos, state);
    }
}
