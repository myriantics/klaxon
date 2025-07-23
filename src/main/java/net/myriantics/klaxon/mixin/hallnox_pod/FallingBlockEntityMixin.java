package net.myriantics.klaxon.mixin.hallnox_pod;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.myriantics.klaxon.block.customblocks.functional.HallnoxPodBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Properties;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin extends Entity {

    @Shadow private BlockState block;

    public FallingBlockEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @ModifyExpressionValue(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;canPlaceAt(Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;)Z")
    )
    private boolean klaxon$rotateHallnoxPodWhenLanding(boolean original, @Local BlockPos blockPos) {
        // if we fail to place the hallnox pod, rotate it and try again
        if (!original && this.block.getBlock() instanceof HallnoxPodBlock && this.block.contains(HallnoxPodBlock.FACING)) {
            BlockState newState = block.with(HallnoxPodBlock.FACING, Direction.DOWN);

            // if we can place the rotated state, overwrite the stored state and return true - because we succeeded :)
            if (newState.canPlaceAt(getWorld(), blockPos)) {
                this.block = newState;
                return true;
            }
        }

        return original;
    }
}
