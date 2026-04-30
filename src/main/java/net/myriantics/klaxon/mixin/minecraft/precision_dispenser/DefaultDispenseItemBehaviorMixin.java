package net.myriantics.klaxon.mixin.minecraft.precision_dispenser;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.block.machines.precision_dispenser.PrecisionDispenserBlock;
import net.myriantics.klaxon.block.machines.precision_dispenser.PrecisionDispenserBlockEntity;
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

    @WrapOperation(
            method = "execute",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/core/dispenser/DefaultDispenseItemBehavior;spawnItem(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;ILnet/minecraft/core/Direction;Lnet/minecraft/core/Position;)V")
    )
    private void klaxon$dispenseItemsPreciselyIfNeeded(Level level, ItemStack stack, int speed, Direction facing, Position position, Operation<Void> original, @Local(argsOnly = true) BlockSource source) {
        if (source.blockEntity() instanceof PrecisionDispenserBlockEntity) {
            original.call(level, stack, 0, facing, position);
        } else {
            original.call(level, stack, speed, facing, position);
        }
    }
}
