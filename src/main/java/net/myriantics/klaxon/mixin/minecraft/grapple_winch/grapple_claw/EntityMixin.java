package net.myriantics.klaxon.mixin.minecraft.grapple_winch.grapple_claw;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.entity.entities.grapple_claw.GrappleClawBlockDestructionHelper;
import net.myriantics.klaxon.entity.entities.grapple_claw.GrappleClawEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @WrapOperation(
            method = "checkInsideBlocks",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;entityInside(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;)V")
    )
    private void klaxon$triggerGrappleClawBlockBreaking(BlockState instance, Level world, BlockPos pos, Entity entity, Operation<Void> original) {
        original.call(instance, world, pos, entity);

        if (entity instanceof GrappleClawEntity grappleClaw) {
            GrappleClawBlockDestructionHelper.onBlockPosIntersection(grappleClaw, world, instance, pos);
        }
    }
}
