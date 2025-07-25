package net.myriantics.klaxon.mixin.item_components.walljump_ability;

import net.minecraft.block.BlockState;
import net.minecraft.block.ObserverBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ObserverBlock.class)
public interface ObserverBlockInvoker {

    // used for the hammer's observer activation ability
    @Invoker("scheduledTick")
    void invokeScheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random);
}
