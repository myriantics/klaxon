package net.myriantics.klaxon.mechanics.wrench.behaviors;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.myriantics.klaxon.mechanics.wrench.BlockStateWrenchBehavior;
import net.myriantics.klaxon.mechanics.wrench.DispenserWrenchInteractionContext;
import net.myriantics.klaxon.mechanics.wrench.ManualWrenchInteractionContext;

import java.util.Optional;

public class HorizontalFacingBlockStateWrenchBehavior extends BlockStateWrenchBehavior<Direction> {

    public HorizontalFacingBlockStateWrenchBehavior(ResourceLocation id) {
        super(BlockStateProperties.HORIZONTAL_FACING, id);
    }

    @Override
    protected Optional<Direction> applyManual(Direction original, ManualWrenchInteractionContext context) {
        Direction.Axis axis = original.getAxis();
        Direction clickedDirection = context.hitResult().getDirection();
        Direction.Axis clickedAxis = clickedDirection.getAxis();

        if (clickedAxis.equals(Direction.Axis.Y)) {
            return Optional.of(original.getClockWise(Direction.Axis.Y));
        }

        if (clickedAxis.equals(axis)) {
            return Optional.of(original.getOpposite());
        }

        return Optional.of(clickedDirection.getOpposite());
    }

    @Override
    protected Optional<Direction> applyDispenser(Direction original, DispenserWrenchInteractionContext context) {
        Direction.Axis dispenserAxis = context.dispenserFacing().getAxis();

        if (dispenserAxis.equals(Direction.Axis.Y)) {
            return Optional.of(original.getClockWise(Direction.Axis.Y));
        }

        return Optional.of(original.getOpposite());
    }
}
