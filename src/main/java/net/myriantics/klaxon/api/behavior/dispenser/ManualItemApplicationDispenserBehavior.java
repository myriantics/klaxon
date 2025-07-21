package net.myriantics.klaxon.api.behavior.dispenser;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.myriantics.klaxon.networking.KlaxonServerPlayNetworkHandler;
import net.myriantics.klaxon.recipe.manual_item_application.ManualItemApplicationRecipeInput;
import net.myriantics.klaxon.recipe.manual_item_application.ManualItemApplicationRecipeLogic;
import net.myriantics.klaxon.registry.custom.KlaxonWorldEvents;

public class ManualItemApplicationDispenserBehavior extends ItemDispenserBehavior {
    public void processManualItemApplicationRecipe(
            ServerWorld serverWorld,
            BlockPointer blockPointer,
            BlockPos targetPos,
            ManualItemApplicationRecipeInput recipeInput,
            BlockState newState
    ) {
        ItemStack usedStack = recipeInput.usedStack();

        ManualItemApplicationRecipeLogic.affectWorld(serverWorld, targetPos, newState, recipeInput);

        // decrement stack and set the block state
        decrementStackWithRemainder(blockPointer, usedStack, usedStack.getRecipeRemainder());
    }
}
