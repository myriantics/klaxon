package net.myriantics.klaxon.api.behavior.blast_processor_catalyst;

import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.myriantics.klaxon.block.customblocks.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeData;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeInput;
import net.myriantics.klaxon.recipe.item_explosion_power.ItemExplosionPowerData;
import net.myriantics.klaxon.recipe.item_explosion_power.ExplosiveCatalystRecipeInput;
import net.myriantics.klaxon.registry.KlaxonRegistries;

public interface BlastProcessorCatalystBehavior {

    void onExplosion(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ItemExplosionPowerData powerData, boolean modifyWorld);

    void ejectItems(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, BlastProcessingRecipeData recipeData, ItemExplosionPowerData powerData);

    ItemExplosionPowerData getExplosionPowerData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ExplosiveCatalystRecipeInput recipeInventory);

    BlastProcessingRecipeData getBlastProcessingRecipeData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, BlastProcessingRecipeInput recipeInventory);

    BlastProcessorBehaviorItemExplosionPowerEmiDataCompound getEmiData();

    boolean shouldRunDispenserEffects(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessorBlock, ExplosiveCatalystRecipeInput recipeInventory);

    default boolean isIn(TagKey<BlastProcessorCatalystBehavior> tagKey) {
        return getRegistryEntry().isIn(tagKey);
    }

    default RegistryEntry<BlastProcessorCatalystBehavior> getRegistryEntry() {
        return KlaxonRegistries.BLAST_PROCESSOR_BEHAVIORS.getEntry(this);
    }

    // long ass name go brrr
    record BlastProcessorBehaviorItemExplosionPowerEmiDataCompound(double explosionPowerMin, double explosionPowerMax, net.minecraft.text.Text infoText, String path) {}
}