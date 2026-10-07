package net.myriantics.klaxon.mixin.minecraft.fire_carrier;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.mechanics.fire_carrier.FireCarrierInteractionContext;
import net.myriantics.klaxon.mechanics.fire_carrier.SimpleWorldlyFireCarrier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractCandleBlock.class)
public abstract class AbstractCandleBlockMixin extends Block implements SimpleWorldlyFireCarrier {

    @Shadow
    protected abstract boolean canBeLit(BlockState state);

    @Shadow
    private static void setLit(LevelAccessor level, BlockState state, BlockPos pos, boolean lit) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    public AbstractCandleBlockMixin(Properties properties) {
        super(properties);
    }

    @Override
    public boolean klaxon$mayIgnite(Level level, BlockPos pos, FireCarrierInteractionContext context, BlockState state, @Nullable BlockEntity blockEntity) {
        return this.canBeLit(state);
    }

    @Override
    public void klaxon$ignite(Level level, BlockPos pos, FireCarrierInteractionContext context, BlockState state, @Nullable BlockEntity blockEntity) {
        setLit(level, state, pos, true);
    }

    @Override
    public boolean klaxon$mayExtinguish(Level level, BlockPos pos, FireCarrierInteractionContext context, BlockState state, @Nullable BlockEntity blockEntity) {
        return AbstractCandleBlock.isLit(state);
    }

    @Override
    public void klaxon$extinguish(Level level, BlockPos pos, FireCarrierInteractionContext context, BlockState state, @Nullable BlockEntity blockEntity) {
        AbstractCandleBlock.extinguish(context.entity() instanceof Player player ? player : null, state, level, pos);
    }

    @Override
    public boolean klaxon$isIgnited(Level level, BlockPos pos, FireCarrierInteractionContext context, BlockState state, @Nullable BlockEntity blockEntity) {
        return AbstractCandleBlock.isLit(state);
    }
}
