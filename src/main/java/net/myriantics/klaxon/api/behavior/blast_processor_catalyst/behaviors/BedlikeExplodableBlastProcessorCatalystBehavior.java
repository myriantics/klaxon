package net.myriantics.klaxon.api.behavior.blast_processor_catalyst.behaviors;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.myriantics.klaxon.api.behavior.blast_processor_catalyst.ItemBlastProcessorCatalystBehavior;
import net.myriantics.klaxon.block.customblocks.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.recipe.item_explosion_power.ExplosiveCatalystRecipeInput;
import net.myriantics.klaxon.recipe.item_explosion_power.ItemExplosionPowerData;

public class BedlikeExplodableBlastProcessorCatalystBehavior extends ItemBlastProcessorCatalystBehavior {
    public BedlikeExplodableBlastProcessorCatalystBehavior(Identifier id) {
        super(id);
    }

    @Override
    public ItemExplosionPowerData getExplosionPowerData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ExplosiveCatalystRecipeInput recipeInventory) {
        ItemExplosionPowerData base = super.getExplosionPowerData(world, pos, blastProcessor, recipeInventory);

        // if the bed doesnt work in dimension, explode
        if (!world.getDimension().bedWorks()) {
            return base;
        }

        return new ItemExplosionPowerData(0, false);
    }

    @Override
    public boolean isVariable() {
        return false;
    }
}
