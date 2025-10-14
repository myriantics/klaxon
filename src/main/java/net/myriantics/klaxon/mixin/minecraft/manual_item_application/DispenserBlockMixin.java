package net.myriantics.klaxon.mixin.minecraft.manual_item_application;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.GameEvent;
import net.myriantics.klaxon.recipe.manual_item_application.ManualItemApplicationRecipeInput;
import net.myriantics.klaxon.recipe.manual_item_application.ManualItemApplicationRecipeLogic;
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
            method = "dispense",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/DispenserBlock;getBehaviorForItem(Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/block/dispenser/DispenserBehavior;")
    )
    private DispenserBehavior klaxon$attemptManualItemApplicationRecipe(
            DispenserBehavior original,
            @Local(argsOnly = true) ServerWorld serverWorld,
            @Local(argsOnly = true) BlockState dispenserState,
            @Local(argsOnly = true) BlockPos dispenserPos,
            @Local BlockPointer blockPointer,
            @Local ItemStack selectedStack
        )
    {
        // return the original behavior if functionality is disabled
        if (!serverWorld.getGameRules().getBoolean(KlaxonGameRules.DISPENSERS_PERFORM_ITEM_INTERACTION_RECIPES)) {
            return original;
        }

        // if functionality is enabled, run logic & affect world.
        if (ManualItemApplicationRecipeLogic.test(serverWorld, selectedStack)) {
            BlockPos targetPos = dispenserPos.offset(dispenserState.get(FACING));
            BlockState targetState = serverWorld.getBlockState(targetPos);
            ManualItemApplicationRecipeInput recipeInput = new ManualItemApplicationRecipeInput(selectedStack, targetState);
            Optional<BlockState> newState = ManualItemApplicationRecipeLogic.getResultState(serverWorld, recipeInput);

            if (newState.isPresent()) {
                KlaxonDispenserBehaviors.MANUAL_ITEM_APPLICATION_BEHAVIOR.processManualItemApplicationRecipe(
                        serverWorld,
                        blockPointer,
                        targetPos,
                        dispenserState.get(FACING),
                        recipeInput,
                        newState.get()
                );

                // sculk sensors do go brrr
                serverWorld.emitGameEvent(GameEvent.BLOCK_CHANGE, targetPos, GameEvent.Emitter.of(targetState));

                // we've done our own stuff, don't perform any more operations
                return DispenserBehavior.NOOP;
            }
        }

        return original;
    }
}
