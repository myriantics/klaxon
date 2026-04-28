package net.myriantics.klaxon.mixin.minecraft.precision_dispenser;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.myriantics.klaxon.mechanics.muffling.MufflableBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DefaultDispenseItemBehavior.class)
public abstract class DefaultDispenseItemBehaviorMixin {
    @WrapOperation(
            method = "dispense",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/core/dispenser/DefaultDispenseItemBehavior;playSound(Lnet/minecraft/core/dispenser/BlockSource;)V")
    )
    private void klaxon$blockSoundPlayingIfMuffled(DefaultDispenseItemBehavior instance, BlockSource blockSource, Operation<Void> original) {
        if (blockSource.blockEntity().getBlockState().getBlock() instanceof MufflableBlock mufflableBlock && mufflableBlock.hasMuffler(blockSource.level(), blockSource.pos())) {
            // bonked
        } else {
            original.call(instance, blockSource);
        }
    }
}
