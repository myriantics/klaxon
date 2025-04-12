package net.myriantics.klaxon.api.behavior.blast_processor_catalyst.behaviors;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;
import net.myriantics.klaxon.api.behavior.blast_processor_catalyst.ItemBlastProcessorCatalystBehavior;
import net.myriantics.klaxon.block.customblocks.machines.blast_processor.deepslate.DeepslateBlastProcessorBlock;
import net.myriantics.klaxon.block.customblocks.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.mixin.FireworkRocketEntityInvoker;
import net.myriantics.klaxon.recipe.item_explosion_power.ItemExplosionPowerData;

import java.util.List;

public class FireworkRocketBlastProcessorCatalystBehavior extends ItemBlastProcessorCatalystBehavior {

    public FireworkRocketBlastProcessorCatalystBehavior(Identifier id) {
        super(id);
    }

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
                getId().getPath()
        );
    }
}
