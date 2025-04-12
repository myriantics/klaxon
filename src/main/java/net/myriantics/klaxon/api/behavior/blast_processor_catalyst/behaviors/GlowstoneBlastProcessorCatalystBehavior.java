package net.myriantics.klaxon.api.behavior.blast_processor_catalyst.behaviors;

import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.myriantics.klaxon.api.behavior.blast_processor_catalyst.ItemBlastProcessorCatalystBehavior;
import net.myriantics.klaxon.block.customblocks.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.recipe.item_explosion_power.ItemExplosionPowerData;

public class GlowstoneBlastProcessorCatalystBehavior extends ItemBlastProcessorCatalystBehavior {
    private final double explosionPower;

    public GlowstoneBlastProcessorCatalystBehavior(Identifier id, double explosionPower) {
        super(id);
        this.explosionPower = explosionPower;
    }

    @Override
    public ItemExplosionPowerData getExplosionPowerData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, RecipeInput recipeInventory) {
        // if respawn anchor doesn't work in dimension, explode
        if (!world.getDimension().respawnAnchorWorks()) {
            return new ItemExplosionPowerData(explosionPower, true);
        }

        return super.getExplosionPowerData(world, pos, blastProcessor, recipeInventory);
    }

    @Override
    public BlastProcessorBehaviorItemExplosionPowerEmiDataCompound getEmiData() {
        return new BlastProcessorBehaviorItemExplosionPowerEmiDataCompound(
                0.0,
                explosionPower,
                Text.translatable("klaxon.emi.text.explosion_power_info.glowstone_behavior_info"),
                getId().getPath()
        );
    }
}
