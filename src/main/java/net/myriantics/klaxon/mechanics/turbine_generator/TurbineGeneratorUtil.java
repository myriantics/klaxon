package net.myriantics.klaxon.mechanics.turbine_generator;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.myriantics.klaxon.block.machines.energy.generators.turbine.TurbineGeneratorBlockEntity;
import org.jetbrains.annotations.Nullable;

public abstract class TurbineGeneratorUtil {
    public static boolean stateConductsAirflow(Level level, BlockPos pos, BlockState state, Direction airflowDirection) {
        return state.isAir();
    }

    public static @Nullable TurbineGeneratorBlockEntity findTurbineGenerator(ServerLevel serverLevel, BlockPos pos, Direction checkingDirection, int range) {
        for (int i = 0; i < range; i++) {
            BlockPos selectedPos = pos.relative(checkingDirection, i + 1);
            BlockState selectedState = serverLevel.getBlockState(selectedPos);
            if (stateConductsAirflow(serverLevel, selectedPos, selectedState, checkingDirection)) {
                continue;
            }
            // if we hit a generator and it's facing the opposite
            if (serverLevel.getBlockEntity(selectedPos) instanceof TurbineGeneratorBlockEntity generator && generator.getFacing().equals(checkingDirection.getOpposite())) {
                return generator;
            } else {
                break;
            }
        }
        return null;
    }
}
