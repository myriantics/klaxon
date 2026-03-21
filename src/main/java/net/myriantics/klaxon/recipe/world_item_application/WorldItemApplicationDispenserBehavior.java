package net.myriantics.klaxon.recipe.world_item_application;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class WorldItemApplicationDispenserBehavior extends DefaultDispenseItemBehavior {
    public void processManualItemApplicationRecipe(
            ServerLevel serverWorld,
            BlockSource blockPointer,
            BlockPos targetPos,
            Direction dispenserFacing,
            WorldItemApplicationRecipeInput recipeInput,
            BlockState newState
    ) {
        ItemStack usedStack = recipeInput.usedStack();

        WorldItemApplicationRecipeLogic.affectWorld(serverWorld, targetPos, newState, dispenserFacing.getOpposite(), null, recipeInput);

        // decrement stack and set the block state
        consumeWithRemainder(blockPointer, usedStack, usedStack.getRecipeRemainder());
    }
}
