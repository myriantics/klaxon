package net.myriantics.klaxon.mechanics.wrench.behaviors;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.myriantics.klaxon.mechanics.wrench.BlockStateWrenchBehavior;
import net.myriantics.klaxon.mechanics.wrench.DispenserWrenchInteractionContext;
import net.myriantics.klaxon.mechanics.wrench.ManualWrenchInteractionContext;
import net.myriantics.klaxon.mechanics.wrench.WrenchActionContext;
import net.myriantics.klaxon.mechanics.wrench.interaction.WrenchInteraction;
import net.myriantics.klaxon.mechanics.wrench.interaction.WrenchInteractionMap;
import net.myriantics.klaxon.registry.behavior.KlaxonWrenchActionTypes;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class FacingBlockStateWrenchBehavior extends BlockStateWrenchBehavior<Direction> {


    private final WrenchInteraction ROTATE_COUNTERCLOCKWISE = WrenchInteraction.of(
            KlaxonWrenchActionTypes.ROTATE_COUNTERCLOCKWISE,
            context -> this.rotate(context, Direction::getCounterClockWise, manual -> manual.clickedDirection().getAxis())
    );
    private final WrenchInteraction ROTATE_CLOCKWISE = WrenchInteraction.of(
            KlaxonWrenchActionTypes.ROTATE_CLOCKWISE,
            context -> this.rotate(context, Direction::getClockWise, manual -> manual.clickedDirection().getAxis())
    );

    private final WrenchInteractionMap EMPTY = WrenchInteractionMap.create();

    private final WrenchInteractionMap AXIS_MISMATCH = WrenchInteractionMap.splitVertical(
            ROTATE_COUNTERCLOCKWISE,
            ROTATE_CLOCKWISE
    );

    private Optional<InteractionResult> rotate(WrenchActionContext context, RotationFunction rotationFunction, Function<WrenchActionContext.Manual, Direction.Axis> manualAxisFunction) {
        Level level = context.level();
        BlockPos pos = context.getTargetPos();
        BlockState state = context.getTargetState();
        Direction.Axis clickedAxis = switch (context) {
            case WrenchActionContext.Dispenser dispenser -> dispenser.getDispenserFacing().getAxis();
            case WrenchActionContext.Manual manual -> manualAxisFunction.apply(manual);
        };
        Property<Direction> property = this.getProperty();
        level.setBlockAndUpdate(pos, state.setValue(property, rotationFunction.rotate(state.getValue(property), clickedAxis)));
        return Optional.of(InteractionResult.SUCCESS);
    }

    @Override
    public WrenchInteraction getDispenserInteraction(WrenchActionContext.Dispenser context) {
        return context.getDispenserFacing().getAxisDirection().equals(Direction.AxisDirection.POSITIVE)
                ? ROTATE_CLOCKWISE
                : ROTATE_COUNTERCLOCKWISE;
    }

    public FacingBlockStateWrenchBehavior(ResourceLocation id) {
        super(BlockStateProperties.FACING, id);
    }

    @Override
    public WrenchInteractionMap getManualInteractionMap(WrenchActionContext.Manual context) {
        return context.getTargetState().getValue(this.getProperty()).getAxis().equals(context.clickedDirection().getAxis())
                ? EMPTY
                : AXIS_MISMATCH;
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

    private interface RotationFunction {
        Direction rotate(Direction facing, Direction.Axis rotateAround);
    }
}
