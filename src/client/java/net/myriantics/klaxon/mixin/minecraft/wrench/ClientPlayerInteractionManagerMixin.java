package net.myriantics.klaxon.mixin.minecraft.wrench;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import net.myriantics.klaxon.mechanics.wrench.Wrenchable;
import net.myriantics.klaxon.item.equipment.tools.WrenchItem;
import net.myriantics.klaxon.mechanics.wrench.ManualWrenchInteractionContext;
import net.myriantics.klaxon.tag.convention.KlaxonConventionalItemTags;
import net.myriantics.klaxon.util.PermissionsHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {

    @ModifyExpressionValue(
            method = "interactBlockInternal",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;shouldCancelInteraction()Z")
    )
    public boolean klaxon$wrenchInteractionCancelOverride(boolean original, @Local(argsOnly = true) Hand hand, @Local(argsOnly = true) ClientPlayerEntity player, @Local ItemStack usedStack, @Local(argsOnly = true) BlockHitResult hitResult) {
        BlockState targetState = player.clientWorld.getBlockState(hitResult.getBlockPos());
        CachedBlockPosition targetPos = new CachedBlockPosition(player.clientWorld, hitResult.getBlockPos(), false);

        // if we're in adventure and we can't do anything to the block, don't override anything
        if (!PermissionsHelper.canModifyWorld(player) && !usedStack.canPlaceOn(targetPos)) return original;
        return original || (usedStack.getItem() instanceof WrenchItem && !WrenchItem.canRotate(player.clientWorld.getRegistryManager(), targetState));
    }

    @WrapOperation(
            method = "interactBlockInternal",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;onUseWithItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ItemActionResult;")
    )
    public ItemActionResult klaxon$runWrenchableFunctionalities(BlockState instance, ItemStack stack, World world, PlayerEntity player, Hand hand, BlockHitResult hitResult, Operation<ItemActionResult> original) {
        if (instance.getBlock() instanceof Wrenchable wrenchable && (stack.isIn(KlaxonConventionalItemTags.WRENCHES) || stack.isIn(KlaxonConventionalItemTags.WRENCH))) {
            ItemActionResult result = wrenchable.onManualWrenchInteraction(new ManualWrenchInteractionContext(instance, stack, world, player, hand, hitResult));
            if (result != null && !result.toActionResult().equals(ActionResult.PASS)) {
                return result;
            }
        }

        return original.call(instance, stack, world, player, hand, hitResult);
    }
}
