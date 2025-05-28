package net.myriantics.klaxon.api.behavior.blast_processor_catalyst.behaviors;

import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.myriantics.klaxon.api.behavior.blast_processor_catalyst.ItemBlastProcessorCatalystBehavior;
import net.myriantics.klaxon.block.customblocks.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.recipe.item_explosion_power.ExplosiveCatalystRecipeInput;
import net.myriantics.klaxon.recipe.item_explosion_power.ItemExplosionPowerData;

public class GlowstoneBlastProcessorCatalystBehavior extends ItemBlastProcessorCatalystBehavior {
    public GlowstoneBlastProcessorCatalystBehavior(Identifier id) {
        super(id);
    }

    @Override
    public ItemExplosionPowerData getExplosionPowerData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ExplosiveCatalystRecipeInput recipeInventory) {
        ItemExplosionPowerData base = super.getExplosionPowerData(world, pos, blastProcessor, recipeInventory);

        // if respawn anchor doesn't work in dimension, explode
        if (!world.getDimension().respawnAnchorWorks()) {
            return new ItemExplosionPowerData(base.explosionPower(), base.producesFire());
        }

        // if respawn anchor does work, tough luck. fail.
        return new ItemExplosionPowerData(0, false);
    }

    @Override
    public BlastProcessorBehaviorItemExplosionPowerEmiDataCompound getEmiData() {
        return new BlastProcessorBehaviorItemExplosionPowerEmiDataCompound(
                0.0,
                5.0,
                Text.translatable("klaxon.emi.text.explosion_power_info.glowstone_behavior_info"),
                getId().getPath()
        );
    }
}
