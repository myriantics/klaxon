package net.myriantics.klaxon.mixin.minecraft.grapple_winch.grapple_claw;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.myriantics.klaxon.entity.GrappleClawEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @WrapOperation(
            method = "checkBlockCollision",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;onEntityCollision(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/Entity;)V")
    )
    private void klaxon$triggerGrappleClawBlockBreaking(BlockState instance, World world, BlockPos pos, Entity entity, Operation<Void> original) {
        original.call(instance, world, pos, entity);

        if (entity instanceof GrappleClawEntity grappleClaw) {
            grappleClaw.onBlockPosIntersection(world, instance, pos);
        }
    }
}
