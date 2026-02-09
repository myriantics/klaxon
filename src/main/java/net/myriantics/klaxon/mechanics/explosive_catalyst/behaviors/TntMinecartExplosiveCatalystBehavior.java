package net.myriantics.klaxon.mechanics.explosive_catalyst.behaviors;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ItemExplosiveCatalystBehavior;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystData;

public class TntMinecartExplosiveCatalystBehavior extends ItemExplosiveCatalystBehavior {
    public TntMinecartExplosiveCatalystBehavior(Identifier id) {
        super(id);
    }

    @Override
    public ExplosiveCatalystData transformExplosiveCatalystData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ExplosiveCatalystData data) {
        int redstoneStrength = world.getReceivedStrongRedstonePower(pos);

        return new ExplosiveCatalystData(this, data.explosionPower() + (double) redstoneStrength / 5, data.producesFire());
    }

    @Override
    public boolean isVariable() {
        return true;
    }
}
