package net.myriantics.klaxon.block.machines.energy.generators.furnace;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class FurnaceGeneratorBlock extends AbstractFurnaceBlock {
    protected FurnaceGeneratorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends AbstractFurnaceBlock> codec() {
        return null;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof AbstractFurnaceBlockEntity) {
                if (level instanceof ServerLevel) {
                    Containers.dropContents(level, pos, (AbstractFurnaceBlockEntity)blockEntity);
                    ((AbstractFurnaceBlockEntity)blockEntity).getRecipesToAwardAndPopExperience((ServerLevel)level, Vec3.atCenterOf(pos));
                }

                super.onRemove(state, level, pos, newState, movedByPiston);
                level.updateNeighbourForOutputSignal(pos, this);
            } else {
                super.onRemove(state, level, pos, newState, movedByPiston);
            }
        }
    }

    @Override
    protected void openContainer(Level level, BlockPos pos, Player player) {

    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }
}
