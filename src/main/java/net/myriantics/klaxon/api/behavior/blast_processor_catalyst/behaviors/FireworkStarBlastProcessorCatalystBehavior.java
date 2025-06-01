package net.myriantics.klaxon.api.behavior.blast_processor_catalyst.behaviors;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.myriantics.klaxon.api.behavior.blast_processor_catalyst.ItemBlastProcessorCatalystBehavior;
import net.myriantics.klaxon.block.customblocks.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.recipe.blast_processor_behavior.BlastProcessorBehaviorRecipeLogic;
import net.myriantics.klaxon.recipe.item_explosion_power.ExplosiveCatalystRecipeInput;
import net.myriantics.klaxon.recipe.item_explosion_power.ItemExplosionPowerData;

public class FireworkStarBlastProcessorCatalystBehavior extends ItemBlastProcessorCatalystBehavior {
    public FireworkStarBlastProcessorCatalystBehavior(Identifier id) {
        super(id);
    }

    @Override
    public ItemExplosionPowerData getExplosionPowerData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ExplosiveCatalystRecipeInput craftingInventory) {
        ItemStack stack = craftingInventory.catalystStack();
        ItemExplosionPowerData base = super.getExplosionPowerData(world, pos, blastProcessor, craftingInventory);

        if (stack.get(DataComponentTypes.FIREWORK_EXPLOSION) instanceof FireworkExplosionComponent component)  {
            boolean producesFire = base.producesFire();
            double explosionPower = base.explosionPower();

            // augment based on shape - only ones with explosive catalysts do something
            switch (component.shape()) {
                case LARGE_BALL ->  {
                    ExplosiveCatalystRecipeInput fireChargeRecipeInput = new ExplosiveCatalystRecipeInput(new ItemStack(Items.FIRE_CHARGE));
                    ItemExplosionPowerData fireChargeData = BlastProcessorBehaviorRecipeLogic.computeBehavior(world, fireChargeRecipeInput).getExplosionPowerData(world, pos, blastProcessor, fireChargeRecipeInput);
                    explosionPower += fireChargeData.explosionPower();
                    producesFire = producesFire || fireChargeData.producesFire();
                }
                case CREEPER -> {
                    ExplosiveCatalystRecipeInput creeperHeadRecipeInput = new ExplosiveCatalystRecipeInput(new ItemStack(Items.CREEPER_HEAD));
                    ItemExplosionPowerData creeperHeadData = BlastProcessorBehaviorRecipeLogic.computeBehavior(world, creeperHeadRecipeInput).getExplosionPowerData(world, pos, blastProcessor, creeperHeadRecipeInput);
                    explosionPower += creeperHeadData.explosionPower();
                    producesFire = producesFire || creeperHeadData.producesFire();
                }
            }

            // glowstone dust
            if (component.hasTwinkle()) {
                ExplosiveCatalystRecipeInput glowstoneDustRecipeInput = new ExplosiveCatalystRecipeInput(new ItemStack(Items.GLOWSTONE_DUST));
                ItemExplosionPowerData glowstoneDustData = BlastProcessorBehaviorRecipeLogic.computeBehavior(world, glowstoneDustRecipeInput).getExplosionPowerData(world, pos, blastProcessor, glowstoneDustRecipeInput);
                explosionPower += glowstoneDustData.explosionPower();
                producesFire = producesFire || glowstoneDustData.producesFire();
            }

            return new ItemExplosionPowerData(explosionPower, producesFire);
        }

        return base;
    }

    @Override
    public boolean isVariable() {
        return true;
    }
}
