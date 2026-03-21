package net.myriantics.klaxon.mechanics.wrench.behaviors;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.myriantics.klaxon.mechanics.wrench.BlockStateWrenchBehavior;
import net.myriantics.klaxon.mechanics.wrench.DispenserWrenchInteractionContext;
import net.myriantics.klaxon.mechanics.wrench.ManualWrenchInteractionContext;

import java.util.Optional;

public class FacingBlockStateWrenchBehavior extends BlockStateWrenchBehavior<Direction> {

    public FacingBlockStateWrenchBehavior(ResourceLocation id) {
        super(BlockStateProperties.FACING, id);
    }

    @Override
    protected Optional<Direction> applyManual(Direction original, ManualWrenchInteractionContext context) {
        Direction.Axis axis = context.hitResult().getDirection().getAxis();

        if (original.getAxis().equals(axis)) {
            return Optional.of(original.getOpposite());
        } else {
            return Optional.of(original.getClockWise(axis));
        }
    }

    @Override
    protected Optional<Direction> applyDispenser(Direction original, DispenserWrenchInteractionContext context) {
        Direction.Axis dispenserAxis = context.dispenserFacing().getAxis();

        if (original.getAxis().equals(dispenserAxis)) {
            return Optional.of(original.getOpposite());
        } else {
            return Optional.of(original.getClockWise(dispenserAxis));
        }
    }
}
