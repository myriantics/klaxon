package net.myriantics.klaxon.api.behavior.blast_processor_catalyst.behaviors;

import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;
import net.myriantics.klaxon.api.behavior.blast_processor_catalyst.ItemBlastProcessorCatalystBehavior;
import net.myriantics.klaxon.block.customblocks.machines.blast_processor.deepslate.DeepslateBlastProcessorBlock;
import net.myriantics.klaxon.block.customblocks.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.mixin.blast_processor_behaviors.FireworkRocketEntityInvoker;
import net.myriantics.klaxon.recipe.blast_processor_behavior.BlastProcessorBehaviorRecipeLogic;
import net.myriantics.klaxon.recipe.item_explosion_power.ExplosiveCatalystRecipeInput;
import net.myriantics.klaxon.recipe.item_explosion_power.ItemExplosionPowerData;

public class FireworkRocketBlastProcessorCatalystBehavior extends ItemBlastProcessorCatalystBehavior {

    public FireworkRocketBlastProcessorCatalystBehavior(Identifier id) {
        super(id);
    }

    @Override
    public ItemExplosionPowerData getExplosionPowerData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ExplosiveCatalystRecipeInput craftingInventory) {
        ItemExplosionPowerData base = super.getExplosionPowerData(world, pos, blastProcessor, craftingInventory);
        ItemStack stack = craftingInventory.catalystStack();

        if (stack.get(DataComponentTypes.FIREWORKS) instanceof FireworksComponent component) {
            boolean producesFire = base.producesFire();
            double explosionPower = base.explosionPower();

            // compute explosion power data from gunpowder
            ExplosiveCatalystRecipeInput gunpowderRecipeInput = new ExplosiveCatalystRecipeInput(new ItemStack(Items.GUNPOWDER));
            ItemExplosionPowerData gunpowderData = BlastProcessorBehaviorRecipeLogic.computeBehavior(world, gunpowderRecipeInput).getExplosionPowerData(world, pos, blastProcessor, gunpowderRecipeInput);

            // add explosion power for the flight duration
            producesFire = producesFire || gunpowderData.producesFire();
            explosionPower += component.flightDuration() * gunpowderData.explosionPower();

            for (FireworkExplosionComponent explosionComponent : component.explosions()) {
                // prepare firework star stack with selected component
                ItemStack starStack = new ItemStack(Items.FIREWORK_STAR);
                starStack.applyComponentsFrom(ComponentMap.builder().add(DataComponentTypes.FIREWORK_EXPLOSION, explosionComponent).build());

                // get explosion power data from star stack
                ExplosiveCatalystRecipeInput fireworkStarRecipeInput = new ExplosiveCatalystRecipeInput(starStack);
                ItemExplosionPowerData fireworkStarData = BlastProcessorBehaviorRecipeLogic.computeBehavior(world, fireworkStarRecipeInput).getExplosionPowerData(world, pos, blastProcessor, fireworkStarRecipeInput);

                // append values to stats
                producesFire = producesFire || fireworkStarData.producesFire();
                explosionPower += fireworkStarData.explosionPower();
            }

            // each recipe produces 3 rockets, so our actual value is 1/3 of what was calculated - then round to the nearest tenth.
            explosionPower /= 3;

            return new ItemExplosionPowerData(explosionPower, producesFire);
        }

        return base;
    }

    @Override
    public void onExplosion(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ItemExplosionPowerData powerData, boolean shouldModifyWorld) {
        ItemStack stack = blastProcessor.getStack(DeepslateBlastProcessorBlockEntity.CATALYST_INDEX);

        if (stack.get(DataComponentTypes.FIREWORKS) instanceof FireworksComponent) {
            Position outputPos = blastProcessor.getExplosionOutputLocation(world.getBlockState(pos).get(DeepslateBlastProcessorBlock.HORIZONTAL_FACING));
            FireworkRocketEntity fireworkRocket = new FireworkRocketEntity(world, outputPos.getX(), outputPos.getY(), outputPos.getZ(), stack);

            // explode using firework rocket entity code - summons dummy firework and detonates it
            world.spawnEntity(fireworkRocket);

            // clear the stack from inventory
            blastProcessor.removeStack(DeepslateBlastProcessorBlockEntity.CATALYST_INDEX);

            // TIL you can't have an invoker method be the same name as the original method. The more you know!
            ((FireworkRocketEntityInvoker) fireworkRocket).invokeExplodeAndRemove();
        } else {
            super.onExplosion(world, pos, blastProcessor, powerData, shouldModifyWorld);
        }
    }

    @Override
    public boolean shouldRunDispenserEffects(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessorBlock, ExplosiveCatalystRecipeInput recipeInventory) {
        return false;
    }

    @Override
    public boolean isVariable() {
        return true;
    }
}
