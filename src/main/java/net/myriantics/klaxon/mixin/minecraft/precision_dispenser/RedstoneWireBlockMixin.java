package net.myriantics.klaxon.mixin.minecraft.precision_dispenser;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.block.machines.precision_dispenser.PrecisionDispenserBlock;
import net.myriantics.klaxon.mechanics.wire_redirector.KlaxonRedstoneWireRedirector;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(RedStoneWireBlock.class)
public abstract class RedstoneWireBlockMixin {
    @ModifyReturnValue(
            method = "shouldConnectTo(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;)Z",
            slice = @Slice(
                    from = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/Blocks;OBSERVER:Lnet/minecraft/world/level/block/Block;", opcode = Opcodes.GETSTATIC)
            ),
            at = @At(value = "RETURN")
    )
    private static boolean klaxon$redirectToPrecisionDispenser(boolean original, @Local(argsOnly = true) BlockState targetState, @Local(argsOnly = true) Direction connectionDirection) {
        if (original) {
            return true;
        }

        if (targetState.getBlock() instanceof KlaxonRedstoneWireRedirector redirector) {
            return redirector.shouldRedirect(targetState, connectionDirection);
        }

        return false;
    }
}
