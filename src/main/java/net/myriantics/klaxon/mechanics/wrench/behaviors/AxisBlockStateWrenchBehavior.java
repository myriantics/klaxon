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

import java.util.Optional;

public class AxisBlockStateWrenchBehavior extends BlockStateWrenchBehavior<Direction.Axis> {

    protected final WrenchInteraction ROTATE_LEFT = WrenchInteraction.of(KlaxonWrenchActionTypes.ROTATE_LEFT, (context, rotation) -> handleRotateHorizontal(context, this.getProperty()));
    protected final WrenchInteraction ROTATE_RIGHT = WrenchInteraction.of(KlaxonWrenchActionTypes.ROTATE_RIGHT, (context, rotation) -> handleRotateHorizontal(context, this.getProperty()));
    //protected final WrenchInteraction ROTATE_UP = WrenchInteraction.of(KlaxonWrenchActionTypes.ROTATE_UP, context -> handleRotateVertical(context, this.getProperty()));
    //protected final WrenchInteraction ROTATE_DOWN = WrenchInteraction.of(KlaxonWrenchActionTypes.ROTATE_DOWN, context -> handleRotateVertical(context, this.getProperty()));
    protected final WrenchInteraction FLIP = WrenchInteraction.of(KlaxonWrenchActionTypes.FLIP, AxisBlockStateWrenchBehavior::handleFlip);

    protected final WrenchInteractionMap AXIS_MATCH = WrenchInteractionMap.create();

    public AxisBlockStateWrenchBehavior(ResourceLocation id) {
        super(BlockStateProperties.AXIS, id);
    }

    private static Optional<InteractionResult> handleFlip(WrenchActionContext context, BlockFaceRegion.Rotation rotation) {
        return Optional.empty();
    }

    //@Override
    public WrenchInteractionMap getManualInteractionMap(WrenchActionContext.Manual context) {
        return null;
    }

    //@Override
    public WrenchInteraction getDispenserInteraction(WrenchActionContext.Dispenser context) {
        return context.getTargetState().getValue(getProperty()).equals(context.getDispenserFacing().getAxis())
                ? WrenchInteraction.NO_OP
                : FLIP;
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

    private static Optional<InteractionResult> handleRotateHorizontal(WrenchActionContext context, Property<Direction.Axis> property) {
        if (!(context instanceof WrenchActionContext.Manual manual)) {
            throw new AssertionError();
        }

        Level level = context.level();
        BlockState state = context.getTargetState();
        BlockPos pos = context.getTargetPos();

        Direction.Axis axis = state.getValue(property);
        // manual.getHitResult().getLocation().

        return Optional.of(InteractionResult.SUCCESS);
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
