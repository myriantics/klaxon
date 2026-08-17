package net.myriantics.klaxon.mechanics.turbine_generator.power_source;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.block.machines.energy.generators.turbine.TurbineGeneratorBlockEntity;

public interface TurbineGeneratorPowerSource {
    double getTargetVelocity();

    default void turbinePowerTick(TurbineGeneratorBlockEntity blockEntity, ServerLevel serverLevel, BlockPos turbinePos, BlockState turbineState) {
    }

    default boolean stillValid() {
        return true;
    }
}
