package net.myriantics.klaxon.api.behavior.blast_processor_catalyst.behaviors;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.myriantics.klaxon.api.behavior.blast_processor_catalyst.ItemBlastProcessorCatalystBehavior;
import net.myriantics.klaxon.block.customblocks.machines.blast_processor.deepslate.DeepslateBlastProcessorBlock;
import net.myriantics.klaxon.block.customblocks.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.networking.KlaxonServerPlayNetworkHandler;
import net.myriantics.klaxon.recipe.item_explosion_power.ExplosiveCatalystRecipeInput;
import net.myriantics.klaxon.recipe.item_explosion_power.ItemExplosionPowerData;
import net.myriantics.klaxon.registry.custom.KlaxonWorldEvents;

public class DragonsBreathBlastProcessorCatalystBehavior extends ItemBlastProcessorCatalystBehavior {
    public DragonsBreathBlastProcessorCatalystBehavior(Identifier id) {
        super(id);
    }

    @Override
    public void onExplosion(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ItemExplosionPowerData powerData, boolean shouldModifyWorld) {
        Position outputPos = blastProcessor.getExplosionOutputLocation(world.getBlockState(pos).get(DeepslateBlastProcessorBlock.HORIZONTAL_FACING));

        if (world instanceof ServerWorld serverWorld) {

            AreaEffectCloudEntity areaEffectCloudEntity = new AreaEffectCloudEntity(world, outputPos.getX(), outputPos.getY() - 0.25, outputPos.getZ());

            float radius = (float) powerData.explosionPower() / 3;
            float finalRadius = (float) powerData.explosionPower() / 2;

            areaEffectCloudEntity.setParticleType(ParticleTypes.DRAGON_BREATH);
            areaEffectCloudEntity.setRadius(radius);
            areaEffectCloudEntity.setDuration(80);
            areaEffectCloudEntity.setRadiusGrowth((finalRadius - radius) / areaEffectCloudEntity.getDuration());
            areaEffectCloudEntity.addEffect(new StatusEffectInstance(StatusEffects.INSTANT_DAMAGE, 1, 1));

            KlaxonServerPlayNetworkHandler.syncWorldEvent(serverWorld, pos, KlaxonWorldEvents.DRAGONS_BREATH_EXPLOSIVE_CATALYST_CLOUD_SPAWNS, 1);
            world.spawnEntity(areaEffectCloudEntity);

            blastProcessor.removeStack(DeepslateBlastProcessorBlockEntity.CATALYST_INDEX);
        }
    }

    @Override
    public ItemExplosionPowerData getExplosionPowerData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ExplosiveCatalystRecipeInput recipeInventory) {
        return new ItemExplosionPowerData(2.5, false);
    }

    @Override
    public boolean shouldRunDispenserEffects(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessorBlock, ExplosiveCatalystRecipeInput recipeInventory) {
        return false;
    }
}
