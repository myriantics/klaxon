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

public class AxisBlockStateWrenchBehavior extends BlockStateWrenchBehavior<Direction.Axis> {

    private final WrenchInteraction FLIP = WrenchInteraction.of(KlaxonWrenchActionTypes.FLIP, this::handleFlip);
    private final WrenchInteraction ROTATE_FORWARD = WrenchInteraction.of(KlaxonWrenchActionTypes.ROTATE_FORWARD, this::rotateForward);

    protected final WrenchInteractionMap AXIS_MISMATCH = WrenchInteractionMap.fullBlock(FLIP);
    protected final WrenchInteractionMap AXIS_MATCH = WrenchInteractionMap.fullBlock(ROTATE_FORWARD);

    public AxisBlockStateWrenchBehavior(ResourceLocation id) {
        super(BlockStateProperties.AXIS, id);
    }

    private Optional<InteractionResult> rotateForward(WrenchActionContext context, BlockFaceRegion.Rotation rotation) {
        Level level = context.level();
        BlockState state = context.getTargetState();
        Property<Direction.Axis> property = this.getProperty();

        return switch (context) {
            case WrenchActionContext.Dispenser dispenser -> Optional.of(InteractionResult.FAIL);
            case WrenchActionContext.Manual manual -> {
                level.setBlockAndUpdate(context.getTargetPos(), state.setValue(property, manual.getGuiOrientation().getGuiUpDir().getAxis()));
                yield Optional.of(InteractionResult.SUCCESS);
            }
        };
    }

    private Optional<InteractionResult> handleFlip(WrenchActionContext context, BlockFaceRegion.Rotation rotation) {
        Level level = context.level();
        BlockState state = context.getTargetState();
        Property<Direction.Axis> property = this.getProperty();
        Direction.Axis axis = state.getValue(property);

        Direction.Axis userAxis = switch (context) {
            case WrenchActionContext.Dispenser dispenser -> dispenser.getDispenserFacing().getAxis();
            case WrenchActionContext.Manual manual -> manual.getGuiOrientation().getFacing().getAxis();
        };

        if (axis.equals(userAxis)) {
            return Optional.of(InteractionResult.FAIL);
        } else {
            level.setBlockAndUpdate(context.getTargetPos(), state.setValue(property, KlaxonMathHelper.neither(axis, userAxis)));
            return Optional.of(InteractionResult.SUCCESS);
        }
    }

    @Override
    public WrenchInteractionMap getManualInteractionMap(WrenchActionContext.Manual context) {
        return context.getTargetState().getValue(this.getProperty()).equals(context.getGuiOrientation().getFacing().getAxis())
                ? AXIS_MATCH
                : AXIS_MISMATCH;
    }

    @Override
    public WrenchInteraction getDispenserInteraction(WrenchActionContext.Dispenser context) {
        return context.getTargetState().getValue(this.getProperty()).equals(context.getDispenserFacing().getAxis())
                ? WrenchInteraction.FAIL
                : FLIP;
    }
}
