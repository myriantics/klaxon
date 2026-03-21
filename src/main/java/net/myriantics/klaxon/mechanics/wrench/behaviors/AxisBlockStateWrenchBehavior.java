package net.myriantics.klaxon.mechanics.wrench.behaviors;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.myriantics.klaxon.mechanics.wrench.BlockStateWrenchBehavior;
import net.myriantics.klaxon.mechanics.wrench.DispenserWrenchInteractionContext;
import net.myriantics.klaxon.mechanics.wrench.ManualWrenchInteractionContext;

import java.util.Optional;

public class AxisBlockStateWrenchBehavior extends BlockStateWrenchBehavior<Direction.Axis> {
    public AxisBlockStateWrenchBehavior(ResourceLocation id) {
        super(BlockStateProperties.AXIS, id);
    }

    @Override
    protected Optional<Direction.Axis> applyManual(Direction.Axis original, ManualWrenchInteractionContext context) {
        Direction.Axis dispenserAxis = context.hitResult().getDirection().getAxis();

        if (dispenserAxis.equals(original)) {
            return Optional.empty();
        }

        for (Direction.Axis axis : Direction.Axis.VALUES) {
            if (!axis.equals(dispenserAxis) && !axis.equals(original)) {
                return Optional.of(axis);
            }
        }

        return Optional.empty();
    }

    @Override
    protected Optional<Direction.Axis> applyDispenser(Direction.Axis original, DispenserWrenchInteractionContext context) {
        Direction.Axis dispenserAxis = context.dispenserFacing().getAxis();

        if (dispenserAxis.equals(original)) {
            return Optional.empty();
        }

        for (Direction.Axis axis : Direction.Axis.VALUES) {
            if (!axis.equals(dispenserAxis) && !axis.equals(original)) {
                return Optional.of(axis);
            }
        }

        return Optional.empty();
    }
}
