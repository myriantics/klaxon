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
import net.myriantics.klaxon.mechanics.wrench.WrenchActionContext;
import net.myriantics.klaxon.mechanics.wrench.interaction.WrenchInteraction;
import net.myriantics.klaxon.mechanics.wrench.interaction.WrenchInteractionMap;
import net.myriantics.klaxon.mechanics.wrench.interaction.segments.InteractionMapSegment;
import net.myriantics.klaxon.registry.behavior.KlaxonWrenchActionTypes;
import net.myriantics.klaxon.util.BlockFaceRegion;
import net.myriantics.klaxon.util.RelativeDirection;

import java.util.Optional;

public class HopperFacingBlockStateWrenchBehavior extends BlockStateWrenchBehavior<Direction> {

    private final WrenchInteraction ROTATE_CLOCKWISE = WrenchInteraction.of(
            KlaxonWrenchActionTypes.ROTATE_CLOCKWISE,
            (context, rotation) -> this.rotate(context, rotation, RelativeDirection.RIGHT)
    );
    private final WrenchInteraction ROTATE_COUNTERCLOCKWISE = WrenchInteraction.of(
            KlaxonWrenchActionTypes.ROTATE_COUNTERCLOCKWISE,
            (context, rotation) -> this.rotate(context, rotation, RelativeDirection.LEFT)
    );
    private final WrenchInteraction ROTATE_DOWN = WrenchInteraction.of(
            KlaxonWrenchActionTypes.ROTATE_DOWN,
            this::rotateDown
    );
    private final WrenchInteraction FLIP = WrenchInteraction.of(
            KlaxonWrenchActionTypes.FLIP,
            this::flip
    );

    private final WrenchInteraction ALIGN = WrenchInteraction.of(
            KlaxonWrenchActionTypes.ALIGN,
            this::align
    );

    private final WrenchInteractionMap TOP_BOTTOM = WrenchInteractionMap.splitVertical(ROTATE_CLOCKWISE, ROTATE_COUNTERCLOCKWISE).state2Rotation(BlockFaceRegion.State2Rotation::topOnly);

    private final WrenchInteractionMap SIDES = WrenchInteractionMap.create()
            .add(InteractionMapSegment.of(ROTATE_CLOCKWISE, 0, 0, 6f/16, 1))
            .add(InteractionMapSegment.of(ROTATE_DOWN, 6f/16, 0, 10f/16, 1))
            .add(InteractionMapSegment.of(ROTATE_COUNTERCLOCKWISE, 10f/16, 0, 1, 1));
    private final WrenchInteractionMap IF_FACING_BOTTOM = WrenchInteractionMap.fullBlock(ALIGN);

    public HopperFacingBlockStateWrenchBehavior(ResourceLocation id) {
        super(BlockStateProperties.FACING_HOPPER, id);
    }

    private Optional<InteractionResult> rotate(WrenchActionContext context, BlockFaceRegion.Rotation rotation, RelativeDirection relativeDirection) {
        Level level = context.level();
        BlockPos pos = context.getTargetPos();
        BlockState state = context.getTargetState();
        Direction facing = state.getValue(this.getProperty());

        if (facing.equals(Direction.DOWN)) {
            return Optional.of(InteractionResult.FAIL);
        }

        try {
            level.setBlockAndUpdate(pos, state.setValue(this.getProperty(), relativeDirection.get(facing.getOpposite(), Direction.UP)));
            return Optional.of(InteractionResult.SUCCESS);
        } catch (IllegalArgumentException e) {
            return Optional.of(InteractionResult.FAIL);
        }
    }

    private Optional<InteractionResult> rotateDown(WrenchActionContext context, BlockFaceRegion.Rotation rotation) {
        context.level().setBlockAndUpdate(context.getTargetPos(), context.getTargetState().setValue(this.getProperty(), Direction.DOWN));
        return Optional.of(InteractionResult.SUCCESS);
    }

    private Optional<InteractionResult> align(WrenchActionContext context, BlockFaceRegion.Rotation rotation) {
        Direction newDir = switch (context) {
            case WrenchActionContext.Dispenser dispenser -> dispenser.getDispenserFacing().getOpposite();
            case WrenchActionContext.Manual manual -> manual.getPlayer().getMotionDirection().getOpposite();
        };

        try {
            context.level().setBlockAndUpdate(context.getTargetPos(), context.getTargetState().setValue(this.getProperty(), newDir));
            return Optional.of(InteractionResult.SUCCESS);
        } catch (IllegalArgumentException e) {
            return Optional.of(InteractionResult.FAIL);
        }
    }

    private Optional<InteractionResult> flip(WrenchActionContext context, BlockFaceRegion.Rotation rotation) {
        BlockState state = context.getTargetState();
        Direction facing = state.getValue(this.getProperty());

        if (facing.equals(Direction.DOWN)) {
            return Optional.of(InteractionResult.FAIL);
        } else {
            context.level().setBlockAndUpdate(context.getTargetPos(), state.setValue(this.getProperty(), facing.getOpposite()));
            return Optional.of(InteractionResult.SUCCESS);
        }
    }

    @Override
    public WrenchInteraction getDispenserInteraction(WrenchActionContext.Dispenser context) {
        Direction dispenserFacing = context.getDispenserFacing();
        Direction facing = context.getTargetState().getValue(this.getProperty());

        return switch (dispenserFacing.getAxis()) {
            case X, Z -> {
                if (facing.equals(Direction.DOWN)) {
                    yield ALIGN;
                } else {
                    yield FLIP;
                }
            }
            case Y -> {
                if (facing.equals(Direction.DOWN)) {
                    yield WrenchInteraction.FAIL;
                } else {
                    yield switch (dispenserFacing.getAxisDirection()) {
                        case POSITIVE -> ROTATE_COUNTERCLOCKWISE;
                        case NEGATIVE -> ROTATE_CLOCKWISE;
                    };
                }
            }
        };
    };

    @Override
    public WrenchInteractionMap getManualInteractionMap(WrenchActionContext.Manual context) {
        BlockState state = context.getTargetState();
        Property<Direction> property = this.getProperty();
        Direction facing = state.getValue(property);
        Direction guiFacing = context.getGuiOrientation().getFacing();

        if (facing.equals(Direction.DOWN)) {
            return IF_FACING_BOTTOM;
        }

        return switch (guiFacing.getAxis()) {
            case X, Z -> SIDES;
            case Y -> TOP_BOTTOM;
        };
    }
}
