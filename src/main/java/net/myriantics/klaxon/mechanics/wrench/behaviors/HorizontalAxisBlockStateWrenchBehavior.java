package net.myriantics.klaxon.mechanics.wrench.behaviors;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.myriantics.klaxon.mechanics.wrench.BlockStateWrenchBehavior;
import net.myriantics.klaxon.mechanics.wrench.WrenchActionContext;
import net.myriantics.klaxon.mechanics.wrench.interaction.WrenchInteraction;
import net.myriantics.klaxon.mechanics.wrench.interaction.WrenchInteractionMap;
import net.myriantics.klaxon.registry.behavior.KlaxonWrenchActionTypes;
import net.myriantics.klaxon.util.BlockFaceRegion;

import java.util.Optional;

public class HorizontalAxisBlockStateWrenchBehavior extends BlockStateWrenchBehavior<Direction.Axis> {

    private final WrenchInteraction FLIP = WrenchInteraction.of(
            KlaxonWrenchActionTypes.FLIP,
            this::flip
    );

    private final WrenchInteractionMap FLIP_MAP = WrenchInteractionMap.fullBlock(FLIP);

    public HorizontalAxisBlockStateWrenchBehavior(ResourceLocation id) {
        super(BlockStateProperties.HORIZONTAL_AXIS, id);
    }

    private Optional<InteractionResult> flip(WrenchActionContext context, BlockFaceRegion.Rotation rotation) {
        context.level().setBlockAndUpdate(context.getTargetPos(), context.getTargetState().cycle(this.getProperty()));
        return Optional.of(InteractionResult.SUCCESS);
    }

    @Override
    public WrenchInteraction getDispenserInteraction(WrenchActionContext.Dispenser context) {
        return FLIP;
    }

    @Override
    public WrenchInteractionMap getManualInteractionMap(WrenchActionContext.Manual context) {
        return FLIP_MAP;
    }
}
