package net.myriantics.klaxon.api.behavior.explosive_catalyst.behaviors;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.myriantics.klaxon.api.behavior.explosive_catalyst.ItemExplosiveCatalystBehavior;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystData;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystDefinitionRecipeLogic;

public class FireworkStarExplosiveCatalystBehavior extends ItemExplosiveCatalystBehavior {
    public FireworkStarExplosiveCatalystBehavior(Identifier id) {
        super(id);
    }

    @Override
    public ExplosiveCatalystData transformExplosiveCatalystData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ExplosiveCatalystData data) {
        ItemStack stack = blastProcessor.getStack(DeepslateBlastProcessorBlockEntity.CATALYST_INDEX);

        if (stack.get(DataComponentTypes.FIREWORK_EXPLOSION) instanceof FireworkExplosionComponent component)  {
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
