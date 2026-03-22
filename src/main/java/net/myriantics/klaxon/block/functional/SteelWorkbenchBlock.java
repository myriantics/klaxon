package net.myriantics.klaxon.block.functional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CraftingTableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.myriantics.klaxon.recipe.world_item_application.WorldItemApplicationResult;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class SteelWorkbenchBlock extends CraftingTableBlock implements WorldItemApplicationResult {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public SteelWorkbenchBlock(Properties properties) {
        super(properties);

        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.UP));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        @Nullable BlockState superState = super.getStateForPlacement(context);
        return superState == null ? this.defaultBlockState() : superState.setValue(FACING, context.getClickedFace());
    }

    @Override
    public Optional<BlockState> getResultState(Level world, BlockState state, BlockPos pos, Direction clickDirection, @Nullable Player lookDirection) {
        return Optional.of(state.setValue(FACING, clickDirection));
    }
}
