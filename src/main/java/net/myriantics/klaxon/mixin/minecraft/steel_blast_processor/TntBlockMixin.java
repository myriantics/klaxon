package net.myriantics.klaxon.mixin.minecraft.steel_blast_processor;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.block.machines.blast_processor.steel.SteelBlastProcessorExhaustHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(TntBlock.class)
public abstract class TntBlockMixin implements SteelBlastProcessorExhaustHandler {

    @Unique
    @Override
    public boolean klaxon$allowCustomExhaustHandling(ServerLevel level, BlockPos pos, BlockState state) {
        return true;
    }

    @Unique
    @Override
    public boolean klaxon$handleExhaust(ServerLevel level, BlockPos pos, BlockState state) {
        TntBlock.explode(level, pos);
        return true;
    }
}
