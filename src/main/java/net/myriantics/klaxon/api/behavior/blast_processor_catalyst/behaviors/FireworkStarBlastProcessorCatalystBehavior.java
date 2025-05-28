package net.myriantics.klaxon.api.behavior.blast_processor_catalyst.behaviors;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.myriantics.klaxon.api.behavior.blast_processor_catalyst.ItemBlastProcessorCatalystBehavior;
import net.myriantics.klaxon.block.customblocks.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.recipe.item_explosion_power.ExplosiveCatalystRecipeInput;
import net.myriantics.klaxon.recipe.item_explosion_power.ItemExplosionPowerData;

import java.util.List;

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
                    ItemExplosionPowerData fireChargeData = super.getExplosionPowerData(world, pos, blastProcessor, new ExplosiveCatalystRecipeInput(new ItemStack(Items.FIRE_CHARGE)));
                    explosionPower += fireChargeData.explosionPower();
                    producesFire = producesFire || fireChargeData.producesFire();
                }
                case CREEPER -> {
                    ItemExplosionPowerData creeperHeadData = super.getExplosionPowerData(world, pos, blastProcessor, new ExplosiveCatalystRecipeInput(new ItemStack(Items.CREEPER_HEAD)));
                    explosionPower += creeperHeadData.explosionPower();
                    producesFire = producesFire || creeperHeadData.producesFire();
                }
            }

            // glowstone dust
            if (component.hasTwinkle()) {
                ItemExplosionPowerData glowstoneDustData = super.getExplosionPowerData(world, pos, blastProcessor, new ExplosiveCatalystRecipeInput(new ItemStack(Items.GLOWSTONE_DUST)));
                explosionPower += glowstoneDustData.explosionPower();
                producesFire = producesFire || glowstoneDustData.producesFire();
            }

            return new ItemExplosionPowerData(explosionPower, producesFire);
        }

        return base;
    }

    @Override
    public BlastProcessorBehaviorItemExplosionPowerEmiDataCompound getEmiData() {
        return new BlastProcessorBehaviorItemExplosionPowerEmiDataCompound(
                0.3,
                1.2,
                Text.empty(),
                getId().getPath()
        );
    }
}
