package net.myriantics.klaxon.mixin.minecraft.item_components.walljump_ability;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.ObserverBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ObserverBlock.class)
public interface ObserverBlockInvoker {

    // used for the hammer's observer activation ability
    @Invoker("tick")
    void invokeScheduledTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random);
}
