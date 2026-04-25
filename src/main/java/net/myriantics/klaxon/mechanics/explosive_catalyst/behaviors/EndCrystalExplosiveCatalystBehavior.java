package net.myriantics.klaxon.mechanics.explosive_catalyst.behaviors;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystContext;
import net.myriantics.klaxon.recipe.explosive_catalyst.ExplosiveCatalystData;

public class EndCrystalExplosiveCatalystBehavior extends DefaultExplosiveCatalystBehavior {

    @Override
    public ExplosiveCatalystData transformExplosiveCatalystData(ExplosiveCatalystContext context, ExplosiveCatalystData original) {
        if (original.producesFire()) {
            return original;
        } else {
            return switch (context) {
                case ExplosiveCatalystContext.Block block -> new ExplosiveCatalystData(original.behavior(), original.explosionPower(), this.isStateValid(context.level(), block.getPos().below()));
                case ExplosiveCatalystContext.Entity entity -> original;
                case ExplosiveCatalystContext.Item item -> original;
            };
        }
    }

    private boolean isStateValid(Level world, BlockPos pos) {
        return world.getBlockState(pos).is(Blocks.BEDROCK);
    }
}
