package net.myriantics.klaxon.mechanics.explosive_catalyst.behaviors;

import net.myriantics.klaxon.mechanics.explosive_catalyst.ItemExplosiveCatalystBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.projectile.windcharge.AbstractWindCharge;
import net.minecraft.world.entity.projectile.windcharge.WindCharge;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlock;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.mixin.minecraft.blast_processor_behaviors.WindChargeEntityInvoker;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystDefinitionRecipeInput;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystData;

public class WindChargeExplosiveCatalystBehavior extends ItemExplosiveCatalystBehavior {
    public WindChargeExplosiveCatalystBehavior(ResourceLocation id) {
        super(id);
    }

    @Override
    public void onExplosion(Level world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ExplosiveCatalystData powerData, boolean shouldModifyWorld) {
        Position outputPos = blastProcessor.getExplosionOutputLocation(world.getBlockState(pos).getValue(DeepslateBlastProcessorBlock.HORIZONTAL_FACING));
        WindCharge windCharge = new WindCharge(world, outputPos.x(), outputPos.y(), outputPos.z(), Vec3.ZERO);
        WindChargeEntityInvoker windChargeInvoker = ((WindChargeEntityInvoker) windCharge);

        world.addFreshEntity(windCharge);

        // explode
        if (shouldModifyWorld) {
            windChargeInvoker.invokeExplode(new Vec3(outputPos.x(), outputPos.y(), outputPos.z()));
        } else {
            world.explode(
                            windCharge,
                            null,
                            AbstractWindCharge.EXPLOSION_DAMAGE_CALCULATOR,
                            outputPos.x(),
                            outputPos.y(),
                            outputPos.z(),
                            1.2F,
                            false,
                            // replace ExplosionSourceType.TRIGGER to prevent world griefing
                            Level.ExplosionInteraction.NONE,
                            ParticleTypes.GUST_EMITTER_SMALL,
                            ParticleTypes.GUST_EMITTER_LARGE,
                            SoundEvents.WIND_CHARGE_BURST
                    );
        }

        // remove stack and discard
        blastProcessor.removeItemNoUpdate(DeepslateBlastProcessorBlockEntity.CATALYST_INDEX);
        windCharge.discard();
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
