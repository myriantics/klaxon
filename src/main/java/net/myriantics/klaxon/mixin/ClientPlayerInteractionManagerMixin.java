package net.myriantics.klaxon.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.myriantics.klaxon.item.equipment.tools.WrenchItem;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {

    @ModifyExpressionValue(
            method = "interactBlockInternal",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;shouldCancelInteraction()Z")
    )
    public boolean klaxon$wrenchInteractionCancelOverride(boolean original, @Local(argsOnly = true) ClientPlayerEntity player, @Local ItemStack usedStack, @Local(argsOnly = true) BlockHitResult hitResult) {
        BlockState targetState = player.clientWorld.getBlockState(hitResult.getBlockPos());
        return original || (usedStack.getItem() instanceof WrenchItem && targetState.isIn(KlaxonBlockTags.WRENCH_ROTATION_ALLOWLIST) && WrenchItem.getRotatedState(player.clientWorld, hitResult.getBlockPos(), targetState, hitResult.getSide(), player.getHorizontalFacing(), hitResult.getPos()).isPresent());
    }
}
