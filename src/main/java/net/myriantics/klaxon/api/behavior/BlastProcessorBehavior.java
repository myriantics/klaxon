package net.myriantics.klaxon.api.behavior;

import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.myriantics.klaxon.block.customblocks.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeData;
import net.myriantics.klaxon.recipe.item_explosion_power.ItemExplosionPowerData;

public interface BlastProcessorBehavior {

    void onExplosion(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ItemExplosionPowerData powerData);

    void ejectItems(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, BlastProcessingRecipeData recipeData, ItemExplosionPowerData powerData);

    ItemExplosionPowerData getExplosionPowerData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, RecipeInput recipeInventory);

    BlastProcessingRecipeData getBlastProcessingRecipeData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, RecipeInput recipeInventory, ItemExplosionPowerData powerData);

    BlastProcessorBehaviorItemExplosionPowerEmiDataCompound getEmiData();

    boolean shouldRunDispenserEffects(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessorBlock, RecipeInput recipeInventory);


    // long ass name go brrr
    record BlastProcessorBehaviorItemExplosionPowerEmiDataCompound(double explosionPowerMin, double explosionPowerMax, net.minecraft.text.Text infoText, String path) {}
}