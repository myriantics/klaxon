package net.myriantics.klaxon.mechanics.wrench.behaviors;

import net.minecraft.block.enums.RailShape;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.myriantics.klaxon.mechanics.wrench.BlockStateWrenchBehavior;
import net.myriantics.klaxon.mechanics.wrench.DispenserWrenchInteractionContext;
import net.myriantics.klaxon.mechanics.wrench.ManualWrenchInteractionContext;
import net.myriantics.klaxon.mechanics.wrench.KlaxonRailHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class StraightRailShapeBlockStateWrenchBehavior extends BlockStateWrenchBehavior<RailShape> {
    public StraightRailShapeBlockStateWrenchBehavior(Identifier id) {
        super(Properties.STRAIGHT_RAIL_SHAPE, id);
    }

    @Override
    protected Optional<RailShape> applyManual(RailShape original, ManualWrenchInteractionContext context) {
        Direction playerFacing = context.player().getFacing();
        @Nullable Direction.Axis railAxis = KlaxonRailHelper.railShapeToAxis(original);
        BlockPos targetPos = context.hitResult().getBlockPos();

        // rail can't be curved here, but we do this to filter any out as good practice
        if (railAxis == null) {
            throw new IllegalStateException("Unable to determine rail axis when running wrench behavior for " + context.targetState() + " at" + targetPos);
        }

        if (playerFacing.getAxis().equals(railAxis)) {
            // if we're coming from the same direction that the rail's already facing, try to switch between ascending and flat rails
            Direction.AxisDirection ascensionDirection = playerFacing.getDirection();
            RailShape toggled = KlaxonRailHelper.tryToggleAscending(context.world(), original, targetPos, ascensionDirection);

            if (toggled != null) {
                return Optional.of(toggled);
            }
        } else {
            // if the rail is being rotated from a horizontal axis, rotate rail to be on that axis
            RailShape rotated = KlaxonRailHelper.axisToRailShape(playerFacing.getAxis());
            if (rotated != null) {
                return Optional.of(rotated);
            }
        }

        return Optional.empty();
    }

    @Override
    protected Optional<RailShape> applyDispenser(RailShape original, DispenserWrenchInteractionContext context) {
        @Nullable Direction.Axis railAxis = KlaxonRailHelper.railShapeToAxis(original);

        // rail can't be curved here, but we do this to filter any out as good practice
        if (railAxis == null) {
            throw new IllegalStateException("Unable to determine rail axis when running wrench behavior for " + context.targetState() + " at" + context.targetPos());
        }

        if (context.dispenserFacing().getAxis().equals(railAxis)) {
            // if we're coming from the same direction that the rail's already facing, try to switch between ascending and flat rails
            Direction.AxisDirection ascensionDirection = context.dispenserFacing().getDirection().getOpposite();
            // swap since we're a dispenser to allow tracks to be routed on top of dispenser - more useful than setting the dispenser at the bottom of the track, blocking any carts.
            RailShape toggled = KlaxonRailHelper.tryToggleAscending(context.serverWorld(), original, context.targetPos(), ascensionDirection);

            if (toggled != null) {
                return Optional.of(toggled);
            }
        } else {
            // if the rail is being rotated from a horizontal axis, rotate rail to be on that axis
            RailShape rotated = KlaxonRailHelper.axisToRailShape(context.dispenserFacing().getAxis());
            // if a dispenser is wrenching from top or bottom, flip the rail's orientation
            if (rotated == null) {
                Direction.Axis axis = KlaxonRailHelper.flipHorizontalAxis(railAxis);
                if(axis != null) rotated = KlaxonRailHelper.axisToRailShape(axis);
            }

            if (rotated != null) {
                return Optional.of(rotated);
            }
        }

        return Optional.empty();
    }
}
