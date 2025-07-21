package net.myriantics.klaxon.mixin.manual_item_application;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.myriantics.klaxon.recipe.manual_item_application.ManualItemApplicationRecipeInput;
import net.myriantics.klaxon.recipe.manual_item_application.ManualItemApplicationRecipeLogic;
import net.myriantics.klaxon.registry.minecraft.KlaxonRecipeTypes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class ServerPlayerInteractionManagerMixin {
    @Shadow protected ServerWorld world;

    @Shadow @Final protected ServerPlayerEntity player;

    @WrapOperation(
            method = "interactBlock",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;onUseWithItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ItemActionResult;")
    )
    private ItemActionResult klaxon$attemptManualItemApplication(BlockState instance, ItemStack stack, World world, PlayerEntity player, Hand hand, BlockHitResult hitResult, Operation<ItemActionResult> original) {
        if (ManualItemApplicationRecipeLogic.test(world, stack)) {
            ManualItemApplicationRecipeInput recipeInput = new ManualItemApplicationRecipeInput(stack, instance);
            Optional<BlockState> newState = ManualItemApplicationRecipeLogic.getResultState(world, recipeInput);

            if (newState.isPresent()) {
                BlockPos targetPos = hitResult.getBlockPos();
                ManualItemApplicationRecipeLogic.affectWorld(this.world, targetPos, newState.get(), recipeInput);

                // remainder fuckery
                if (!player.isCreative()) {
                    ItemStack remainder = stack.getRecipeRemainder();
                    stack.decrement(1);
                    if (!player.getInventory().insertStack(remainder)) {
                        player.dropItem(remainder, false);
                    }
                }

                // sculk sensors go brrrt
                this.world.emitGameEvent(GameEvent.BLOCK_CHANGE, targetPos, GameEvent.Emitter.of(player));

                return ItemActionResult.SUCCESS;
            }
        }

        return original.call(instance, stack, world, player, hand, hitResult);
    }
}
