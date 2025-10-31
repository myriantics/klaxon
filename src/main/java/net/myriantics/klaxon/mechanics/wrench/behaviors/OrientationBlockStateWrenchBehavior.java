package net.myriantics.klaxon.mechanics.wrench.behaviors;

import net.minecraft.block.BlockState;
import net.minecraft.block.enums.Orientation;
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

public class OrientationBlockStateWrenchBehavior extends BlockStateWrenchBehavior<Orientation> {
    public OrientationBlockStateWrenchBehavior(Identifier id) {
        super(Properties.ORIENTATION, id);
    }

    @Override
    protected Optional<Orientation> applyManual(Orientation original, ManualWrenchInteractionContext context) {
        Direction facing = original.getFacing();
        Direction rotation = original.getRotation();
        Direction.Axis clickedAxis = context.hitResult().getSide().getAxis();

        if (facing.getAxis().equals(clickedAxis)) {
            return Optional.ofNullable(Orientation.byDirections(facing, rotation.rotateClockwise(clickedAxis)));
        } else {
            return Optional.ofNullable(Orientation.byDirections(facing.rotateClockwise(clickedAxis), rotation));
        }
    }

    @Override
    protected Optional<Orientation> applyDispenser(Orientation original, DispenserWrenchInteractionContext context) {
        Direction facing = original.getFacing();
        Direction rotation = original.getRotation();
        Direction.Axis dispenserAxis = context.dispenserFacing().getAxis();

        if (facing.getAxis().equals(dispenserAxis)) {
            return Optional.of(Orientation.byDirections(facing, rotation.rotateClockwise(dispenserAxis)));
        } else {
            return Optional.of(Orientation.byDirections(facing.rotateClockwise(dispenserAxis), rotation));
        }
    }
}
