package net.myriantics.klaxon.block.customblocks.decor;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Degradable;
import net.minecraft.block.Oxidizable;
import net.minecraft.block.PillarBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class OxidizablePillarBlock extends PillarBlock implements Oxidizable {
    public static final MapCodec<OxidizablePillarBlock> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(OxidationLevel.CODEC.fieldOf("weathering_state").forGetter(Degradable::getDegradationLevel), createSettingsCodec())
                    .apply(instance, OxidizablePillarBlock::new)
    );
    private final Oxidizable.OxidationLevel oxidationLevel;

    public OxidizablePillarBlock(Oxidizable.OxidationLevel oxidationLevel, Settings settings) {
        super(settings);
        this.oxidationLevel = oxidationLevel;
    }

    @Override
    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        this.tickDegradation(state, world, pos, random);
    }

    @Override
    protected boolean hasRandomTicks(BlockState state) {
        return Oxidizable.getIncreasedOxidationBlock(state.getBlock()).isPresent();
    }

    @Override
    public OxidationLevel getDegradationLevel() {
        return this.oxidationLevel;
    }

    @Override
    public MapCodec<? extends PillarBlock> getCodec() {
        return CODEC;
    }
}
