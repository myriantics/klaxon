package net.myriantics.klaxon.mixin.minecraft.steel_blast_processor;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DragonEggBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.block.machines.blast_processor.steel.SteelBlastProcessorExhaustHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(DragonEggBlock.class)
public abstract class DragonEggBlockMixin implements SteelBlastProcessorExhaustHandler {

    @Shadow
    protected abstract void teleport(BlockState state, Level level, BlockPos pos);

    @Unique
    @Override
    public boolean klaxon$handleExhaust(Level level, BlockPos pos, BlockState state) {
        this.teleport(state, level, pos);
        return true;
    }
}
