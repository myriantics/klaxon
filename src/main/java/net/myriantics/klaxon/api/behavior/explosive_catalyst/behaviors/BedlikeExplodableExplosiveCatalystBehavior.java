package net.myriantics.klaxon.api.behavior.explosive_catalyst.behaviors;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.myriantics.klaxon.api.behavior.explosive_catalyst.ItemExplosiveCatalystBehavior;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystDefinitionRecipeInput;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystData;

public class BedlikeExplodableExplosiveCatalystBehavior extends ItemExplosiveCatalystBehavior {
    public BedlikeExplodableExplosiveCatalystBehavior(Identifier id) {
        super(id);
    }

    @Override
    public ExplosiveCatalystData getExplosionPowerData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ExplosiveCatalystDefinitionRecipeInput recipeInventory) {
        ExplosiveCatalystData base = super.getExplosionPowerData(world, pos, blastProcessor, recipeInventory);

        // if the bed doesnt work in dimension, explode
        if (!world.getDimension().bedWorks()) {
            return base;
        }

        return new ExplosiveCatalystData(0, false);
    }

    @Override
    public boolean isVariable() {
        return false;
    }
}
