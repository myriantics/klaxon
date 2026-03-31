package net.myriantics.klaxon.mechanics.wrench.behaviors;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.myriantics.klaxon.mechanics.wrench.*;
import net.myriantics.klaxon.mechanics.wrench.interaction.WrenchInteraction;
import net.myriantics.klaxon.mechanics.wrench.interaction.WrenchInteractionMap;
import net.myriantics.klaxon.registry.behavior.KlaxonWrenchActionTypes;
import net.myriantics.klaxon.util.BlockFaceRegion;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class StraightRailShapeBlockStateWrenchBehavior extends BlockStateWrenchBehavior<RailShape> {

    private final WrenchInteraction FLIP = WrenchInteraction.of(
            KlaxonWrenchActionTypes.FLIP,
            this::flip
    );

    private final WrenchInteraction ALIGN = WrenchInteraction.of(
            KlaxonWrenchActionTypes.ALIGN,
            this::flip
    );

    private final WrenchInteraction RAISE = WrenchInteraction.of(
            KlaxonWrenchActionTypes.RAISE,
            this::toggleAscension
    );

    private final WrenchInteraction LOWER = WrenchInteraction.of(
            KlaxonWrenchActionTypes.LOWER,
            this::toggleAscension
    );

    private final WrenchInteractionMap FLIP_MAP = WrenchInteractionMap.fullBlock(FLIP);
    private final WrenchInteractionMap ALIGN_MAP = WrenchInteractionMap.fullBlock(ALIGN);
    private final WrenchInteractionMap RAISE_MAP = WrenchInteractionMap.fullBlock(RAISE);
    private final WrenchInteractionMap LOWER_MAP = WrenchInteractionMap.fullBlock(LOWER);

    public StraightRailShapeBlockStateWrenchBehavior(ResourceLocation id) {
        super(BlockStateProperties.RAIL_SHAPE_STRAIGHT, id);
    }

    private Optional<InteractionResult> flip(WrenchActionContext context, BlockFaceRegion.Rotation rotation) {
        context.level().setBlockAndUpdate(context.getTargetPos(), context.getTargetState().setValue(this.getProperty(), KlaxonRailHelper.rotateClockwise(context.getTargetState().getValue(this.getProperty()))));
        return Optional.of(InteractionResult.SUCCESS);
    }

    private Optional<InteractionResult> toggleAscension(WrenchActionContext context, BlockFaceRegion.Rotation rotation) {
        Level level = context.level();
        BlockPos pos = context.getTargetPos();
        BlockState state = context.getTargetState();
        Property<RailShape> property = this.getProperty();
        RailShape shape = state.getValue(property);

        if (shape.isAscending()) {
            level.setBlockAndUpdate(pos, state.setValue(property, KlaxonRailHelper.getLowered(shape)));
            return Optional.of(InteractionResult.SUCCESS);
        }

        Direction ascensionDirection = switch (context) {
            case WrenchActionContext.Dispenser dispenser -> dispenser.getDispenserFacing().getOpposite();
            case WrenchActionContext.Manual manual -> manual.getPlayer().getMotionDirection();
        };

        if (KlaxonRailHelper.canAscend(level, shape, pos, ascensionDirection)) {
            level.setBlockAndUpdate(pos, state.setValue(property, KlaxonRailHelper.getRaised(shape, ascensionDirection.getAxisDirection())));
            return Optional.of(InteractionResult.SUCCESS);
        } else {
            return Optional.of(InteractionResult.FAIL);
        }
    }

    @Override
    public WrenchInteractionMap getManualInteractionMap(WrenchActionContext.Manual context) {
        Level level = context.level();
        BlockState state = context.getTargetState();
        RailShape shape = state.getValue(this.getProperty());
        Direction horizFacing = context.getPlayer().getMotionDirection();
        @Nullable Direction.Axis railAxis = KlaxonRailHelper.railShapeToAxis(shape);

        if (shape.isAscending()) {
            return LOWER_MAP;
        }

        if (railAxis == null || !railAxis.equals(horizFacing.getAxis())) {
            return ALIGN_MAP;
        } else if (KlaxonRailHelper.canAscend(level, shape, context.getTargetPos(), horizFacing)) {
            return RAISE_MAP;
        } else {
            return FLIP_MAP;
        }
    }

    @Override
    public WrenchInteraction getDispenserInteraction(WrenchActionContext.Dispenser context) {
        BlockState state = context.getTargetState();
        RailShape shape = state.getValue(this.getProperty());
        @Nullable Direction.Axis railAxis = KlaxonRailHelper.railShapeToAxis(shape);
        Direction.Axis dispenserAxis = context.getDispenserFacing().getAxis();

        if (shape.isAscending()) {
            return LOWER;
        }

        return switch (dispenserAxis) {
            case X, Z -> {
                if (dispenserAxis.equals(railAxis)) {
                    yield RAISE;
                } else {
                    yield ALIGN;
                }
            }
            case Y -> FLIP;
        };
    }
}
