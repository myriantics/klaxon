package net.myriantics.klaxon.mechanics.wrench.behaviors;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.myriantics.klaxon.mechanics.wrench.BlockStateWrenchBehavior;
import net.myriantics.klaxon.mechanics.wrench.DispenserWrenchInteractionContext;
import net.myriantics.klaxon.mechanics.wrench.ManualWrenchInteractionContext;

import java.util.Optional;

public class HorizontalAxisBlockStateWrenchBehavior extends BlockStateWrenchBehavior<Direction.Axis> {
    public HorizontalAxisBlockStateWrenchBehavior(ResourceLocation id) {
        super(BlockStateProperties.HORIZONTAL_AXIS, id);
    }

    @Override
    protected Optional<Direction.Axis> applyManual(Direction.Axis original, ManualWrenchInteractionContext context) {
        return doThing(original, context.targetState(), context.hitResult().getBlockPos());
    }

    @Override
    protected Optional<Direction.Axis> applyDispenser(Direction.Axis original, DispenserWrenchInteractionContext context) {
        return doThing(original, context.targetState(), context.targetPos());
    }

    private static Optional<Direction.Axis> doThing(Direction.Axis original, BlockState state, BlockPos pos) {
        switch (original) {
            case X -> {
                return Optional.of(Direction.Axis.Z);
            }
            case Z -> {
                return Optional.of(Direction.Axis.X);
            }
            case Y -> {
                throw new IllegalStateException("Encountered Y axis when performing wrench operation on horizontal block state " + state + " at " + pos);
            }
        };

        return Optional.empty();
    }
}
