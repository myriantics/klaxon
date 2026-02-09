package net.myriantics.klaxon.block.machines.geothermal.pipe_matrix;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Degradable;
import net.minecraft.block.Oxidizable;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.myriantics.klaxon.block.decor.OxidizablePillarBlock;

import java.util.Optional;

public class OxidizablePipeMatrixSegmentBlock extends PipeMatrixSegmentBlock implements Oxidizable {
    private final OxidationLevel oxidationLevel;

    public OxidizablePipeMatrixSegmentBlock(OxidationLevel oxidationLevel, Settings settings, Block loopBlock) {
        super(settings, loopBlock);
        this.oxidationLevel = oxidationLevel;
    }

    @Override
    public OxidationLevel getDegradationLevel() {
        return this.oxidationLevel;
    }

    @Override
    public Optional<BlockState> tryDegrade(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        Optional<BlockState> degraded = Oxidizable.super.tryDegrade(state, world, pos, random);

        // pipe matrices actively hooked up to a geothermal generator have water in them, so they oxidize twice as fast
        if (degraded.isEmpty() && state.get(FORMED)) {
            degraded = Oxidizable.super.tryDegrade(state, world, pos, random);
        }

        return degraded;
    }

    @Override
    protected boolean hasRandomTicks(BlockState state) {
        return true;
    }

    @Override
    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        this.tickDegradation(state, world, pos, random);
        super.randomTick(state, world, pos, random);
    }
}
