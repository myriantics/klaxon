package net.myriantics.klaxon.mixin.hallnox_bulb;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.myriantics.klaxon.block.customblocks.decor.HallnoxBulbBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {

    @Inject(
            method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/BlockItem;postPlacement(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/block/BlockState;)Z")
    )
    public void klaxon$updateNeighboringHallnoxBulbs(ItemPlacementContext context, CallbackInfoReturnable<ActionResult> cir, @Local PlayerEntity player, @Local(ordinal = 0) BlockState placedState) {
        World world = context.getWorld();
        BlockPos placedPos = context.getBlockPos();
        BlockPos clickedBlockPos = placedPos.offset(context.getSide().getOpposite());
        BlockState clickedState = world.getBlockState(clickedBlockPos);

        if (!player.isSneaking() && clickedState.getBlock() instanceof HallnoxBulbBlock hallnoxBulbBlock) {
            hallnoxBulbBlock.onAdjacentPlaceOnSideWhileNotCrouching(world, clickedBlockPos, clickedState, placedPos, placedState, context.getSide());
        }
    }
}
