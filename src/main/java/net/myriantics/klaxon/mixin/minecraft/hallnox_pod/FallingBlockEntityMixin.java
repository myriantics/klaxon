package net.myriantics.klaxon.mixin.minecraft.hallnox_pod;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.block.functional.hallnox_pod.HallnoxPodBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin extends Entity {

    @Shadow private BlockState blockState;

    public FallingBlockEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @ModifyExpressionValue(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;canBeReplaced(Lnet/minecraft/world/item/context/BlockPlaceContext;)Z")
    )
    private boolean klaxon$rotateHallnoxPodWhenLanding(boolean original, @Local BlockPos blockPos) {
        // if we fail to place the hallnox pod, rotate it and try again
        if (!original && this.blockState.getBlock() instanceof HallnoxPodBlock && this.blockState.hasProperty(HallnoxPodBlock.FACING)) {
            BlockState newState = blockState.setValue(HallnoxPodBlock.FACING, Direction.DOWN);

            // if we can place the rotated state, overwrite the stored state and return true - because we succeeded :)
            if (newState.canSurvive(level(), blockPos)) {
                this.blockState = newState;
                return true;
            }
        }

        return original;
    }
}
