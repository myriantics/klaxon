package net.myriantics.klaxon.mixin.blast_processor;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.myriantics.klaxon.block.blockentities.blast_processor.BlastProcessorBlockEntity;
import net.myriantics.klaxon.block.customblocks.BlastProcessorBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerOverride {

    @ModifyVariable(
            method = "interactBlockInternal",
            at = @At(value = "LOAD", ordinal = 0),
            ordinal = 1)
    public boolean checkForFastInput(
            boolean original,
            @Local(argsOnly = true) ClientPlayerEntity player,
            @Local(argsOnly = true) BlockHitResult hitResult,
            @Local(argsOnly = true) Hand hand,
            @Local(ordinal = 0) boolean isHoldingSomething) {

        World world = player.getWorld();
        BlockState state = world.getBlockState(hitResult.getBlockPos());
        Direction dir = hitResult.getSide();
        ItemStack handStack = player.getStackInHand(hand);

        // yay! now we're not doing item processing on the client
        // no more janky shit networking stuff is required!
        if (state.getBlock() instanceof BlastProcessorBlock) {
            return !BlastProcessorBlock.canFastInput(state, dir) && original;
        }

        return original;
    }
}