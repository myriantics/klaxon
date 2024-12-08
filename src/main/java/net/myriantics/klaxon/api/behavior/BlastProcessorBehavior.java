package net.myriantics.klaxon.api.behavior;

import net.minecraft.client.particle.FireworksSparkParticle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.*;
import net.minecraft.recipe.input.RecipeInput;
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

    void onExplosion(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ItemExplosionPowerData powerData, boolean isMuffled);

    void ejectItems(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, BlastProcessingRecipeData recipeData, ItemExplosionPowerData powerData);

    ItemExplosionPowerData getExplosionPowerData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, RecipeInput recipeInventory);

    BlastProcessingRecipeData getBlastProcessingRecipeData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, RecipeInput recipeInventory, ItemExplosionPowerData powerData);

    boolean shouldRunDispenserEffects(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessorBlock, RecipeInput recipeInventory, boolean isMuffled);

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
            public void onExplosion(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ItemExplosionPowerData powerData, boolean isMuffled) {
                Position outputPos = blastProcessor.getExplosionOutputLocation(world.getBlockState(pos).get(DeepslateBlastProcessorBlock.HORIZONTAL_FACING));
                FireworkRocketEntity fireworkRocket = new FireworkRocketEntity(world, outputPos.getX(), outputPos.getY(), outputPos.getZ(), blastProcessor.getStack(DeepslateBlastProcessorBlockEntity.CATALYST_INDEX).copy());

                // explode using firework rocket entity code - summons dummy firework and detonates it
                world.spawnEntity(fireworkRocket);

                // clear the stack from inventory
                blastProcessor.removeStack(DeepslateBlastProcessorBlockEntity.CATALYST_INDEX);

                // muffling override so it doesn't trip sculk sensors
                if (isMuffled) {
                    fireworkRocket.setSilent(true);
                    // custom entity status code so that client knows not to play sound
                    world.sendEntityStatus(fireworkRocket, (byte) 16);
                    ((FireworkRocketEntityInvoker) fireworkRocket).invokeExplode();
                    fireworkRocket.discard();
                    return;
                }
                // TIL you can't have an invoker method be the same name as the original method. The more you know!
                ((FireworkRocketEntityInvoker) fireworkRocket).invokeExplodeAndRemove();
            }

            @Override
            public boolean shouldRunDispenserEffects(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessorBlock, RecipeInput recipeInventory, boolean isMuffled) {
                return false;
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

        ItemBlastProcessorBehavior bedBlastProcessorBehavior = new ItemBlastProcessorBehavior() {
            @Override
            public ItemExplosionPowerData getExplosionPowerData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, RecipeInput recipeInventory) {

                // if the bed doesnt work in dimension, explode
                if (!world.getDimension().bedWorks()) {
                    return new ItemExplosionPowerData(5, true);
                }

                return super.getExplosionPowerData(world, pos, blastProcessor, recipeInventory);
            }
        };

        // maybe ill tagify this using some crap in the future, but this is fine for now.
        DeepslateBlastProcessorBlock.registerBehavior(Items.RED_BED, bedBlastProcessorBehavior);
        DeepslateBlastProcessorBlock.registerBehavior(Items.BLACK_BED, bedBlastProcessorBehavior);
        DeepslateBlastProcessorBlock.registerBehavior(Items.BROWN_BED, bedBlastProcessorBehavior);
        DeepslateBlastProcessorBlock.registerBehavior(Items.CYAN_BED, bedBlastProcessorBehavior);
        DeepslateBlastProcessorBlock.registerBehavior(Items.BLUE_BED, bedBlastProcessorBehavior);
        DeepslateBlastProcessorBlock.registerBehavior(Items.GRAY_BED, bedBlastProcessorBehavior);
        DeepslateBlastProcessorBlock.registerBehavior(Items.GREEN_BED, bedBlastProcessorBehavior);
        DeepslateBlastProcessorBlock.registerBehavior(Items.LIGHT_BLUE_BED, bedBlastProcessorBehavior);
        DeepslateBlastProcessorBlock.registerBehavior(Items.LIGHT_GRAY_BED, bedBlastProcessorBehavior);
        DeepslateBlastProcessorBlock.registerBehavior(Items.LIME_BED, bedBlastProcessorBehavior);
        DeepslateBlastProcessorBlock.registerBehavior(Items.MAGENTA_BED, bedBlastProcessorBehavior);
        DeepslateBlastProcessorBlock.registerBehavior(Items.ORANGE_BED, bedBlastProcessorBehavior);
        DeepslateBlastProcessorBlock.registerBehavior(Items.PINK_BED, bedBlastProcessorBehavior);
        DeepslateBlastProcessorBlock.registerBehavior(Items.PURPLE_BED, bedBlastProcessorBehavior);
        DeepslateBlastProcessorBlock.registerBehavior(Items.WHITE_BED, bedBlastProcessorBehavior);
        DeepslateBlastProcessorBlock.registerBehavior(Items.YELLOW_BED, bedBlastProcessorBehavior);
    }
}