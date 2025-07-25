package net.myriantics.klaxon.mixin.wrench;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.myriantics.klaxon.item.equipment.tools.WrenchItem;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;
import net.myriantics.klaxon.util.PermissionsHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {
    @ModifyExpressionValue(
            method = "interactBlockInternal",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;shouldCancelInteraction()Z")
    )
    public boolean klaxon$wrenchInteractionCancelOverride(boolean original, @Local(argsOnly = true) ClientPlayerEntity player, @Local ItemStack usedStack, @Local(argsOnly = true) BlockHitResult hitResult) {
        BlockState targetState = player.clientWorld.getBlockState(hitResult.getBlockPos());
        CachedBlockPosition targetPos = new CachedBlockPosition(player.clientWorld, hitResult.getBlockPos(), false);

        // if we're in adventure and we can't do anything to the block, don't override anything
        if (!PermissionsHelper.canModifyWorld(player) && !usedStack.canPlaceOn(targetPos)) return original;
        return original || (usedStack.getItem() instanceof WrenchItem && targetState.isIn(KlaxonBlockTags.WRENCH_ROTATION_ALLOWLIST) && !targetState.isIn(KlaxonBlockTags.WRENCH_ROTATION_DENYLIST) && WrenchItem.getRotatedState(player.clientWorld, hitResult.getBlockPos(), targetState, hitResult.getSide(), player.getHorizontalFacing(), hitResult.getPos()).isPresent());
    }

}
