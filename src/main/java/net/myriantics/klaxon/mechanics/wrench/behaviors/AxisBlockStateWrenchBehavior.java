package net.myriantics.klaxon.mechanics.wrench.behaviors;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
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

public class AxisBlockStateWrenchBehavior extends BlockStateWrenchBehavior<Direction.Axis> {
    public AxisBlockStateWrenchBehavior(Identifier id) {
        super(Properties.AXIS, id);
    }

    @Override
    protected Optional<Direction.Axis> applyManual(Direction.Axis original, ManualWrenchInteractionContext context) {
        Direction.Axis dispenserAxis = context.hitResult().getSide().getAxis();

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
