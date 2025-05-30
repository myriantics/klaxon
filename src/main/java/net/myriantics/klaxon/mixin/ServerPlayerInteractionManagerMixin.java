package net.myriantics.klaxon.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.myriantics.klaxon.item.equipment.tools.WrenchItem;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class ServerPlayerInteractionManagerMixin {

    @Shadow protected ServerWorld world;

    @Shadow @Final protected ServerPlayerEntity player;

    @ModifyExpressionValue(
            method = "interactBlock",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;shouldCancelInteraction()Z")
    )
    public boolean klaxon$wrenchInteractionCancelOverride(boolean original, @Local(argsOnly = true) ItemStack usedStack, @Local(argsOnly = true) BlockHitResult hitResult, @Local BlockState targetState) {
        return original || (usedStack.getItem() instanceof WrenchItem && targetState.isIn(KlaxonBlockTags.WRENCH_ROTATION_ALLOWLIST) && WrenchItem.getRotatedState(world, hitResult.getBlockPos(), targetState, hitResult.getSide(), player.getHorizontalFacing(), hitResult.getPos()).isPresent());
    }
}
