package net.myriantics.klaxon.mechanics.explosive_catalyst.behaviors;

import net.myriantics.klaxon.mechanics.explosive_catalyst.ItemExplosiveCatalystBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.level.Level;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlock;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.networking.KlaxonServerPlayNetworkHandler;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystDefinitionRecipeInput;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystData;
import net.myriantics.klaxon.registry.misc.KlaxonWorldEvents;

public class DragonsBreathExplosiveCatalystBehavior extends ItemExplosiveCatalystBehavior {
    public DragonsBreathExplosiveCatalystBehavior(ResourceLocation id) {
        super(id);
    }

    @Override
    public void onExplosion(Level world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ExplosiveCatalystData powerData, boolean shouldModifyWorld) {
        Position outputPos = blastProcessor.getExplosionOutputLocation(world.getBlockState(pos).getValue(DeepslateBlastProcessorBlock.HORIZONTAL_FACING));

        if (world instanceof ServerLevel serverWorld) {

            AreaEffectCloud areaEffectCloudEntity = new AreaEffectCloud(world, outputPos.x(), outputPos.y() - 0.25, outputPos.z());

            float radius = (float) powerData.explosionPower() / 3;
            float finalRadius = (float) powerData.explosionPower() / 2;

            areaEffectCloudEntity.setParticle(ParticleTypes.DRAGON_BREATH);
            areaEffectCloudEntity.setRadius(radius);
            areaEffectCloudEntity.setDuration(80);
            areaEffectCloudEntity.setRadiusPerTick((finalRadius - radius) / areaEffectCloudEntity.getDuration());
            areaEffectCloudEntity.addEffect(new MobEffectInstance(MobEffects.HARM, 1, 1));

            KlaxonServerPlayNetworkHandler.syncWorldEvent(serverWorld, pos, KlaxonWorldEvents.DRAGONS_BREATH_EXPLOSIVE_CATALYST_CLOUD_SPAWNS, 1);
            world.addFreshEntity(areaEffectCloudEntity);

            blastProcessor.removeItemNoUpdate(DeepslateBlastProcessorBlockEntity.CATALYST_INDEX);
        }
    }

    @Override
    public boolean shouldRunDispenserEffects(Level world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessorBlock, ExplosiveCatalystDefinitionRecipeInput recipeInventory) {
        return false;
    }

    @Override
    public boolean isVariable() {
        return false;
    }
}
