package net.myriantics.klaxon.api.behavior.blast_processor_catalyst.behaviors;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;
import net.myriantics.klaxon.api.behavior.blast_processor_catalyst.BlastProcessorExplosionBehavior;
import net.myriantics.klaxon.api.behavior.blast_processor_catalyst.ItemBlastProcessorCatalystBehavior;
import net.myriantics.klaxon.block.customblocks.machines.blast_processor.deepslate.DeepslateBlastProcessorBlock;
import net.myriantics.klaxon.block.customblocks.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.recipe.item_explosion_power.ItemExplosionPowerData;
import net.myriantics.klaxon.registry.minecraft.KlaxonBlocks;

public class CreeperHeadBlastProcessorCatalystBehavior extends ItemBlastProcessorCatalystBehavior {
    public CreeperHeadBlastProcessorCatalystBehavior(Identifier id) {
        super(id);
    }

    @Override
    public void onExplosion(World world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ItemExplosionPowerData powerData, boolean shouldModifyWorld) {
        if (world instanceof ServerWorld serverWorld) {
            BlockState activeBlockState = world.getBlockState(pos);
            if (activeBlockState.getBlock().equals(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR)) {
                if (powerData.explosionPower() > 0.0) {
                    Direction direction = activeBlockState.get(DeepslateBlastProcessorBlock.HORIZONTAL_FACING);
                    Position position = blastProcessor.getExplosionOutputLocation(direction);

                    // mimic a charged creeper because it's really funny
                    CreeperEntity creeperEntity = new CreeperEntity(EntityType.CREEPER, serverWorld);
                    // we have to set the name here because otherwise it would say "blown up by creeper"
                    creeperEntity.setCustomName(Text.translatable("klaxon.text.blast_processor_creeper_name"));
                    creeperEntity.onStruckByLightning(serverWorld, null);

                    blastProcessor.removeStack(DeepslateBlastProcessorBlockEntity.CATALYST_INDEX);
                    serverWorld.createExplosion(creeperEntity, world.getDamageSources().explosion(creeperEntity, creeperEntity),
                            // this is used to differentiate blast processor explosions from normal ones
                            new BlastProcessorExplosionBehavior(shouldModifyWorld),
                            position.getX(), position.getY(), position.getZ(),
                            (float) powerData.explosionPower(),
                            shouldModifyWorld && powerData.producesFire(),
                            World.ExplosionSourceType.BLOCK,
                            ParticleTypes.EXPLOSION,
                            ParticleTypes.EXPLOSION_EMITTER,
                            SoundEvents.ENTITY_GENERIC_EXPLODE);
                    serverWorld.updateNeighbors(pos, activeBlockState.getBlock());

                    // bonk the creeper entity
                    creeperEntity.discard();
                }
            }
        }
    }
}
