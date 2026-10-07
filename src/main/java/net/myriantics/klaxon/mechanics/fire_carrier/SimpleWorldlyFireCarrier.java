package net.myriantics.klaxon.mechanics.fire_carrier;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;


public interface SimpleWorldlyFireCarrier {
    default boolean klaxon$mayIgnite(Level level, BlockPos pos, FireCarrierInteractionContext context, BlockState state, @Nullable BlockEntity blockEntity) {
        return true;
    }

    void klaxon$ignite(Level level, BlockPos pos, FireCarrierInteractionContext context, BlockState state, @Nullable BlockEntity blockEntity);

    default boolean klaxon$mayExtinguish(Level level, BlockPos pos, FireCarrierInteractionContext context, BlockState state, @Nullable BlockEntity blockEntity) {
        return true;
    }

    void klaxon$extinguish(Level level, BlockPos pos, FireCarrierInteractionContext context, BlockState state, @Nullable BlockEntity blockEntity);

    boolean klaxon$isIgnited(Level level, BlockPos pos, FireCarrierInteractionContext context, BlockState state, @Nullable BlockEntity blockEntity);

    static String getExceptionMessage(BlockState state, String action) {
        return "An error was encountered when attempting to " + action + " " + state + ". Please report this to the KLAXON GitHub";
    }
}
