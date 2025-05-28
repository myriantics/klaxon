package net.myriantics.klaxon.api.behavior.blast_processor_catalyst.behaviors;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.myriantics.klaxon.api.behavior.blast_processor_catalyst.ItemBlastProcessorCatalystBehavior;
import net.myriantics.klaxon.block.customblocks.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.recipe.item_explosion_power.ExplosiveCatalystRecipeInput;
import net.myriantics.klaxon.recipe.item_explosion_power.ItemExplosionPowerData;

public class TntMinecartBlastProcessorCatalystBehavior extends ItemBlastProcessorCatalystBehavior {
    public TntMinecartBlastProcessorCatalystBehavior(Identifier id) {
        super(id);
    }

    @Override
    public ItemExplosionPowerData getExplosionPowerData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ExplosiveCatalystRecipeInput recipeInventory) {
        ItemExplosionPowerData base = super.getExplosionPowerData(world, pos, blastProcessor, recipeInventory);
        int redstoneStrength = world.getReceivedStrongRedstonePower(pos);

        return new ItemExplosionPowerData(base.explosionPower() + (double) redstoneStrength / 5, base.producesFire());
    }

    @Override
    public boolean isVariable() {
        return true;
    }
}
