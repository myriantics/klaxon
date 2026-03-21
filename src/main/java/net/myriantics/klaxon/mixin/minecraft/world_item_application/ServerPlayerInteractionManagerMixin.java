package net.myriantics.klaxon.mixin.minecraft.world_item_application;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.myriantics.klaxon.recipe.world_item_application.WorldItemApplicationRecipeInput;
import net.myriantics.klaxon.recipe.world_item_application.WorldItemApplicationRecipeLogic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerInteractionManagerMixin {
    @Shadow protected ServerLevel level;

    @Shadow @Final protected ServerPlayer player;

    @WrapOperation(
            method = "useItemOn",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;useItemOn(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/ItemInteractionResult;")
    )
    private ItemInteractionResult klaxon$attemptManualItemApplication(BlockState instance, ItemStack stack, Level world, Player player, InteractionHand hand, BlockHitResult hitResult, Operation<ItemInteractionResult> original) {
        if (WorldItemApplicationRecipeLogic.test(world, stack)) {
            WorldItemApplicationRecipeInput recipeInput = new WorldItemApplicationRecipeInput(stack, instance);
            Optional<BlockState> newState = WorldItemApplicationRecipeLogic.getResultState(world, recipeInput);

            if (newState.isPresent()) {
                BlockPos targetPos = hitResult.getBlockPos();
                WorldItemApplicationRecipeLogic.affectWorld(this.level, targetPos, newState.get(), hitResult.getDirection(), player, recipeInput);

                // remainder fuckery
                if (!player.isCreative()) {
                    ItemStack remainder = stack.getRecipeRemainder();
                    stack.shrink(1);
                    if (!player.getInventory().add(remainder)) {
                        player.drop(remainder, false);
                    }
                }

                // sculk sensors go brrrt
                this.level.gameEvent(GameEvent.BLOCK_CHANGE, targetPos, GameEvent.Context.of(player));

                return ItemInteractionResult.SUCCESS;
            }
        }

        return original.call(instance, stack, world, player, hand, hitResult);
    }
}
