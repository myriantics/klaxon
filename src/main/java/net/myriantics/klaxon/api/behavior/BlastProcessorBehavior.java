package net.myriantics.klaxon.api.behavior;

import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.blockentities.blast_processor.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.block.customblocks.DeepslateBlastProcessorBlock;
import net.myriantics.klaxon.mixin.FireworkRocketEntityInvoker;
import net.myriantics.klaxon.recipes.blast_processing.BlastProcessingRecipeData;
import net.myriantics.klaxon.recipes.item_explosion_power.ItemExplosionPowerData;

public interface BlastProcessorBehavior {

    void onExplosion(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ItemExplosionPowerData powerData);

    void ejectItems(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, BlastProcessingRecipeData recipeData, ItemExplosionPowerData powerData);

    ItemExplosionPowerData getExplosionPowerData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, SimpleInventory recipeInventory);

    BlastProcessingRecipeData getBlastProcessingRecipeData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, SimpleInventory recipeInventory, ItemExplosionPowerData powerData);

    static void registerBlastProcessorBehaviors() {
        KlaxonCommon.LOGGER.info("Registering KLAXON's Blast Processor Behaviors!");

        DeepslateBlastProcessorBlock.registerBehavior(Items.FIREWORK_ROCKET, new ItemBlastProcessorBehavior() {
            @Override
            public ItemExplosionPowerData getExplosionPowerData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, SimpleInventory craftingInventory) {
                ItemStack stack = blastProcessor.getStack(DeepslateBlastProcessorBlockEntity.CATALYST_INDEX);

                // will have to redo this probably haha
                double explosionPower = 0.0;
                NbtCompound nbtCompound = stack.isEmpty() ? null : stack.getSubNbt("Fireworks");
                NbtList nbtList = nbtCompound != null ? nbtCompound.getList("Explosions", NbtElement.COMPOUND_TYPE) : null;
                if (nbtList != null && !nbtList.isEmpty()) {
                    explosionPower = 0.3 + nbtList.size() * 0.5;
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
            public ItemExplosionPowerData getExplosionPowerData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, SimpleInventory craftingInventory) {
                ItemStack stack = blastProcessor.getStack(DeepslateBlastProcessorBlockEntity.CATALYST_INDEX);

                // will have to redo this probably haha
                double explosionPower = 0.3;
                NbtCompound explosions = stack.isEmpty() ? null : stack.getSubNbt("Explosion");
                if (explosions != null && !explosions.isEmpty()) {
                    explosionPower += 0.5;
                }
                return new ItemExplosionPowerData(explosionPower, false);
            }
        });
    }
}