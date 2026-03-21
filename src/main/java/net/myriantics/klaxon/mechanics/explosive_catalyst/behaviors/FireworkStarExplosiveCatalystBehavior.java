package net.myriantics.klaxon.mechanics.explosive_catalyst.behaviors;

import net.myriantics.klaxon.mechanics.explosive_catalyst.ItemExplosiveCatalystBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.level.Level;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystData;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystDefinitionRecipeLogic;

public class FireworkStarExplosiveCatalystBehavior extends ItemExplosiveCatalystBehavior {
    public FireworkStarExplosiveCatalystBehavior(ResourceLocation id) {
        super(id);
    }

    @Override
    public ExplosiveCatalystData transformExplosiveCatalystData(Level world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ExplosiveCatalystData data) {
        ItemStack stack = blastProcessor.getItem(DeepslateBlastProcessorBlockEntity.CATALYST_INDEX);

        if (stack.get(DataComponents.FIREWORK_EXPLOSION) instanceof FireworkExplosion component)  {
            boolean producesFire = data.producesFire();
            double explosionPower = data.explosionPower();

            // augment based on shape - only ones with explosive catalysts do something
            switch (component.shape()) {
                case LARGE_BALL ->  {
                    ExplosiveCatalystData fireChargeData = ExplosiveCatalystDefinitionRecipeLogic.computeExplosiveCatalystData(world, pos, blastProcessor, Items.FIRE_CHARGE);
                    explosionPower += fireChargeData.explosionPower();
                    producesFire = producesFire || fireChargeData.producesFire();
                }
                case CREEPER -> {
                    ExplosiveCatalystData creeperHeadData = ExplosiveCatalystDefinitionRecipeLogic.computeExplosiveCatalystData(world, pos, blastProcessor, Items.CREEPER_HEAD);
                    explosionPower += creeperHeadData.explosionPower();
                    producesFire = producesFire || creeperHeadData.producesFire();
                }
            }

            // glowstone dust
            if (component.hasTwinkle()) {
                ExplosiveCatalystData glowstoneDustData = ExplosiveCatalystDefinitionRecipeLogic.computeExplosiveCatalystData(world, pos, blastProcessor, Items.GLOWSTONE_DUST);
                explosionPower += glowstoneDustData.explosionPower();
                producesFire = producesFire || glowstoneDustData.producesFire();
            }

            return new ExplosiveCatalystData(this, explosionPower, producesFire);
        }

        return data;
    }

    @Override
    public boolean isVariable() {
        return true;
    }
}
