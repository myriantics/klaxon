package net.myriantics.klaxon.recipe.manual_item_application;

import net.minecraft.block.BlockState;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class ManualItemApplicationDispenserBehavior extends ItemDispenserBehavior {
    public void processManualItemApplicationRecipe(
            ServerWorld serverWorld,
            BlockPointer blockPointer,
            BlockPos targetPos,
            Direction dispenserFacing,
            ManualItemApplicationRecipeInput recipeInput,
            BlockState newState
    ) {
        ItemStack usedStack = recipeInput.usedStack();

        ManualItemApplicationRecipeLogic.affectWorld(serverWorld, targetPos, newState, dispenserFacing.getOpposite(), null, recipeInput);

        // decrement stack and set the block state
        decrementStackWithRemainder(blockPointer, usedStack, usedStack.getRecipeRemainder());
    }
}
