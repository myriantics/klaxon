package net.myriantics.klaxon.mechanics.wrench.behaviors;

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

public class HorizontalFacingBlockStateWrenchBehavior extends BlockStateWrenchBehavior<Direction> {

    private final WrenchInteraction ROTATE_CLOCKWISE = WrenchInteraction.of(
            KlaxonWrenchActionTypes.ROTATE_CLOCKWISE,
            (context, rotation) -> this.rotate(context, rotation, RelativeDirection.LEFT)
    );
    private final WrenchInteraction ROTATE_COUNTERCLOCKWISE = WrenchInteraction.of(
            KlaxonWrenchActionTypes.ROTATE_COUNTERCLOCKWISE,
            (context, rotation) -> this.rotate(context, rotation, RelativeDirection.RIGHT)
    );
    private final WrenchInteraction FLIP = WrenchInteraction.of(
            KlaxonWrenchActionTypes.FLIP,
            this::flip
    );

    private final WrenchInteractionMap ROTATE_MAP = WrenchInteractionMap.splitVertical(ROTATE_CLOCKWISE, ROTATE_COUNTERCLOCKWISE).state2Rotation(BlockFaceRegion.State2Rotation::topOnly);

    public HorizontalFacingBlockStateWrenchBehavior(ResourceLocation id) {
        super(BlockStateProperties.HORIZONTAL_FACING, id);
    }

    private Optional<InteractionResult> flip(WrenchActionContext context, BlockFaceRegion.Rotation rotation) {
        Level level = context.level();
        BlockState state = context.getTargetState();
        Property<Direction> property = this.getProperty();
        Direction original = state.getValue(property);

        level.setBlockAndUpdate(context.getTargetPos(), state.setValue(property, original.getOpposite()));
        return Optional.of(InteractionResult.SUCCESS);
    }

    private Optional<InteractionResult> rotate(WrenchActionContext context, BlockFaceRegion.Rotation rotation, RelativeDirection relativeDirection) {
        Level level = context.level();
        BlockState state = context.getTargetState();
        Property<Direction> property = this.getProperty();
        Direction facing = state.getValue(property);

        try {
            level.setBlockAndUpdate(
                    context.getTargetPos(),
                    state.setValue(property, relativeDirection.get(facing.getOpposite(), Direction.UP))
            );
            return Optional.of(InteractionResult.SUCCESS);
        } catch (IllegalArgumentException e) {
            return Optional.of(InteractionResult.FAIL);
        }
    }

    @Override
    public WrenchInteractionMap getManualInteractionMap(WrenchActionContext.Manual context) {
        return ROTATE_MAP;
    }

    @Override
    public WrenchInteraction getDispenserInteraction(WrenchActionContext.Dispenser context) {
        return switch (context.getDispenserFacing()) {
            case DOWN -> ROTATE_CLOCKWISE;
            case UP -> ROTATE_COUNTERCLOCKWISE;
            default -> FLIP;
        };
    }
}
