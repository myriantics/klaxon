package net.myriantics.klaxon.block.customblocks;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.myriantics.klaxon.block.blockentities.CrudeExtrapolatorBlockEntity;
import org.jetbrains.annotations.Nullable;

public class CrudeExtrapolatorBlock extends BlockWithEntity {

    enum FuelState implements StringIdentifiable {
        HYPER, SUPER, REGULAR, EMPTY;

        @Override
        public String asString() {
            return this.toString().toLowerCase();
        }
    }

    static final BooleanProperty POWERED = BooleanProperty.of("powered");
    static final EnumProperty<FuelState> FUEL_STATE = EnumProperty.of("fuel_state", FuelState.class);
    static final DirectionProperty FACING = FacingBlock.FACING;

    public CrudeExtrapolatorBlock(Settings settings) {
        super(settings);

        setDefaultState(getStateManager().getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(POWERED, false)
                .with(FUEL_STATE, FuelState.EMPTY));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CrudeExtrapolatorBlockEntity(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWERED, FUEL_STATE, FACING);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return this.getDefaultState().with(FACING, context.getPlayerLookDirection().getOpposite());
    }
}
