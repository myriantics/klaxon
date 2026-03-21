package net.myriantics.klaxon.mechanics.explosive_catalyst.behaviors;

import net.myriantics.klaxon.mechanics.explosive_catalyst.BlastProcessorExplosionBehavior;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ItemExplosiveCatalystBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlock;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlockEntity;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystData;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;

public class CreeperHeadExplosiveCatalystBehavior extends ItemExplosiveCatalystBehavior {
    public CreeperHeadExplosiveCatalystBehavior(ResourceLocation id) {
        super(id);
    }

    @Override
    public void onExplosion(Level world, BlockPos pos, DeepslateBlastProcessorBlockEntity blastProcessor, ExplosiveCatalystData powerData, boolean shouldModifyWorld) {
        if (world instanceof ServerLevel serverWorld) {
            BlockState activeBlockState = world.getBlockState(pos);
            if (activeBlockState.getBlock().equals(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR)) {
                if (powerData.explosionPower() > 0.0) {
                    Direction direction = activeBlockState.getValue(DeepslateBlastProcessorBlock.HORIZONTAL_FACING);
                    Position position = blastProcessor.getExplosionOutputLocation(direction);

                    // mimic a charged creeper because it's really funny
                    Creeper creeperEntity = new Creeper(EntityType.CREEPER, serverWorld);
                    // we have to set the name here because otherwise it would say "blown up by creeper"
                    creeperEntity.setCustomName(Component.translatable("klaxon.text.blast_processor_creeper_name"));
                    creeperEntity.thunderHit(serverWorld, null);

                    blastProcessor.removeItemNoUpdate(DeepslateBlastProcessorBlockEntity.CATALYST_INDEX);
                    serverWorld.explode(creeperEntity, world.damageSources().explosion(creeperEntity, creeperEntity),
                            // this is used to differentiate blast processor explosions from normal ones
                            new BlastProcessorExplosionBehavior(shouldModifyWorld),
                            position.x(), position.y(), position.z(),
                            (float) powerData.explosionPower(),
                            shouldModifyWorld && powerData.producesFire(),
                            Level.ExplosionInteraction.BLOCK,
                            ParticleTypes.EXPLOSION,
                            ParticleTypes.EXPLOSION_EMITTER,
                            SoundEvents.GENERIC_EXPLODE);
                    serverWorld.blockUpdated(pos, activeBlockState.getBlock());

                    // bonk the creeper entity
                    creeperEntity.discard();
                }
            }
        }
    }

    @Override
    public boolean isVariable() {
        return false;
    }
}
