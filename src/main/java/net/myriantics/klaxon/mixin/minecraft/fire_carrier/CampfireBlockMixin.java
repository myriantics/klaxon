package net.myriantics.klaxon.mixin.minecraft.fire_carrier;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.mechanics.fire_carrier.FireCarrierInteractionContext;
import net.myriantics.klaxon.mechanics.fire_carrier.SimpleWorldlyFireCarrier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CampfireBlock.class)
public abstract class CampfireBlockMixin extends BaseEntityBlock implements SimpleWorldlyFireCarrier {

    protected CampfireBlockMixin(Properties properties) {
        super(properties);
    }

    @Override
    public boolean klaxon$mayExtinguish(Level level, BlockPos pos, FireCarrierInteractionContext context, BlockState state, @Nullable BlockEntity blockEntity) {
        return CampfireBlock.isLitCampfire(state);
    }

    @Override
    public boolean klaxon$mayIgnite(Level level, BlockPos pos, FireCarrierInteractionContext context, BlockState state, @Nullable BlockEntity blockEntity) {
        return CampfireBlock.canLight(state);
    }

    @Override
    public void klaxon$extinguish(Level level, BlockPos pos, FireCarrierInteractionContext context, BlockState state, @Nullable BlockEntity blockEntity) {
        CampfireBlock.dowse(null, level, pos, state);
        level.setBlockAndUpdate(pos, state.setValue(CampfireBlock.LIT, false));
    }

    @Override
    public void klaxon$ignite(Level level, BlockPos pos, FireCarrierInteractionContext context, BlockState state, @Nullable BlockEntity blockEntity) {
        level.setBlockAndUpdate(pos, state.setValue(CampfireBlock.LIT, true));
    }

    @Override
    public boolean klaxon$isIgnited(Level level, BlockPos pos, FireCarrierInteractionContext context, BlockState state, @Nullable BlockEntity blockEntity) {
        return CampfireBlock.isLitCampfire(state);
    }
}
