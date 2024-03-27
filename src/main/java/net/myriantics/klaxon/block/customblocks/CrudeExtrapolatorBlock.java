package net.myriantics.klaxon.block.customblocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.myriantics.klaxon.block.blockentities.CrudeExtrapolatorBlockEntity;
import org.jetbrains.annotations.Nullable;

public class CrudeExtrapolatorBlock extends BlockWithEntity {

    enum FuelState implements StringIdentifiable {
        RIGGED, FUELED, EMPTY;

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
}
