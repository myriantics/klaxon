package net.myriantics.klaxon.mechanics.wrench.behaviors;

import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.myriantics.klaxon.mechanics.wrench.BlockStateWrenchBehavior;
import net.myriantics.klaxon.mechanics.wrench.DispenserWrenchInteractionContext;
import net.myriantics.klaxon.mechanics.wrench.ManualWrenchInteractionContext;

import java.util.Optional;

public class OrientationBlockStateWrenchBehavior extends BlockStateWrenchBehavior<FrontAndTop> {
    public OrientationBlockStateWrenchBehavior(ResourceLocation id) {
        super(BlockStateProperties.ORIENTATION, id);
    }

    @Override
    protected Optional<FrontAndTop> applyManual(FrontAndTop original, ManualWrenchInteractionContext context) {
        Direction facing = original.front();
        Direction rotation = original.top();
        Direction.Axis clickedAxis = context.hitResult().getDirection().getAxis();

        if (facing.getAxis().equals(clickedAxis)) {
            return Optional.ofNullable(FrontAndTop.fromFrontAndTop(facing, rotation.getClockWise(clickedAxis)));
        } else {
            return Optional.ofNullable(FrontAndTop.fromFrontAndTop(facing.getClockWise(clickedAxis), rotation));
        }
    }

    @Override
    protected Optional<FrontAndTop> applyDispenser(FrontAndTop original, DispenserWrenchInteractionContext context) {
        Direction facing = original.front();
        Direction rotation = original.top();
        Direction.Axis dispenserAxis = context.dispenserFacing().getAxis();

        if (facing.getAxis().equals(dispenserAxis)) {
            return Optional.of(FrontAndTop.fromFrontAndTop(facing, rotation.getClockWise(dispenserAxis)));
        } else {
            return Optional.of(FrontAndTop.fromFrontAndTop(facing.getClockWise(dispenserAxis), rotation));
        }
    }
}
