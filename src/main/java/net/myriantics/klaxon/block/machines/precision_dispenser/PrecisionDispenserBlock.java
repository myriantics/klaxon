package net.myriantics.klaxon.block.machines.precision_dispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class PrecisionDispenserBlock extends DispenserBlock {

    public static final DirectionProperty FACING = DispenserBlock.FACING;
    public static final BooleanProperty TRIGGERED = DispenserBlock.TRIGGERED;

    public PrecisionDispenserBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PrecisionDispenserBlockEntity(pos, state);
    }
}
