package net.myriantics.klaxon.mixin.minecraft.wrench;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.myriantics.klaxon.mechanics.wrench.Wrenchable;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.phys.BlockHitResult;
import net.myriantics.klaxon.item.equipment.tools.WrenchItem;
import net.myriantics.klaxon.mechanics.wrench.ManualWrenchInteractionContext;
import net.myriantics.klaxon.tag.convention.KlaxonConventionalItemTags;
import net.myriantics.klaxon.util.PermissionsHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin {

    @ModifyExpressionValue(
            method = "performUseItemOn",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSecondaryUseActive()Z")
    )
    public boolean klaxon$wrenchInteractionCancelOverride(boolean original, @Local(argsOnly = true) InteractionHand hand, @Local(argsOnly = true) LocalPlayer player, @Local ItemStack usedStack, @Local(argsOnly = true) BlockHitResult hitResult) {
        BlockState targetState = player.clientLevel.getBlockState(hitResult.getBlockPos());
        BlockInWorld targetPos = new BlockInWorld(player.clientLevel, hitResult.getBlockPos(), false);

        // if we're in adventure and we can't do anything to the block, don't override anything
        if (!PermissionsHelper.canModifyWorld(player) && !usedStack.canPlaceOnBlockInAdventureMode(targetPos)) return original;
        return original || (usedStack.getItem() instanceof WrenchItem && !WrenchItem.canRotate(player.clientLevel.registryAccess(), targetState));
    }

    @WrapOperation(
            method = "performUseItemOn",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;useItemOn(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/ItemInteractionResult;")
    )
    public ItemInteractionResult klaxon$runWrenchableFunctionalities(BlockState instance, ItemStack stack, Level world, Player player, InteractionHand hand, BlockHitResult hitResult, Operation<ItemInteractionResult> original) {
        if (instance.getBlock() instanceof Wrenchable wrenchable && (stack.is(KlaxonConventionalItemTags.WRENCHES) || stack.is(KlaxonConventionalItemTags.WRENCH))) {
            ItemInteractionResult result = wrenchable.onManualWrenchInteraction(new ManualWrenchInteractionContext(instance, stack, world, player, hand, hitResult));
            if (result != null && !result.result().equals(InteractionResult.PASS)) {
                return result;
            }
        }

        return original.call(instance, stack, world, player, hand, hitResult);
    }
}
