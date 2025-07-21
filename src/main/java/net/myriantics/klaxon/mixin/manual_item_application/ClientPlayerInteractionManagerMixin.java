package net.myriantics.klaxon.mixin.manual_item_application;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import net.myriantics.klaxon.recipe.manual_item_application.ManualItemApplicationRecipeInput;
import net.myriantics.klaxon.recipe.manual_item_application.ManualItemApplicationRecipeLogic;
import net.myriantics.klaxon.registry.minecraft.KlaxonRecipeTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {
    @WrapOperation(
            method = "interactBlockInternal",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;onUseWithItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ItemActionResult;")
    )
    private ItemActionResult klaxon$attemptManualItemApplication(BlockState instance, ItemStack stack, World world, PlayerEntity player, Hand hand, BlockHitResult hitResult, Operation<ItemActionResult> original) {
        if (ManualItemApplicationRecipeLogic.test(world, stack)) {
            ManualItemApplicationRecipeInput recipeInput = new ManualItemApplicationRecipeInput(stack, instance);
            if (world.getRecipeManager().getFirstMatch(KlaxonRecipeTypes.MANUAL_ITEM_APPLICATION, recipeInput, world).isPresent()) {
                return ItemActionResult.SUCCESS;
            }
        }

        return original.call(instance, stack, world, player, hand, hitResult);
    }
}
