package net.myriantics.klaxon.mechanics.wrench.behaviors;

import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.myriantics.klaxon.mechanics.wrench.BlockStateWrenchBehavior;
import net.myriantics.klaxon.mechanics.wrench.DispenserWrenchInteractionContext;
import net.myriantics.klaxon.mechanics.wrench.ManualWrenchInteractionContext;

import java.util.Objects;
import java.util.Optional;

public class HopperFacingBlockStateWrenchBehavior extends BlockStateWrenchBehavior<Direction> {
    public HopperFacingBlockStateWrenchBehavior(Identifier id) {
        super(Properties.HOPPER_FACING, id);
    }

    @Override
    protected Optional<Direction> applyManual(Direction original, ManualWrenchInteractionContext context) {
        Direction hitSide = context.hitResult().getSide();

        if (hitSide.equals(Direction.UP)) {
            if (original.equals(Direction.DOWN)) {
                return Optional.empty();
            } else {
                return Optional.ofNullable(original.rotateClockwise(Direction.Axis.Y));
            }
        }

        if (hitSide.equals(original)) {
            return Optional.of(Direction.DOWN);
        } else {
            return Optional.of(hitSide);
        }
    }

    @Override
    protected Optional<Direction> applyDispenser(Direction original, DispenserWrenchInteractionContext context) {
        if (Objects.requireNonNull(context.dispenserFacing()) == Direction.DOWN) {
            if (Objects.requireNonNull(original) == Direction.DOWN) {
                return Optional.of(Direction.NORTH);
            }
            return Optional.ofNullable(original.rotateClockwise(Direction.Axis.Y));
        }

        if (context.dispenserFacing().equals(original.getOpposite())) {
            return Optional.of(Direction.DOWN);
        } else {
            return Optional.ofNullable(context.dispenserFacing().getOpposite());
        }
    }
}
