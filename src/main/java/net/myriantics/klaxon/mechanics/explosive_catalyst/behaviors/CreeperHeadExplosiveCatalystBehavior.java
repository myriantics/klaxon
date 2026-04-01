package net.myriantics.klaxon.mechanics.explosive_catalyst.behaviors;

import net.minecraft.world.damagesource.DamageSource;
import net.myriantics.klaxon.mechanics.explosive_catalyst.BlastProcessorExplosionBehavior;
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
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystContext;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystData;
import net.myriantics.klaxon.registry.block.KlaxonBlocks;
import org.jetbrains.annotations.Nullable;

public class CreeperHeadExplosiveCatalystBehavior extends DefaultExplosiveCatalystBehavior {
    @Override
    public @Nullable DamageSource getDamageSource(ExplosiveCatalystContext context) {
        if (context.level() instanceof ServerLevel level) {
            Creeper creeper = new Creeper(EntityType.CREEPER, level);

            creeper.setCustomName(Component.translatable("klaxon.text.blast_processor_creeper_name"));
            creeper.thunderHit(level, null);

            creeper.discard();
            return level.damageSources().explosion(null, creeper);
        } else {
            return null;
        }
    }
}
