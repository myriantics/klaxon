package net.myriantics.klaxon.mechanics.wrench.behaviors;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
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
import net.myriantics.klaxon.util.BlockFaceRegion;
import net.myriantics.klaxon.util.KlaxonMathHelper;

import java.util.Optional;

public class FacingBlockStateWrenchBehavior extends BlockStateWrenchBehavior<Direction> {

    private final WrenchInteraction ROTATE_COUNTERCLOCKWISE = WrenchInteraction.of(
            KlaxonWrenchActionTypes.ROTATE_COUNTERCLOCKWISE,
            (context, rotation) -> this.rotateAround(context, Direction::getCounterClockWise, manual -> manual.clickedDirection().getAxis())
    );
    private final WrenchInteraction ROTATE_CLOCKWISE = WrenchInteraction.of(
            KlaxonWrenchActionTypes.ROTATE_CLOCKWISE,
            (context, rotation) -> this.rotateAround(context, Direction::getClockWise, manual -> manual.clickedDirection().getAxis())
    );
    private final WrenchInteraction ROTATE_FORWARDS = WrenchInteraction.of(
            KlaxonWrenchActionTypes.ROTATE_FORWARD,
            (context, rotation) -> this.rotateAround(context, Direction::getCounterClockWise, manual -> manual.getGuiOrientation().getSidesAxis())
    );

    private final WrenchInteraction ROTATE_BACKWARDS = WrenchInteraction.of(
            KlaxonWrenchActionTypes.ROTATE_BACKWARD,
            (context, rotation) -> this.rotateAround(context, Direction::getClockWise, manual -> manual.getGuiOrientation().getSidesAxis())
    );

    private final WrenchInteractionMap AXIS_MISMATCH = WrenchInteractionMap.splitVertical(
            ROTATE_COUNTERCLOCKWISE,
            ROTATE_CLOCKWISE
    ).state2Rotation(BlockFaceRegion.State2Rotation::topOnly);

    private BlockFaceRegion.Rotation rotate(BlockState state, WrenchActionContext.GuiOrientation orientation) {
        return switch (state.getValue(this.getProperty()).getAxis()) {
            case X, Y, Z -> BlockFaceRegion.Rotation.R0;
        };
    }

    private final WrenchInteractionMap AXIS_MATCH = WrenchInteractionMap.splitHorizontal(
            ROTATE_FORWARDS,
            ROTATE_BACKWARDS
    ).state2Rotation(this::rotate);

    private Optional<InteractionResult> rotateAround(WrenchActionContext context, RotationFunction function, AxisFunction manualAxisFunction) {
        Level level = context.level();
        BlockPos pos = context.getTargetPos();
        BlockState state = context.getTargetState();
        Direction.Axis axis = switch (context) {
            case WrenchActionContext.Dispenser dispenser -> dispenser.getDispenserFacing().getAxis();
            case WrenchActionContext.Manual manual -> manualAxisFunction.get(manual);
        };
        Property<Direction> property = this.getProperty();
        level.setBlockAndUpdate(pos, state.setValue(
                property,
                function.rotate(state.getValue(property), axis)
        ));
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
                ? AXIS_MATCH
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

    private interface AxisFunction {
        Direction.Axis get(WrenchActionContext.Manual manual);
    }

    private interface RotationFunction {
        Direction rotate(Direction original, Direction.Axis rotationAxis);
    }
}
