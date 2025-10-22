package net.myriantics.klaxon.api.behavior.explosive_catalyst;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeData;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeInput;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystData;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystDefinitionRecipeInput;
import net.myriantics.klaxon.registry.KlaxonRegistries;

public interface ExplosiveCatalystBehavior {

    void onExplosion(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ExplosiveCatalystData powerData, boolean modifyWorld);

    void ejectItems(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, BlastProcessingRecipeData recipeData, ExplosiveCatalystData powerData);

    ExplosiveCatalystData getExplosionPowerData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ExplosiveCatalystDefinitionRecipeInput recipeInventory);

    BlastProcessingRecipeData getBlastProcessingPreviewData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, BlastProcessingRecipeInput recipeInventory);

    BlastProcessingRecipeData getBlastProcessingRecipeData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, BlastProcessingRecipeInput recipeInventory);

    boolean shouldRunDispenserEffects(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessorBlock, ExplosiveCatalystDefinitionRecipeInput recipeInventory);

    boolean isVariable();

    default boolean isIn(TagKey<ExplosiveCatalystBehavior> tagKey) {
        return getRegistryEntry().isIn(tagKey);
    }

    default RegistryEntry<ExplosiveCatalystBehavior> getRegistryEntry() {
        return KlaxonRegistries.BLAST_PROCESSOR_BEHAVIORS.getEntry(this);
    }

    // long ass name go brrr
    record BlastProcessorBehaviorItemExplosionPowerEmiDataCompound(double explosionPowerMin, double explosionPowerMax, net.minecraft.text.Text infoText, String path) {}
}