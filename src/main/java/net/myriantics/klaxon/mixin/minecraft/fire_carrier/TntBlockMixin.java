package net.myriantics.klaxon.mixin.minecraft.fire_carrier;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.mechanics.fire_carrier.FireCarrierInteractionContext;
import net.myriantics.klaxon.mechanics.fire_carrier.SimpleWorldlyFireCarrier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TntBlock.class)
public abstract class TntBlockMixin extends Block implements SimpleWorldlyFireCarrier {
    @Shadow
    private static void explode(Level level, BlockPos pos, @Nullable LivingEntity entity) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    public TntBlockMixin(Properties properties) {
        super(properties);
    }

    @Override
    public void klaxon$ignite(Level level, BlockPos pos, FireCarrierInteractionContext context, BlockState state, @Nullable BlockEntity blockEntity) {
        explode(level, pos, context.entity() instanceof LivingEntity livingEntity ? livingEntity : null);
        level.removeBlock(pos, false);
    }

    @Override
    public boolean klaxon$mayExtinguish(Level level, BlockPos pos, FireCarrierInteractionContext context, BlockState state, @Nullable BlockEntity blockEntity) {
        return false;
    }

    @Override
    public void klaxon$extinguish(Level level, BlockPos pos, FireCarrierInteractionContext context, BlockState state, @Nullable BlockEntity blockEntity) {
    }

    @Override
    public boolean klaxon$isIgnited(Level level, BlockPos pos, FireCarrierInteractionContext context, BlockState state, @Nullable BlockEntity blockEntity) {
        return false;
    }
}
