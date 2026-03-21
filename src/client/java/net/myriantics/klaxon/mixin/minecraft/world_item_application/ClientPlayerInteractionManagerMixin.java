package net.myriantics.klaxon.mixin.minecraft.world_item_application;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.myriantics.klaxon.recipe.world_item_application.WorldItemApplicationRecipeInput;
import net.myriantics.klaxon.recipe.world_item_application.WorldItemApplicationRecipeLogic;
import net.myriantics.klaxon.registry.misc.KlaxonRecipeTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MultiPlayerGameMode.class)
public abstract class ClientPlayerInteractionManagerMixin {
    @WrapOperation(
            method = "performUseItemOn",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;useItemOn(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/ItemInteractionResult;")
    )
    private ItemInteractionResult klaxon$attemptManualItemApplication(BlockState instance, ItemStack stack, Level world, Player player, InteractionHand hand, BlockHitResult hitResult, Operation<ItemInteractionResult> original) {
        if (WorldItemApplicationRecipeLogic.test(world, stack)) {
            WorldItemApplicationRecipeInput recipeInput = new WorldItemApplicationRecipeInput(stack, instance);
            if (world.getRecipeManager().getRecipeFor(KlaxonRecipeTypes.WORLD_ITEM_APPLICATION, recipeInput, world).isPresent()) {
                return ItemInteractionResult.SUCCESS;
            }
        }

        return original.call(instance, stack, world, player, hand, hitResult);
    }
}
