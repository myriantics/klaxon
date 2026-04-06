package net.myriantics.klaxon.mechanics.explosive_catalyst.behaviors;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.FireworkExplosion;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystContext;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystData;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystDefinitionRecipeLogic;

public class FireworkStarExplosiveCatalystBehavior extends DefaultExplosiveCatalystBehavior {

    @Override
    public ExplosiveCatalystData transformExplosiveCatalystData(ExplosiveCatalystContext context, ExplosiveCatalystData original) {
        if (context.components().get(DataComponents.FIREWORK_EXPLOSION) instanceof FireworkExplosion component)  {
            boolean producesFire = original.producesFire();
            double explosionPower = original.explosionPower();

            // augment based on shape - only ones with explosive catalysts do something
            switch (component.shape()) {
                case LARGE_BALL ->  {
                    ExplosiveCatalystData fireChargeData = ExplosiveCatalystDefinitionRecipeLogic.computeExplosiveCatalystData(context, Items.FIRE_CHARGE);
                    explosionPower += fireChargeData.explosionPower();
                    producesFire = producesFire || fireChargeData.producesFire();
                }
                case CREEPER -> {
                    ExplosiveCatalystData creeperHeadData = ExplosiveCatalystDefinitionRecipeLogic.computeExplosiveCatalystData(context, Items.CREEPER_HEAD);
                    explosionPower += creeperHeadData.explosionPower();
                    producesFire = producesFire || creeperHeadData.producesFire();
                }
            }

            // glowstone dust
            if (component.hasTwinkle()) {
                ExplosiveCatalystData glowstoneDustData = ExplosiveCatalystDefinitionRecipeLogic.computeExplosiveCatalystData(context, Items.GLOWSTONE_DUST);
                explosionPower += glowstoneDustData.explosionPower();
                producesFire = producesFire || glowstoneDustData.producesFire();
            }

            return new ExplosiveCatalystData(this, explosionPower, producesFire);
        }

        return original;
    }
}
