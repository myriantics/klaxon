package net.myriantics.klaxon.mixin.minecraft.neighbor_placement_listener;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.block.decor.hallnox_bulb.NeighborPlacementListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {

    @Inject(
            method = "place(Lnet/minecraft/world/item/context/BlockPlaceContext;)Lnet/minecraft/world/InteractionResult;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/BlockItem;updateCustomBlockEntityTag(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/block/state/BlockState;)Z")
    )
    public void klaxon$updateNeighboringHallnoxBulbs(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> cir, @Local(ordinal = 0) BlockState placedState) {
        Level world = context.getLevel();
        BlockPos placedPos = context.getClickedPos();
        BlockPos clickedBlockPos = placedPos.relative(context.getClickedFace().getOpposite());
        BlockState clickedState = world.getBlockState(clickedBlockPos);

        if (clickedState.getBlock() instanceof NeighborPlacementListener listener) {
            listener.onAdjacentPlaceOnSide(world, clickedBlockPos, clickedState, placedPos, placedState, context);
        }
    }
}
