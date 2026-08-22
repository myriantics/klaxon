package net.myriantics.klaxon.mechanics.explosive_catalyst.handler;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Position;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystHandler;
import net.myriantics.klaxon.mechanics.explosive_catalyst.context.ExplosiveCatalystContext;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystData;
import net.myriantics.klaxon.registry.explosive_catalyst.KlaxonExplosiveCatalystContextParams;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class DefaultExplosiveCatalystHandler extends ExplosiveCatalystHandler {

    private static final ExplosionDamageCalculator DOES_NOT_EDIT_WORLD = new ExplosionDamageCalculator() {
        @Override
        public boolean shouldBlockExplode(Explosion explosion, BlockGetter reader, BlockPos pos, BlockState state, float power) {
            return false;
        }
    };
    private static final ExplosionDamageCalculator DEFAULT = new ExplosionDamageCalculator();

    private final @Nullable ResourceKey<DamageType> type;
    private final ExplosionDamageCalculator calculator;
    private final Level.ExplosionInteraction interaction;
    private final ParticleOptions smallExplosionParticles;
    private final ParticleOptions largeExplosionParticles;
    private final Holder<SoundEvent> soundEvent;

    public DefaultExplosiveCatalystHandler(
            @Nullable ResourceKey<DamageType> type,
            @Nullable ExplosionDamageCalculator calculator,
            @Nullable Level.ExplosionInteraction interaction,
            @Nullable ParticleOptions smallExplosionParticles,
            @Nullable ParticleOptions largeExplosionParticles,
            @Nullable Holder<SoundEvent> soundEvent

    ) {
        this.type = type;
        this.calculator = calculator == null ? DEFAULT : calculator;
        this.interaction = interaction == null ? Level.ExplosionInteraction.BLOCK : interaction;
        this.smallExplosionParticles = smallExplosionParticles == null ? ParticleTypes.EXPLOSION : smallExplosionParticles;
        this.largeExplosionParticles = largeExplosionParticles == null ? ParticleTypes.EXPLOSION_EMITTER : largeExplosionParticles;
        this.soundEvent = soundEvent == null ? SoundEvents.GENERIC_EXPLODE : soundEvent;
    }

    public DefaultExplosiveCatalystHandler() {
        this.type = null;
        this.calculator = DEFAULT;
        this.interaction = Level.ExplosionInteraction.BLOCK;
        this.smallExplosionParticles = ParticleTypes.EXPLOSION;
        this.largeExplosionParticles = ParticleTypes.EXPLOSION_EMITTER;
        this.soundEvent = SoundEvents.GENERIC_EXPLODE;
    }

    @Override
    public void createExplosion(ExplosiveCatalystContext context, Position detonationPosition, ExplosiveCatalystData data, boolean modifyWorld) {
        if (data.explosionPower() > 0) {
            ServerLevel level = context.level();
            if (!level.isClientSide()) {
                level.explode(
                        context.get(KlaxonExplosiveCatalystContextParams.THIS_ENTITY),
                        this.getDamageSource(context, detonationPosition),
                        this.explosionDamageCalculator(context, data, modifyWorld),
                        detonationPosition.x(),
                        detonationPosition.y(),
                        detonationPosition.z(),
                        (float) data.explosionPower(),
                        modifyWorld && data.producesFire(),
                        this.explosionInteraction(context, data, modifyWorld),
                        this.smallExplosionParticles(context, data),
                        this.largeExplosionParticles(context, data),
                        this.explosionSound(context, data)
                );
            }
        }
    }

    @Nullable
    protected DamageSource getDamageSource(ExplosiveCatalystContext context, Position position) {
        if (this.type == null) {
            return context.level().damageSources().explosion(context.get(KlaxonExplosiveCatalystContextParams.THIS_ENTITY), context.get(KlaxonExplosiveCatalystContextParams.SOURCE_ENTITY));
        } else {
            Optional<Holder.Reference<DamageType>> ref = context.level().damageSources().damageTypes.getHolder(type);
            return new DamageSource(ref.get(), context.get(KlaxonExplosiveCatalystContextParams.THIS_ENTITY), context.get(KlaxonExplosiveCatalystContextParams.SOURCE_ENTITY));
        }
    }

    protected ExplosionDamageCalculator explosionDamageCalculator(ExplosiveCatalystContext context, ExplosiveCatalystData data, boolean modifyWorld) {
        return modifyWorld ? this.calculator : DOES_NOT_EDIT_WORLD;
    }

    protected Level.ExplosionInteraction explosionInteraction(ExplosiveCatalystContext context, ExplosiveCatalystData data, boolean modifyWorld) {
        return this.interaction;
    }

    protected ParticleOptions smallExplosionParticles(ExplosiveCatalystContext context, ExplosiveCatalystData data) {
        return this.smallExplosionParticles;
    }

    protected ParticleOptions largeExplosionParticles(ExplosiveCatalystContext context, ExplosiveCatalystData data) {
        return this.largeExplosionParticles;
    }

    protected Holder<SoundEvent> explosionSound(ExplosiveCatalystContext context, ExplosiveCatalystData data) {
        return this.soundEvent;
    }
}
