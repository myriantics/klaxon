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
import net.myriantics.klaxon.recipe.blast_processor_behavior.BlastProcessorBehaviorRecipeLogic;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystDefinitionRecipeInput;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystData;

public class FireworkStarExplosiveCatalystBehavior extends ItemExplosiveCatalystBehavior {
    public FireworkStarExplosiveCatalystBehavior(Identifier id) {
        super(id);
    }

    @Override
    public ExplosiveCatalystData getExplosionPowerData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ExplosiveCatalystDefinitionRecipeInput craftingInventory) {
        ItemStack stack = craftingInventory.catalystStack();
        ExplosiveCatalystData base = super.getExplosionPowerData(world, pos, blastProcessor, craftingInventory);

        if (stack.get(DataComponentTypes.FIREWORK_EXPLOSION) instanceof FireworkExplosionComponent component)  {
            boolean producesFire = base.producesFire();
            double explosionPower = base.explosionPower();

            // augment based on shape - only ones with explosive catalysts do something
            switch (component.shape()) {
                case LARGE_BALL ->  {
                    ExplosiveCatalystDefinitionRecipeInput fireChargeRecipeInput = new ExplosiveCatalystDefinitionRecipeInput(new ItemStack(Items.FIRE_CHARGE));
                    ExplosiveCatalystData fireChargeData = BlastProcessorBehaviorRecipeLogic.computeBehavior(world, fireChargeRecipeInput).getExplosionPowerData(world, pos, blastProcessor, fireChargeRecipeInput);
                    explosionPower += fireChargeData.explosionPower();
                    producesFire = producesFire || fireChargeData.producesFire();
                }
                case CREEPER -> {
                    ExplosiveCatalystDefinitionRecipeInput creeperHeadRecipeInput = new ExplosiveCatalystDefinitionRecipeInput(new ItemStack(Items.CREEPER_HEAD));
                    ExplosiveCatalystData creeperHeadData = BlastProcessorBehaviorRecipeLogic.computeBehavior(world, creeperHeadRecipeInput).getExplosionPowerData(world, pos, blastProcessor, creeperHeadRecipeInput);
                    explosionPower += creeperHeadData.explosionPower();
                    producesFire = producesFire || creeperHeadData.producesFire();
                }
            }

            // glowstone dust
            if (component.hasTwinkle()) {
                ExplosiveCatalystDefinitionRecipeInput glowstoneDustRecipeInput = new ExplosiveCatalystDefinitionRecipeInput(new ItemStack(Items.GLOWSTONE_DUST));
                ExplosiveCatalystData glowstoneDustData = BlastProcessorBehaviorRecipeLogic.computeBehavior(world, glowstoneDustRecipeInput).getExplosionPowerData(world, pos, blastProcessor, glowstoneDustRecipeInput);
                explosionPower += glowstoneDustData.explosionPower();
                producesFire = producesFire || glowstoneDustData.producesFire();
            }

            return new ExplosiveCatalystData(explosionPower, producesFire);
        }

        return base;
    }

    @Override
    public boolean isVariable() {
        return true;
    }
}
