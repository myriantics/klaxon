package net.myriantics.klaxon.block.machines.geothermal.pipe_matrix;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class OxidizablePipeMatrixUBendBlock extends PipeMatrixUBendBlock implements WeatheringCopper {
    private final WeatherState oxidationLevel;

    public OxidizablePipeMatrixUBendBlock(WeatherState oxidationLevel, Properties settings) {
        super(settings);
        this.oxidationLevel = oxidationLevel;
    }

    @Override
    public WeatherState getAge() {
        return this.oxidationLevel;
    }

    @Override
    public Optional<BlockState> getNextState(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        Optional<BlockState> degraded = WeatheringCopper.super.getNextState(state, world, pos, random);

        // pipe matrices actively hooked up to a geothermal generator have water in them, so they oxidize twice as fast
        if (degraded.isEmpty() && state.getValue(FORMED)) {
            degraded = WeatheringCopper.super.getNextState(state, world, pos, random);
        }

        return degraded;
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        this.changeOverTime(state, world, pos, random);
        super.randomTick(state, world, pos, random);
    }
}
