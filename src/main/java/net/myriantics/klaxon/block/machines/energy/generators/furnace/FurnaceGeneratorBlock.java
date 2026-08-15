package net.myriantics.klaxon.block.machines.energy.generators.furnace;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class FurnaceGeneratorBlock extends AbstractFurnaceBlock {

    public static final DirectionProperty FACING = AbstractFurnaceBlock.FACING;
    public static final BooleanProperty LIT = AbstractFurnaceBlock.LIT;

    public FurnaceGeneratorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends AbstractFurnaceBlock> codec() {
        return null;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        Containers.dropContentsOnDestroy(state, newState, level, pos);
    }

    @Override
    protected void openContainer(Level level, BlockPos pos, Player player) {

    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FurnaceGeneratorBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide() ? null : (level1, blockPos, blockState, blockEntity) -> {
            if (blockEntity instanceof FurnaceGeneratorBlockEntity furnaceGeneratorBlockEntity) {
                furnaceGeneratorBlockEntity.serverTick((ServerLevel) level1, blockPos, blockState);
            }
        };
    }
}
