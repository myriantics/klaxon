package net.myriantics.klaxon.api.behavior;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.item.*;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.customblocks.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.block.customblocks.blast_processor.deepslate.DeepslateBlastProcessorBlock;
import net.myriantics.klaxon.mixin.FireworkRocketEntityInvoker;
import net.myriantics.klaxon.mixin.WindChargeEntityInvoker;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeData;
import net.myriantics.klaxon.recipe.item_explosion_power.ItemExplosionPowerData;

import java.util.List;

public interface BlastProcessorBehavior {

    void onExplosion(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ItemExplosionPowerData powerData);

    void ejectItems(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, BlastProcessingRecipeData recipeData, ItemExplosionPowerData powerData);

    ItemExplosionPowerData getExplosionPowerData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, RecipeInput recipeInventory);

    BlastProcessingRecipeData getBlastProcessingRecipeData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, RecipeInput recipeInventory, ItemExplosionPowerData powerData);

    BlastProcessorBehaviorItemExplosionPowerEmiDataCompound getEmiData();


    boolean shouldRunDispenserEffects(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessorBlock, RecipeInput recipeInventory);

    static void registerBlastProcessorBehaviors() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Blast Processor Behaviors!");

        DeepslateBlastProcessorBlock.registerBehavior(Items.FIREWORK_ROCKET, new ItemBlastProcessorBehavior() {
            @Override
            public ItemExplosionPowerData getExplosionPowerData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, RecipeInput craftingInventory) {
                ItemStack stack = blastProcessor.getStack(DeepslateBlastProcessorBlockEntity.CATALYST_INDEX);

                FireworksComponent fireworksComponent = stack.get(DataComponentTypes.FIREWORKS);
                List<FireworkExplosionComponent> list =  fireworksComponent != null ? fireworksComponent.explosions() : List.of();

                double explosionPower = 0.3;
                if (!list.isEmpty()) {
                    explosionPower += (list.size() * 0.5);
                }

                explosionPower = Math.min(explosionPower, 10.0);


                return new ItemExplosionPowerData(explosionPower, false);
            }

            @Override
            public void onExplosion(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ItemExplosionPowerData powerData) {
                Position outputPos = blastProcessor.getExplosionOutputLocation(world.getBlockState(pos).get(DeepslateBlastProcessorBlock.HORIZONTAL_FACING));
                FireworkRocketEntity fireworkRocket = new FireworkRocketEntity(world, outputPos.getX(), outputPos.getY(), outputPos.getZ(), blastProcessor.getStack(DeepslateBlastProcessorBlockEntity.CATALYST_INDEX).copy());

                // explode using firework rocket entity code - summons dummy firework and detonates it
                world.spawnEntity(fireworkRocket);

                // clear the stack from inventory
                blastProcessor.removeStack(DeepslateBlastProcessorBlockEntity.CATALYST_INDEX);
                
                // TIL you can't have an invoker method be the same name as the original method. The more you know!
                ((FireworkRocketEntityInvoker) fireworkRocket).invokeExplodeAndRemove();
            }

            @Override
            public boolean shouldRunDispenserEffects(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessorBlock, RecipeInput recipeInventory) {
                return false;
            }

            @Override
            public BlastProcessorBehaviorItemExplosionPowerEmiDataCompound getEmiData() {
                return new BlastProcessorBehaviorItemExplosionPowerEmiDataCompound(
                        0.3,
                        10.0,
                        Text.translatable("klaxon.emi.text.explosion_power_info.firework_behavior_info"),
                        "firework_behavior"
                );
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

            @Override
            public BlastProcessorBehaviorItemExplosionPowerEmiDataCompound getEmiData() {
                return new BlastProcessorBehaviorItemExplosionPowerEmiDataCompound(
                        0.3,
                        0.8,
                        Text.empty(),
                        "firework_star_behavior"
                );
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

            @Override
            public BlastProcessorBehaviorItemExplosionPowerEmiDataCompound getEmiData() {
                return new BlastProcessorBehaviorItemExplosionPowerEmiDataCompound(
                        0.0,
                        5.0,
                        Text.translatable("klaxon.emi.text.explosion_power_info.bed_behavior_info"),
                        "bed_behavior"
                );
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

        DeepslateBlastProcessorBlock.registerBehavior(Items.WIND_CHARGE, new ItemBlastProcessorBehavior() {
            @Override
            public ItemExplosionPowerData getExplosionPowerData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, RecipeInput recipeInventory) {
                // wind charges don't do any damage
                return new ItemExplosionPowerData(0, false);
            }

            @Override
            public void onExplosion(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ItemExplosionPowerData powerData) {
                Position outputPos = blastProcessor.getExplosionOutputLocation(world.getBlockState(pos).get(DeepslateBlastProcessorBlock.HORIZONTAL_FACING));
                WindChargeEntity windCharge = new WindChargeEntity(world, outputPos.getX(), outputPos.getY(), outputPos.getZ(), Vec3d.ZERO);
                WindChargeEntityInvoker windChargeInvoker = ((WindChargeEntityInvoker) windCharge);

                world.spawnEntity(windCharge);
                
                // explode
                windChargeInvoker.invokeCreateExplosion(new Vec3d(outputPos.getX(), outputPos.getY(), outputPos.getZ()));
                
                // remove stack and discard
                blastProcessor.removeStack(DeepslateBlastProcessorBlockEntity.CATALYST_INDEX);
                windCharge.discard();
            }

            @Override
            public boolean shouldRunDispenserEffects(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessorBlock, RecipeInput recipeInventory) {
                return false;
            }

            @Override
            public BlastProcessorBehaviorItemExplosionPowerEmiDataCompound getEmiData() {
                return new BlastProcessorBehaviorItemExplosionPowerEmiDataCompound(
                        0.0,
                        0.0,
                        Text.translatable("klaxon.emi.text.explosion_power_info.wind_charge_behavior_info"),
                        "wind_charge_behavior"
                );
            }
        });
    }

    // long ass name go brrr
    record BlastProcessorBehaviorItemExplosionPowerEmiDataCompound(double explosionPowerMin, double explosionPowerMax, net.minecraft.text.Text infoText, String path) {}
}