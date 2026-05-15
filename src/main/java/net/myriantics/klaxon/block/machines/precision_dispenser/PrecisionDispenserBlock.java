package net.myriantics.klaxon.block.machines.precision_dispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.myriantics.klaxon.mechanics.muffling.MufflableBlock;
import net.myriantics.klaxon.registry.block.KlaxonBlockStateProperties;

public class PrecisionDispenserBlock extends DispenserBlock implements MufflableBlock {

    public static final DirectionProperty FACING = DispenserBlock.FACING;
    public static final BooleanProperty TRIGGERED = DispenserBlock.TRIGGERED;
    public static final BooleanProperty MUFFLED = KlaxonBlockStateProperties.MUFFLED;

    public PrecisionDispenserBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(MUFFLED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(MUFFLED);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PrecisionDispenserBlockEntity(pos, state);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && !level.isClientSide()) {
            if (level.getBlockEntity(pos) instanceof PrecisionDispenserBlockEntity precisionDispenser) {
                Containers.dropItemStack(
                        level,
                        pos.getX(),
                        pos.getY(),
                        pos.getZ(),
                        precisionDispenser.getMuffler()
                );
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public boolean hasMuffler(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.hasProperty(MUFFLED) && state.getValue(MUFFLED);
    }

    @Override
    public ItemStack getMuffler(Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof PrecisionDispenserBlockEntity precisionDispenser) {
            return precisionDispenser.getMuffler();
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void setMuffler(Level level, BlockPos pos, ItemStack stack) {
        if (level.getBlockEntity(pos) instanceof PrecisionDispenserBlockEntity precisionDispenser) {
            precisionDispenser.setMuffler(stack);
        }
    }
}
