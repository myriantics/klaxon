package net.myriantics.klaxon.api.behavior;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.*;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.blockentities.blast_processor.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.block.customblocks.DeepslateBlastProcessorBlock;
import net.myriantics.klaxon.mixin.FireworkRocketEntityInvoker;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeData;
import net.myriantics.klaxon.recipe.item_explosion_power.ItemExplosionPowerData;

import java.util.List;

public interface BlastProcessorBehavior {

    void onExplosion(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ItemExplosionPowerData powerData);

    void ejectItems(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, BlastProcessingRecipeData recipeData, ItemExplosionPowerData powerData);

    ItemExplosionPowerData getExplosionPowerData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, RecipeInput recipeInventory);

    BlastProcessingRecipeData getBlastProcessingRecipeData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, RecipeInput recipeInventory, ItemExplosionPowerData powerData);

    static void registerBlastProcessorBehaviors() {
        KlaxonCommon.LOGGER.info("Registering KLAXON's Blast Processor Behaviors!");

        DeepslateBlastProcessorBlock.registerBehavior(Items.FIREWORK_ROCKET, new ItemBlastProcessorBehavior() {
            @Override
            public ItemExplosionPowerData getExplosionPowerData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, RecipeInput craftingInventory) {
                ItemStack stack = blastProcessor.getStack(DeepslateBlastProcessorBlockEntity.CATALYST_INDEX);

                FireworksComponent fireworksComponent = stack.get(DataComponentTypes.FIREWORKS);
                List<FireworkExplosionComponent> list =  fireworksComponent != null ? fireworksComponent.explosions() : List.of();

                double explosionPower = 0.0;
                if (!list.isEmpty()) {
                    explosionPower = 0.3 + (list.size() * 0.5);
                }


                return new ItemExplosionPowerData(explosionPower, false);
            }

            @Override
            public void onExplosion(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ItemExplosionPowerData powerData) {
                Position outputPos = blastProcessor.getOutputLocation(world.getBlockState(pos).get(Properties.FACING));
                FireworkRocketEntity fireworkRocket = new FireworkRocketEntity(world, outputPos.getX(), outputPos.getY(), outputPos.getZ(), blastProcessor.getStack(DeepslateBlastProcessorBlockEntity.CATALYST_INDEX).copy());

                // explode using firework rocket entity code - summons dummy firework and detonates it
                world.spawnEntity(fireworkRocket);
                // TIL you can't have an invoker method be the same name as the original method. The more you know!
                ((FireworkRocketEntityInvoker) fireworkRocket).invokeExplodeAndRemove();
            }
        });

        DeepslateBlastProcessorBlock.registerBehavior(Items.FIREWORK_STAR, new ItemBlastProcessorBehavior() {
            @Override
            public ItemExplosionPowerData getExplosionPowerData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, RecipeInput craftingInventory) {
                ItemStack stack = blastProcessor.getStack(DeepslateBlastProcessorBlockEntity.CATALYST_INDEX);

                FireworksComponent fireworksComponent = stack.get(DataComponentTypes.FIREWORKS);
                List<FireworkExplosionComponent> list =  fireworksComponent != null ? fireworksComponent.explosions() : List.of();

                double explosionPower = 0.3;
                if (!list.isEmpty()) {
                    explosionPower += 0.5;
                }

                return new ItemExplosionPowerData(explosionPower, false);
            }
        });
    }
}