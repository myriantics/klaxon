package net.myriantics.klaxon.mixin.minecraft.grapple_winch.grapple_claw;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.myriantics.klaxon.entity.entities.grapple_claw.GrappleClawBlockDestructionHelper;
import net.myriantics.klaxon.entity.entities.grapple_claw.GrappleClawEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @WrapOperation(
            method = "checkBlockCollision",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;onEntityCollision(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/Entity;)V")
    )
    private void klaxon$triggerGrappleClawBlockBreaking(BlockState instance, World world, BlockPos pos, Entity entity, Operation<Void> original) {
        original.call(instance, world, pos, entity);

        if (entity instanceof GrappleClawEntity grappleClaw) {
            GrappleClawBlockDestructionHelper.onBlockPosIntersection(grappleClaw, world, instance, pos);
        }
    }
}
