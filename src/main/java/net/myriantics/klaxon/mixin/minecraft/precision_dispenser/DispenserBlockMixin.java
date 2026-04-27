package net.myriantics.klaxon.mixin.minecraft.precision_dispenser;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.myriantics.klaxon.block.machines.precision_dispenser.PrecisionDispenserBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(DispenserBlock.class)
public abstract class DispenserBlockMixin extends BaseEntityBlock {
    protected DispenserBlockMixin(Properties properties) {
        super(properties);
    }

    @WrapOperation(
            method = "neighborChanged",
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;above()Lnet/minecraft/core/BlockPos;")
            ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;hasNeighborSignal(Lnet/minecraft/core/BlockPos;)Z")
    )
    private boolean klaxon$disableQuasiconnectivityOnPrecisionDispenser(Level instance, BlockPos pos, Operation<Boolean> original) {
        return !((Object) this instanceof PrecisionDispenserBlock) && original.call(instance, pos);
    }
}
