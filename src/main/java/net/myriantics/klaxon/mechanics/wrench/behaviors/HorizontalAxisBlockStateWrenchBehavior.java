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

public class HorizontalAxisBlockStateWrenchBehavior extends BlockStateWrenchBehavior<Direction.Axis> {
    public HorizontalAxisBlockStateWrenchBehavior(Identifier id) {
        super(Properties.HORIZONTAL_AXIS, id);
    }

    @Override
    protected Optional<Direction.Axis> applyManual(Direction.Axis original, ManualWrenchInteractionContext context) {
        return doThing(original, context.targetState(), context.hitResult().getBlockPos());
    }

    @Override
    protected Optional<Direction.Axis> applyDispenser(Direction.Axis original, DispenserWrenchInteractionContext context) {
        return doThing(original, context.targetState(), context.targetPos());
    }

    private static Optional<Direction.Axis> doThing(Direction.Axis original, BlockState state, BlockPos pos) {
        switch (original) {
            case X -> {
                return Optional.of(Direction.Axis.Z);
            }
            case Z -> {
                return Optional.of(Direction.Axis.X);
            }
            case Y -> {
                throw new IllegalStateException("Encountered Y axis when performing wrench operation on horizontal block state " + state + " at " + pos);
            }
        };

        return Optional.empty();
    }
}
