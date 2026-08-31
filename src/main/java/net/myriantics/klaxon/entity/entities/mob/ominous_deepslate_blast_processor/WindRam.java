package net.myriantics.klaxon.entity.entities.mob.ominous_deepslate_blast_processor;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.projectile.windcharge.AbstractWindCharge;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.myriantics.klaxon.mixin.minecraft.blast_processor_behaviors.WindChargeInvoker;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;
import org.jetbrains.annotations.Nullable;

public abstract sealed class WindRam extends Goal permits WindRam.Heal, WindRam.Attack{
    final OminousDeepslateBlastProcessorEntity mob;
    boolean ramPerformed;

    public WindRam(OminousDeepslateBlastProcessorEntity mob) {
        this.mob = mob;
    }

    protected abstract @Nullable Vec3 getTargetVec();

    protected abstract boolean extraContinuedUseCondition();

    protected abstract void extraOnStart();

    protected abstract boolean extraCanUse();

    protected abstract Type getType();

    @Override
    public final void start() {
        this.extraOnStart();
    }

    @Override
    public final boolean canUse() {
        return this.mob.isRamCooldownFinished() && !this.mob.isRamming() && this.extraCanUse() && Math.abs(this.mob.getDeltaMovement().y) < 0.25;
    }

    @Override
    public final boolean canContinueToUse() {
        return !this.mob.isRamming() && this.extraContinuedUseCondition();
    }

    @Override
    public void stop() {
        super.stop();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getTargetVec() != null) {
            this.mob.getLookControl().setLookAt(this.getTargetVec().reverse());
            Vec3 normalizedTarget = this.getTargetVec();
            Vec3 explosionPosition = this.mob.position().add(normalizedTarget.scale(0.5));
            this.mob.level().explode(
                    this.mob,
                    null,
                    AbstractWindCharge.EXPLOSION_DAMAGE_CALCULATOR,
                    explosionPosition.x,
                    explosionPosition.y,
                    explosionPosition.z,
                    1.5f,
                    false,
                    Level.ExplosionInteraction.TRIGGER,
                    ParticleTypes.GUST_EMITTER_SMALL,
                    ParticleTypes.GUST_EMITTER_LARGE,
                    SoundEvents.WIND_CHARGE_BURST
            );
            this.mob.addDeltaMovement(normalizedTarget.scale(0.25));
            Vec3 target = this.getTargetVec();
            this.mob.getMoveControl().setWantedPosition(target.x, target.y, target.z, 1.2);
            this.mob.setRamming(true);
            this.mob.ramType = this.getType();
        }
    }

    public static final class Heal extends WindRam {

        private static final float DEEPSLATE_CHECKING_RANGE = 32;

        @Nullable Vec3 launchVector = null;
        int searchCooldown = 0;


        public Heal(OminousDeepslateBlastProcessorEntity mob) {
            super(mob);
        }

        @Override
        protected @Nullable Vec3 getTargetVec() {
            return this.launchVector;
        }

        @Override
        protected boolean extraCanUse() {
            return this.mob.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) && this.isHealthValid();
        }

        @Override
        protected boolean extraContinuedUseCondition() {
            return this.isHealthValid();
        }

        @Override
        protected void extraOnStart() {
            this.launchVector = null;
            this.searchForNearbyDeepslate();
            this.refreshSearchCooldown();
        }

        private boolean isHealthValid() {
            return this.mob.getNeededHealing() > 10;
        }

        private void refreshSearchCooldown() {
            RandomSource random = this.mob.level().getRandom();
            this.searchCooldown = this.mob.getY() > 0 ? random.nextInt(30, 70) : random.nextInt(15, 35);
        }

        private boolean searchForNearbyDeepslate() {
            Level level = this.mob.level();
            RandomSource random = level.random;
            @Nullable Entity target = this.mob.getTarget();
            final Vec3 preferredSearchingDirection;
            if (target != null) {
                preferredSearchingDirection = this.mob.position().subtract(target.position()).normalize();
            } else {
                preferredSearchingDirection = Vec3.directionFromRotation(this.mob.yHeadRot, this.mob.getXRot()).reverse();
            }

            int highestScore = -1;
            Vec3 bestVector = null;
            for (int i = 0; i < 4; i++) {
                final float factor = 0.5f * (i + 1);
                Vec3 checkingVector = preferredSearchingDirection.add(randomNextFloatBetweenNegPos(random) * factor, randomNextFloatBetweenNegPos(random) * factor, randomNextFloatBetweenNegPos(random) * factor).normalize();
                BlockHitResult result = level.clip(new ClipContext(this.mob.getEyePosition(), checkingVector.scale(DEEPSLATE_CHECKING_RANGE), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this.mob));
                int deepslateScore = 0;
                if (result.getType() == HitResult.Type.BLOCK) {
                    for (BlockPos pos : BlockPos.randomInCube(random, 27, result.getBlockPos(), 1)) {
                        if (level.getBlockState(pos).is(KlaxonBlockTags.OMINOUS_DEEPSLATE_BLAST_PROCESSOR_HEALING_BLOCKS)) {
                            deepslateScore++;
                        }
                    }
                }
                if (deepslateScore > highestScore) {
                    highestScore = deepslateScore;
                    bestVector = checkingVector;
                }
            }

            if (highestScore > 0) {
                this.launchVector = bestVector;
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void tick() {
            if (--this.searchCooldown < 0) {
                this.searchForNearbyDeepslate();
                this.refreshSearchCooldown();
            }
            super.tick();
        }

        private static float randomNextFloatBetweenNegPos(RandomSource randomSource) {
            return (randomSource.nextFloat() * 2) - 1;
        }

        @Override
        protected Type getType() {
            return Type.HEAL;
        }
    }

    public static final class Attack extends WindRam {
        public Attack(OminousDeepslateBlastProcessorEntity mob) {
            super(mob);
        }

        @Override
        protected @Nullable Vec3 getTargetVec() {
            return this.mob.getTarget().position().subtract(this.mob.position());
        }

        @Override
        protected boolean extraContinuedUseCondition() {
            return this.mob.getTarget() != null;
        }

        @Override
        protected void extraOnStart() {

        }

        @Override
        protected boolean extraCanUse() {
            return this.mob.getTarget() != null;
        }

        @Override
        protected Type getType() {
            return Type.ATTACK;
        }
    }

    public enum Type {
        HEAL {
            @Override
            public int getCooldownTicks() {
                return 20;
            }
        },
        ATTACK {
            @Override
            public int getCooldownTicks() {
                return 40;
            }
        };

        public abstract int getCooldownTicks();
    }
}
