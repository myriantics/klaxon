package net.myriantics.klaxon.mechanics.wrench.behaviors;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.myriantics.klaxon.mechanics.wrench.BlockStateWrenchBehavior;
import net.myriantics.klaxon.mechanics.wrench.DispenserWrenchInteractionContext;
import net.myriantics.klaxon.mechanics.wrench.ManualWrenchInteractionContext;

import java.util.Optional;

public class HorizontalFacingBlockStateWrenchBehavior extends BlockStateWrenchBehavior<Direction> {

    public HorizontalFacingBlockStateWrenchBehavior(Identifier id) {
        super(Properties.HORIZONTAL_FACING, id);
    }

    @Override
    protected Optional<Direction> applyManual(Direction original, ManualWrenchInteractionContext context) {
        Direction.Axis axis = original.getAxis();
        Direction clickedDirection = context.hitResult().getSide();
        Direction.Axis clickedAxis = clickedDirection.getAxis();

        if (clickedAxis.equals(Direction.Axis.Y)) {
            return Optional.of(original.rotateClockwise(Direction.Axis.Y));
        }

        if (clickedAxis.equals(axis)) {
            return Optional.of(original.getOpposite());
        }

        return Optional.of(clickedDirection.getOpposite());
    }

    @Override
    protected Optional<Direction> applyDispenser(Direction original, DispenserWrenchInteractionContext context) {
        Direction.Axis dispenserAxis = context.dispenserFacing().getAxis();

        if (dispenserAxis.equals(Direction.Axis.Y)) {
            return Optional.of(original.rotateClockwise(Direction.Axis.Y));
        }

        return Optional.of(original.getOpposite());
    }
}
