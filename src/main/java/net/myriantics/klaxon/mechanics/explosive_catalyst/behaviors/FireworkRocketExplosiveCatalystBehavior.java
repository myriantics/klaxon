package net.myriantics.klaxon.mechanics.explosive_catalyst.behaviors;

import net.myriantics.klaxon.mechanics.explosive_catalyst.ItemExplosiveCatalystBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.level.Level;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlock;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.mixin.minecraft.blast_processor_behaviors.FireworkRocketEntityInvoker;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystDefinitionRecipeInput;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystData;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystDefinitionRecipeLogic;

public class FireworkRocketExplosiveCatalystBehavior extends ItemExplosiveCatalystBehavior {

    public FireworkRocketExplosiveCatalystBehavior(ResourceLocation id) {
        super(id);
    }

    @Override
    public ExplosiveCatalystData transformExplosiveCatalystData(Level world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ExplosiveCatalystData data) {
        ItemStack stack = blastProcessor.getItem(DeepslateBlastProcessorBlockEntity.CATALYST_INDEX);

        if (stack.get(DataComponents.FIREWORKS) instanceof Fireworks component) {
            boolean producesFire = data.producesFire();
            double explosionPower = data.explosionPower();

            // compute explosion power data from gunpowder
            ExplosiveCatalystData gunpowderData = ExplosiveCatalystDefinitionRecipeLogic.computeExplosiveCatalystData(world, pos, blastProcessor, new ExplosiveCatalystDefinitionRecipeInput(new ItemStack(Items.GUNPOWDER)));

            // add explosion power for the flight duration
            producesFire = producesFire || gunpowderData.producesFire();
            explosionPower += component.flightDuration() * gunpowderData.explosionPower();

            for (FireworkExplosion explosionComponent : component.explosions()) {
                // prepare firework star stack with selected component
                ItemStack starStack = new ItemStack(Items.FIREWORK_STAR);
                starStack.applyComponents(DataComponentMap.builder().set(DataComponents.FIREWORK_EXPLOSION, explosionComponent).build());

                // get explosion power data from star stack
                ExplosiveCatalystDefinitionRecipeInput fireworkStarRecipeInput = new ExplosiveCatalystDefinitionRecipeInput(starStack);
                ExplosiveCatalystData fireworkStarData = ExplosiveCatalystDefinitionRecipeLogic.computeExplosiveCatalystData(world, pos, blastProcessor, fireworkStarRecipeInput);

                // append values to stats
                producesFire = producesFire || fireworkStarData.producesFire();
                explosionPower += fireworkStarData.explosionPower();
            }

            // each recipe produces 3 rockets, so our actual value is 1/3 of what was calculated - then round to the nearest tenth.
            explosionPower /= 3;

            return new ExplosiveCatalystData(this, explosionPower, producesFire);
        }

        return data;
    }

    @Override
    public void onExplosion(Level world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ExplosiveCatalystData powerData, boolean shouldModifyWorld) {
        ItemStack stack = blastProcessor.getItem(DeepslateBlastProcessorBlockEntity.CATALYST_INDEX);

        if (stack.get(DataComponents.FIREWORKS) instanceof Fireworks) {
            Position outputPos = blastProcessor.getExplosionOutputLocation(world.getBlockState(pos).getValue(DeepslateBlastProcessorBlock.HORIZONTAL_FACING));
            FireworkRocketEntity fireworkRocket = new FireworkRocketEntity(world, outputPos.x(), outputPos.y(), outputPos.z(), stack);

            // explode using firework rocket entity code - summons dummy firework and detonates it
            world.addFreshEntity(fireworkRocket);

            // clear the stack from inventory
            blastProcessor.removeItemNoUpdate(DeepslateBlastProcessorBlockEntity.CATALYST_INDEX);

            // TIL you can't have an invoker method be the same name as the original method. The more you know!
            ((FireworkRocketEntityInvoker) fireworkRocket).invokeExplodeAndRemove();
        } else {
            super.onExplosion(world, pos, blastProcessor, powerData, shouldModifyWorld);
        }
    }

    @Override
    public boolean shouldRunDispenserEffects(Level world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessorBlock, ExplosiveCatalystDefinitionRecipeInput recipeInventory) {
        return false;
    }

    @Override
    public boolean isVariable() {
        return true;
    }
}
