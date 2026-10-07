package net.myriantics.klaxon.mixin.minecraft.steel_blast_processor;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.block.machines.blast_processor.steel.SteelBlastProcessorExhaustHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(IceBlock.class)
public abstract class IceBlockMixin extends HalfTransparentBlock implements SteelBlastProcessorExhaustHandler {
    @Shadow
    protected abstract void melt(BlockState state, Level level, BlockPos pos);

    public IceBlockMixin(Properties properties) {
        super(properties);
    }

    @Override
    public boolean klaxon$mayHandleExhaust(LevelAccessor level, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void klaxon$handleExhaust(ServerLevel level, BlockPos pos, BlockState state) {
        this.melt(state, level, pos);
    }
}
