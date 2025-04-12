package net.myriantics.klaxon.api.behavior.blast_processor_catalyst.behaviors;

import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.myriantics.klaxon.api.behavior.blast_processor_catalyst.ItemBlastProcessorCatalystBehavior;
import net.myriantics.klaxon.block.customblocks.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.recipe.item_explosion_power.ItemExplosionPowerData;

public class BedlikeExplodableBlastProcessorCatalystBehavior extends ItemBlastProcessorCatalystBehavior {
    public BedlikeExplodableBlastProcessorCatalystBehavior(Identifier id) {
        super(id);
    }

    @Override
    public ItemExplosionPowerData getExplosionPowerData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, RecipeInput recipeInventory) {

        // if the bed doesnt work in dimension, explode
        if (!world.getDimension().bedWorks()) {
            return new ItemExplosionPowerData(5, true);
        }

        return super.getExplosionPowerData(world, pos, blastProcessor, recipeInventory);
    }

    @Override
    public BlastProcessorBehaviorItemExplosionPowerEmiDataCompound getEmiData() {
        return new BlastProcessorBehaviorItemExplosionPowerEmiDataCompound(
                0.0,
                5.0,
                Text.translatable("klaxon.emi.text.explosion_power_info.bed_behavior_info"),
                getId().getPath()
        );
    }
}
