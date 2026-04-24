package net.myriantics.klaxon.block.machines.blast_processor.steel;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.myriantics.klaxon.block.machines.blast_processor.AbstractBlastProcessorBlock;
import net.myriantics.klaxon.mechanics.muffling.MufflableBlock;
import net.myriantics.klaxon.registry.block.KlaxonBlockEntityTypes;
import net.myriantics.klaxon.registry.block.KlaxonBlockStateProperties;
import org.jetbrains.annotations.Nullable;

public class SteelBlastProcessorBlock extends AbstractBlastProcessorBlock implements MufflableBlock {

    public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty MUFFLED = KlaxonBlockStateProperties.MUFFLED;

    public SteelBlastProcessorBlock(Properties properties) {
        super(properties);

        registerDefaultState(defaultBlockState()
                .setValue(HORIZONTAL_FACING, Direction.NORTH)
                .setValue(MUFFLED, false)
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(SteelBlastProcessorBlock::new);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SteelBlastProcessorBlockEntity(KlaxonBlockEntityTypes.STEEL_BLAST_PROCESSOR.value(), pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HORIZONTAL_FACING, MUFFLED);
    }

    public void handleExhaust(Level level, BlockPos pos) {

    }

    public void updateState(Level level, BlockPos pos, SteelBlastProcessorBlockEntity blastProcessor) {
        BlockState original = level.getBlockState(pos);
        BlockState newState = original.setValue(MUFFLED, !blastProcessor.getMuffler().isEmpty());

        if (!original.equals(newState)) {
            level.setBlockAndUpdate(pos, newState);
        }
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        @Nullable BlockState original = super.getStateForPlacement(context);
        if (original == null) {
            return this.defaultBlockState().setValue(HORIZONTAL_FACING, context.getHorizontalDirection());
        } else {
            return original.setValue(HORIZONTAL_FACING, context.getHorizontalDirection());
        }
    }

    @Override
    public boolean hasMuffler(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.getValue(MUFFLED);
    }

    @Override
    public ItemStack getMuffler(Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof SteelBlastProcessorBlockEntity blastProcessor) {
            return blastProcessor.getMuffler();
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void setMuffler(Level level, BlockPos pos, ItemStack stack) {
        if (level.getBlockEntity(pos) instanceof SteelBlastProcessorBlockEntity blastProcessor) {
            blastProcessor.setMuffler(stack);
        }
    }
}
