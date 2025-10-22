package net.myriantics.klaxon.api.behavior.explosive_catalyst.behaviors;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.myriantics.klaxon.api.behavior.explosive_catalyst.ItemExplosiveCatalystBehavior;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystDefinitionRecipeInput;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystData;

public class TntMinecartExplosiveCatalystBehavior extends ItemExplosiveCatalystBehavior {
    public TntMinecartExplosiveCatalystBehavior(Identifier id) {
        super(id);
    }

    @Override
    public ExplosiveCatalystData getExplosionPowerData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ExplosiveCatalystDefinitionRecipeInput recipeInventory) {
        ExplosiveCatalystData base = super.getExplosionPowerData(world, pos, blastProcessor, recipeInventory);
        int redstoneStrength = world.getReceivedStrongRedstonePower(pos);

        return new ExplosiveCatalystData(base.explosionPower() + (double) redstoneStrength / 5, base.producesFire());
    }

    @Override
    public boolean isVariable() {
        return true;
    }
}
