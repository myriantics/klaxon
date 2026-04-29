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
import net.myriantics.klaxon.registry.behavior.KlaxonWrenchActionTypes;
import net.myriantics.klaxon.util.BlockFaceRegion;
import net.myriantics.klaxon.util.RelativeDirection;

import java.util.Optional;

public class FacingBlockStateWrenchBehavior extends BlockStateWrenchBehavior<Direction> {

    private final WrenchInteraction ROTATE_COUNTERCLOCKWISE = WrenchInteraction.of(
            KlaxonWrenchActionTypes.ROTATE_COUNTERCLOCKWISE,
            (context, rotation) -> this.rotateAround(context, rotation.equals(BlockFaceRegion.Rotation.R90) || rotation.equals(BlockFaceRegion.Rotation.R270) ? RelativeDirection.LEFT::get : RelativeDirection.RIGHT::get, manual -> manual.getGuiOrientation().getFacing())
    );
    private final WrenchInteraction ROTATE_CLOCKWISE = WrenchInteraction.of(
            KlaxonWrenchActionTypes.ROTATE_CLOCKWISE,
            (context, rotation) -> this.rotateAround(context, rotation.equals(BlockFaceRegion.Rotation.R90) || rotation.equals(BlockFaceRegion.Rotation.R270) ? RelativeDirection.RIGHT::get : RelativeDirection.LEFT::get, manual -> manual.getGuiOrientation().getFacing())
    );
    private final WrenchInteraction ROTATE_FORWARDS = WrenchInteraction.of(
            KlaxonWrenchActionTypes.ROTATE_FORWARD,
            (context, rotation) -> this.rotateAround(context, RelativeDirection.UP::get, manual -> manual.getTargetState().getValue(this.getProperty()).equals(manual.getGuiOrientation().getFacing()) ? manual.getGuiOrientation().getGuiUpDir() : manual.getGuiOrientation().getGuiUpDir().getOpposite())
    );
    private final WrenchInteraction ROTATE_BACKWARDS = WrenchInteraction.of(
            KlaxonWrenchActionTypes.ROTATE_BACKWARD,
            (context, rotation) -> this.rotateAround(context, RelativeDirection.DOWN::get, manual -> manual.getTargetState().getValue(this.getProperty()).equals(manual.getGuiOrientation().getFacing()) ? manual.getGuiOrientation().getGuiUpDir() : manual.getGuiOrientation().getGuiUpDir().getOpposite())
    );
    private final WrenchInteraction FLIP = WrenchInteraction.of(
            KlaxonWrenchActionTypes.FLIP,
            this::flip
    );

    private final WrenchInteractionMap AXIS_MISMATCH = WrenchInteractionMap.splitVertical(
            ROTATE_COUNTERCLOCKWISE,
            ROTATE_CLOCKWISE
    ).state2Rotation(BlockFaceRegion.State2Rotation::topOnly);

    private final WrenchInteractionMap AXIS_MATCH = WrenchInteractionMap.splitHorizontal(
            ROTATE_FORWARDS,
            ROTATE_BACKWARDS
    ).state2Rotation(BlockFaceRegion.State2Rotation::topOnly);

    private Optional<InteractionResult> flip(WrenchActionContext context, BlockFaceRegion.Rotation rotation) {
        Level level = context.level();
        BlockPos pos = context.getTargetPos();
        BlockState state = context.getTargetState();
        Property<Direction> property = this.getProperty();
        level.setBlockAndUpdate(pos, state.setValue(property, state.getValue(property).getOpposite()));
        return Optional.of(InteractionResult.SUCCESS);
    }

    private Optional<InteractionResult> rotateAround(WrenchActionContext context, RotationFunction function, RotationDirFunction manualRotationDirFunction) {
        Level level = context.level();
        BlockPos pos = context.getTargetPos();
        BlockState state = context.getTargetState();
        Property<Direction> property = this.getProperty();

        Direction rotationDir = switch (context) {
            case WrenchActionContext.Dispenser dispenser -> dispenser.getDispenserFacing();
            case WrenchActionContext.Manual manual -> manualRotationDirFunction.get(manual);
        };

        level.setBlockAndUpdate(pos, state.setValue(
                property,
                function.rotate(state.getValue(property), rotationDir)
        ));
        return Optional.of(InteractionResult.SUCCESS);
    }

    @Override
    public WrenchInteraction getDispenserInteraction(WrenchActionContext.Dispenser context) {
        Direction facing = context.getTargetState().getValue(this.getProperty());
        Direction dispenserFacing = context.getDispenserFacing();
        if (facing.getAxis().equals(dispenserFacing.getAxis())) {
            return FLIP;
        } else {
            return ROTATE_CLOCKWISE;
        }
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

    private interface RotationDirFunction {
        Direction get(WrenchActionContext.Manual manual);
    }

    private interface RotationFunction {
        Direction rotate(Direction original, Direction rotationDir);
    }
}
