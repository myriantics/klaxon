package net.myriantics.klaxon.mixin.minecraft.world_item_application;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.myriantics.klaxon.recipe.world_item_application.WorldItemApplicationRecipeInput;
import net.myriantics.klaxon.recipe.world_item_application.WorldItemApplicationRecipeLogic;
import net.myriantics.klaxon.registry.behavior.KlaxonDispenserBehaviors;
import net.myriantics.klaxon.registry.misc.KlaxonGameRules;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(DispenserBlock.class)
public abstract class DispenserBlockMixin {
    @Shadow @Final public static DirectionProperty FACING;

    @ModifyExpressionValue(
            method = "dispenseFrom",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/DispenserBlock;getDispenseMethod(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/core/dispenser/DispenseItemBehavior;")
    )
    private DispenseItemBehavior klaxon$attemptManualItemApplicationRecipe(
            DispenseItemBehavior original,
            @Local(argsOnly = true) ServerLevel serverWorld,
            @Local(argsOnly = true) BlockState dispenserState,
            @Local(argsOnly = true) BlockPos dispenserPos,
            @Local BlockSource blockPointer,
            @Local ItemStack selectedStack
        )
    {
        // return the original behavior if functionality is disabled
        if (!serverWorld.getGameRules().getBoolean(KlaxonGameRules.DISPENSERS_PERFORM_ITEM_INTERACTION_RECIPES)) {
            return original;
        }

        // if functionality is enabled, run logic & affect world.
        if (WorldItemApplicationRecipeLogic.test(serverWorld, selectedStack)) {
            BlockPos targetPos = dispenserPos.relative(dispenserState.getValue(FACING));
            BlockState targetState = serverWorld.getBlockState(targetPos);
            WorldItemApplicationRecipeInput recipeInput = new WorldItemApplicationRecipeInput(selectedStack, targetState);
            Optional<BlockState> newState = WorldItemApplicationRecipeLogic.getResultState(serverWorld, recipeInput);

            if (newState.isPresent()) {
                KlaxonDispenserBehaviors.MANUAL_ITEM_APPLICATION_BEHAVIOR.processManualItemApplicationRecipe(
                        serverWorld,
                        blockPointer,
                        targetPos,
                        dispenserState.getValue(FACING),
                        recipeInput,
                        newState.get()
                );

                // sculk sensors do go brrr
                serverWorld.gameEvent(GameEvent.BLOCK_CHANGE, targetPos, GameEvent.Context.of(targetState));

                // we've done our own stuff, don't perform any more operations
                return DispenseItemBehavior.NOOP;
            }
        }

        return original;
    }
}
