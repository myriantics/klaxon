package net.myriantics.klaxon.mechanics.explosive_catalyst.behaviors;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Creeper;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystContext;
import org.jetbrains.annotations.Nullable;

public class CreeperHeadExplosiveCatalystBehavior extends DefaultExplosiveCatalystBehavior {
    @Override
    @Nullable
    protected DamageSource getDamageSource(ExplosiveCatalystContext context) {
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
