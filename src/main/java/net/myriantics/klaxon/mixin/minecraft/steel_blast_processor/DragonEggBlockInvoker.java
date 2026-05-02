package net.myriantics.klaxon.mixin.minecraft.steel_blast_processor;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DragonEggBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DragonEggBlock.class)
public interface DragonEggBlockInvoker {
    @Invoker(value = "teleport")
    void klaxon$invokeTeleport(BlockState state, Level level, BlockPos pos);
}
