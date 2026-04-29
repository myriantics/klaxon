package net.myriantics.klaxon.mechanics.wrench.behaviors;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.myriantics.klaxon.mechanics.wrench.BlockStateWrenchBehavior;
import net.myriantics.klaxon.mechanics.wrench.WrenchActionContext;
import net.myriantics.klaxon.mechanics.wrench.interaction.WrenchInteraction;
import net.myriantics.klaxon.mechanics.wrench.interaction.WrenchInteractionMap;
import net.myriantics.klaxon.mechanics.wrench.interaction.segments.InteractionMapSegment;
import net.myriantics.klaxon.registry.behavior.KlaxonWrenchActionTypes;
import net.myriantics.klaxon.util.BlockFaceRegion;
import net.myriantics.klaxon.util.RelativeDirection;

import java.util.Optional;
import java.util.function.UnaryOperator;

public class FrontAndTopBlockStateWrenchBehavior extends BlockStateWrenchBehavior<FrontAndTop> {

    private final WrenchInteraction FLIP = WrenchInteraction.of(
            KlaxonWrenchActionTypes.FLIP,
            this::flip
    );

    private final WrenchInteraction ROTATE_FORWARD = WrenchInteraction.of(
            KlaxonWrenchActionTypes.ROTATE_FORWARD,
            this::rotateFwd
    );

    private final WrenchInteraction ROTATE_BACKWARD = WrenchInteraction.of(
            KlaxonWrenchActionTypes.ROTATE_BACKWARD,
            (context, rotation) -> Optional.of(InteractionResult.FAIL)//this.rotateFwd(context, rotation, Direction::getOpposite))
    );

    private final WrenchInteraction ROTATE_CLOCKWISE = WrenchInteraction.NO_OP;
    private final WrenchInteraction ROTATE_COUNTERCLOCKWISE = WrenchInteraction.NO_OP;
    private final WrenchInteractionMap ORIENTATION_MISMATCH = WrenchInteractionMap.splitVertical(
            ROTATE_COUNTERCLOCKWISE,
            ROTATE_CLOCKWISE
    ).state2Rotation(BlockFaceRegion.State2Rotation::topOnly);

    private final WrenchInteractionMap ORIENTATION_MATCH = WrenchInteractionMap.create()
            .add(InteractionMapSegment.of(ROTATE_COUNTERCLOCKWISE, 0f/16, 0f/16, 5f/16, 16f/16))
            .add(InteractionMapSegment.of(ROTATE_BACKWARD, 5f/16, 0f/16, 11f/16, 8f/16))
            .add(InteractionMapSegment.of(ROTATE_FORWARD, 5f/16, 8f/16, 11f/16, 16f/16))
            .add(InteractionMapSegment.of(ROTATE_CLOCKWISE, 11f/16, 0f/16, 16f/16, 16f/16))
            .state2Rotation(BlockFaceRegion.State2Rotation::topOnly);

    public FrontAndTopBlockStateWrenchBehavior(ResourceLocation id) {
        super(BlockStateProperties.ORIENTATION, id);
    }

    private Optional<InteractionResult> flip(WrenchActionContext context, BlockFaceRegion.Rotation rotation) {
        Level level = context.level();
        BlockPos pos = context.getTargetPos();
        BlockState state = context.getTargetState();
        Property<FrontAndTop> property = this.getProperty();
        FrontAndTop existing = state.getValue(property);
        level.setBlockAndUpdate(pos, state.setValue(property, FrontAndTop.fromFrontAndTop(existing.front().getOpposite(), existing.top())));
        return Optional.of(InteractionResult.SUCCESS);
    }

    private Optional<InteractionResult> rotateFwd(WrenchActionContext context, BlockFaceRegion.Rotation rotation) {
        if (!(context instanceof WrenchActionContext.Manual manual)) {
            return Optional.of(InteractionResult.FAIL);
        }

        Level level = context.level();
        BlockPos pos = context.getTargetPos();
        BlockState state = context.getTargetState();
        Property<FrontAndTop> property = this.getProperty();
        WrenchActionContext.GuiOrientation orientation = manual.getGuiOrientation();
        FrontAndTop frontAndTop = state.getValue(property);
        Direction top = frontAndTop.top();
        Direction front = frontAndTop.front();
        Direction back = front.getOpposite();

        try {
            Direction newTop = front.getAxis().equals(Direction.Axis.Y) ? Direction.UP : back;


            FrontAndTop newFrontAndTop = FrontAndTop.fromFrontAndTop(top, newTop);
            level.setBlockAndUpdate(pos, state.setValue(property, newFrontAndTop));
            return Optional.of(InteractionResult.SUCCESS);
        } catch (Throwable t) {
            return Optional.of(InteractionResult.FAIL);
        }
    }

    @Override
    public WrenchInteraction getDispenserInteraction(WrenchActionContext.Dispenser context) {
        FrontAndTop frontAndTop = context.getTargetState().getValue(this.getProperty());

        if (frontAndTop.top().getAxis().equals(Direction.Axis.Y) && frontAndTop.front().getAxis().equals(context.getDispenserFacing().getAxis())) {
            return FLIP;
        } else {
            return ROTATE_CLOCKWISE;
        }
    }

    @Override
    public WrenchInteractionMap getManualInteractionMap(WrenchActionContext.Manual context) {
        FrontAndTop frontAndTop = context.getTargetState().getValue(this.getProperty());

        WrenchActionContext.GuiOrientation guiOrientation = context.getGuiOrientation();

        if (guiOrientation.matches(frontAndTop) || guiOrientation.matches(frontAndTop.front(), frontAndTop.top().getOpposite()) || guiOrientation.matches(frontAndTop.front().getOpposite(), frontAndTop.top()) || guiOrientation.matches(frontAndTop.front().getOpposite(), frontAndTop.top().getOpposite())) {
            return ORIENTATION_MATCH;
        } else {
            return ORIENTATION_MISMATCH;
        }
    }
}
