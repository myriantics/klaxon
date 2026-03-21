package net.myriantics.klaxon.mechanics.explosive_catalyst.behaviors;

import net.myriantics.klaxon.mechanics.explosive_catalyst.ItemExplosiveCatalystBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystData;

public class TntMinecartExplosiveCatalystBehavior extends ItemExplosiveCatalystBehavior {
    public TntMinecartExplosiveCatalystBehavior(ResourceLocation id) {
        super(id);
    }

    @Override
    public ExplosiveCatalystData transformExplosiveCatalystData(Level world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ExplosiveCatalystData data) {
        int redstoneStrength = world.getDirectSignalTo(pos);

        return new ExplosiveCatalystData(this, data.explosionPower() + (double) redstoneStrength / 5, data.producesFire());
    }

    @Override
    public boolean isVariable() {
        return true;
    }
}
