package net.myriantics.klaxon.api.behavior.explosive_catalyst.behaviors;

import net.minecraft.entity.projectile.AbstractWindChargeEntity;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.myriantics.klaxon.api.behavior.explosive_catalyst.ItemExplosiveCatalystBehavior;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlock;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.mixin.minecraft.blast_processor_behaviors.WindChargeEntityInvoker;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystDefinitionRecipeInput;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystData;

public class WindChargeExplosiveCatalystBehavior extends ItemExplosiveCatalystBehavior {
    public WindChargeExplosiveCatalystBehavior(Identifier id) {
        super(id);
    }

    @Override
    public ExplosiveCatalystData getExplosionPowerData(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ExplosiveCatalystDefinitionRecipeInput recipeInventory) {
        // wind charges don't do any damage
        return new ExplosiveCatalystData(0, false);
    }

    @Override
    public void onExplosion(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ExplosiveCatalystData powerData, boolean shouldModifyWorld) {
        Position outputPos = blastProcessor.getExplosionOutputLocation(world.getBlockState(pos).get(DeepslateBlastProcessorBlock.HORIZONTAL_FACING));
        WindChargeEntity windCharge = new WindChargeEntity(world, outputPos.getX(), outputPos.getY(), outputPos.getZ(), Vec3d.ZERO);
        WindChargeEntityInvoker windChargeInvoker = ((WindChargeEntityInvoker) windCharge);

        world.spawnEntity(windCharge);

        // explode
        if (shouldModifyWorld) {
            windChargeInvoker.invokeCreateExplosion(new Vec3d(outputPos.getX(), outputPos.getY(), outputPos.getZ()));
        } else {
            world.createExplosion(
                            windCharge,
                            null,
                            AbstractWindChargeEntity.EXPLOSION_BEHAVIOR,
                            outputPos.getX(),
                            outputPos.getY(),
                            outputPos.getZ(),
                            1.2F,
                            false,
                            // replace ExplosionSourceType.TRIGGER to prevent world griefing
                            World.ExplosionSourceType.NONE,
                            ParticleTypes.GUST_EMITTER_SMALL,
                            ParticleTypes.GUST_EMITTER_LARGE,
                            SoundEvents.ENTITY_WIND_CHARGE_WIND_BURST
                    );
        }

        // remove stack and discard
        blastProcessor.removeStack(DeepslateBlastProcessorBlockEntity.CATALYST_INDEX);
        windCharge.discard();
    }

    @Override
    public boolean shouldRunDispenserEffects(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessorBlock, ExplosiveCatalystDefinitionRecipeInput recipeInventory) {
        return false;
    }

    @Override
    public boolean isVariable() {
        return false;
    }
}
