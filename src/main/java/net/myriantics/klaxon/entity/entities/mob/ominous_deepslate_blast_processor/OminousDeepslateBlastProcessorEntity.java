package net.myriantics.klaxon.entity.entities.mob.ominous_deepslate_blast_processor;

import com.mojang.serialization.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.myriantics.klaxon.entity.entities.projectile.explosive_deepslate_chunk.ExplosiveDeepslateChunkEntity;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystData;
import net.myriantics.klaxon.registry.entity.KlaxonEntityTypes;
import net.myriantics.klaxon.registry.explosive_catalyst.KlaxonExplosiveCatalystBehaviors;
import net.myriantics.klaxon.registry.misc.KlaxonNBTIds;
import net.myriantics.klaxon.registry.misc.KlaxonSoundEvents;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class OminousDeepslateBlastProcessorEntity extends PathfinderMob implements Enemy, RangedAttackMob {

    protected static final int RAM_COOLDOWN = 80;
    protected static final ExplosiveCatalystData DEFAULT = new ExplosiveCatalystData(KlaxonExplosiveCatalystBehaviors.DRAGONS_BREATH, 3.0, false);

    protected final float desiredLevitationHeight;
    protected final float levitationTolerance;
    protected @Nullable BlockPos blockPos = null;
    protected static final EntityDataAccessor<Boolean> RAMMING = SynchedEntityData.defineId(OminousDeepslateBlastProcessorEntity.class, EntityDataSerializers.BOOLEAN);
    protected int ramCooldown = 0;

    public OminousDeepslateBlastProcessorEntity(EntityType<? extends OminousDeepslateBlastProcessorEntity> entityType, Level level) {
        super(entityType, level);
        this.xpReward = Enemy.XP_REWARD_LARGE;
        this.lookControl = new LookControl(this);
        this.moveControl = new MoveControl(this);
        this.desiredLevitationHeight = 4;
        this.levitationTolerance = 0.25f;
        this.setCanPickUpLoot(true);
    }

    public OminousDeepslateBlastProcessorEntity(Level level, BlockPos pos, ItemStack summoningStack, Direction facing) {
        this(KlaxonEntityTypes.OMINOUS_DEEPSLATE_BLAST_PROCESSOR.value(), level);
        this.setPos(pos.getBottomCenter());
        float wantedRot = facing.toYRot();
        this.absRotateTo(wantedRot, 0);
        ((LookControl) this.lookControl).setWantedYRot(wantedRot);
        this.setYBodyRot(wantedRot);
        this.setYHeadRot(wantedRot);
    }

    @Override
    public void setYRot(float yRot) {
        super.setYRot(yRot);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(RAMMING, false);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.goalSelector.addGoal(2, new HurtByTargetGoal(this, OminousDeepslateBlastProcessorEntity.class));
        this.goalSelector.addGoal(3, new WindRam.Heal(this));
        this.goalSelector.addGoal(4, new ApproachTarget(this));
        this.goalSelector.addGoal(5, new RangedAttackGoal(this, 1.25, 50, 10));
        // this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes();
    }

    @Override
    public void tick() {
        if (!this.level().isClientSide() && this.isAlive()) {
            this.tickLevitation();
        }
        this.setBoundingBox(this.makeBoundingBox());
        Vec3 deltaMovement = this.getDeltaMovement();
        if (deltaMovement.y > 0.001) {
            for (Entity entity : this.level()
                    .getEntities(
                            this,
                            this.getBoundingBox().inflate(0, deltaMovement.y, 0),
                            EntitySelector.NO_SPECTATORS.and(e -> !e.isPassengerOfSameVehicle(this))
                    )) {
                if (!(entity instanceof Shulker || entity instanceof OminousDeepslateBlastProcessorEntity) && !entity.noPhysics && (entity.isControlledByLocalInstance() && (!(entity instanceof Player player) || player.isLocalPlayer()))) {
                    entity.move(MoverType.SHULKER, new Vec3(0, deltaMovement.y * 2.5, 0));
                }
            }
        }
        super.tick();
        if (!this.level().isClientSide() && this.isRamCooldownFinished() && this.isRamming() && (this.verticalCollision || this.horizontalCollision)) {
            this.performRammingExplosion();
        }
        if (this.ramCooldown > 0) {
            this.ramCooldown--;
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.level().isClientSide() && amount > 0 && this.ramCooldown > 0) {
            this.ramCooldown -= Math.clamp((int) (amount * 3), 0, 16);
        }
        return super.hurt(source, amount);
    }

    private void performRammingExplosion() {
        Level level = this.level();
        Vec3 origin = this.getEyePosition();
        final int neededHealing = this.getNeededHealing();
        int healingCredits = 0;
        if (level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) && !this.isInWater()) {
            for (BlockPos pos : BlockPos.randomInCube(this.level().getRandom(), 27, BlockPos.containing(origin), 1)) {
                BlockState targetState = level.getBlockState(pos);
                if (targetState.is(KlaxonBlockTags.OMINOUS_DEEPSLATE_BLAST_PROCESSOR_HEALING_BLOCKS) && healingCredits < neededHealing) {
                    level.setBlockAndUpdate(pos, targetState.getFluidState().createLegacyBlock());
                    healingCredits++;
                } else {
                    level.destroyBlock(pos, true, this);
                }
            }
        }

        for (Entity entity : level.getEntities(this, this.getBoundingBox().inflate(1))) {
            entity.hurt(level.damageSources().explosion(null, this), (float) (entity.position().distanceTo(origin) * 2f));
        }
        this.heal(healingCredits);
        this.setRamming(false);
        this.ramCooldown = RAM_COOLDOWN;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void pickUpItem(ItemEntity itemEntity) {
        ItemStack pickupStack = itemEntity.getItem();
        int neededHealing = this.getNeededHealing();
        if (neededHealing > 0 && pickupStack.is(KlaxonItemTags.OMINOUS_DEEPSLATE_BLAST_PROCESSOR_HEALING_ITEMS)) {
            if (pickupStack.getCount() > neededHealing) {
                pickupStack.shrink(neededHealing);
                this.heal(neededHealing);
                this.playHealSound();
            } else {
                this.heal(pickupStack.getCount());
                this.playHealSound();
                itemEntity.setItem(ItemStack.EMPTY);
                itemEntity.discard();
            }
        }
    }

    protected void playHealSound() {
        this.playSound(KlaxonSoundEvents.BLOCK_DEEPSLATE_BLAST_PROCESSOR_INSERT, this.random.nextFloat() * 0.25F + 0.75F, random.nextFloat() + 0.5F);
    }

    @Override
    public boolean wantsToPickUp(ItemStack stack) {
        return this.getNeededHealing() > 0 && stack.is(KlaxonItemTags.OMINOUS_DEEPSLATE_BLAST_PROCESSOR_HEALING_ITEMS);
    }

    protected int getNeededHealing() {
        return Mth.ceil(this.getMaxHealth() - this.getHealth());
    }

    @Override
    public InteractionResult interactAt(Player player, Vec3 vec, InteractionHand hand) {
        ItemStack usedStack = player.getItemInHand(hand);
        if (usedStack.is(KlaxonItemTags.OMINOUS_DEEPSLATE_BLAST_PROCESSOR_HEALING_ITEMS) && this.getNeededHealing() > 0) {
            if (!this.level().isClientSide()) {
                this.heal(1);
                usedStack.consume(1, player);
                this.playHealSound();
            }
            return InteractionResult.SUCCESS;
        }
        return super.interactAt(player, vec, hand);
    }

    protected void tickLevitation() {
        if (Objects.requireNonNull(this.getServer()).getTickCount() % 4 == 0) {
            double diff = this.getLevitationVelocity();
            Vec3 motion = this.getDeltaMovement();
            if (Math.abs(this.getDeltaMovement().y) < 0.5) {
                this.setDeltaMovement(motion.x, diff, motion.z);
            } else {
                this.addDeltaMovement(new Vec3(0, diff, 0));
            }
        }
    }

    protected double getLevitationVelocity() {
        double targetHoverHeight = this.getTargetHoverHeight();
        return (targetHoverHeight < 0 ? targetHoverHeight * 1.6 : targetHoverHeight * 0.8) / 20;
    }

    protected double getTargetHoverHeight() {
        double distanceToGround = this.getFreeAbsYDistanceBetween(this.position(), this.desiredLevitationHeight * -2);
        double distanceToCeiling = this.getFreeAbsYDistanceBetween(this.position().add(0, this.getBbHeight(), 0), 2);
        // if we've got enough room above and we're within tolerance, make no changes
        if (distanceToCeiling >= 2 && (this.desiredLevitationHeight - distanceToGround > -this.levitationTolerance && this.desiredLevitationHeight - distanceToGround < this.levitationTolerance)) {
            return 0;
        }

        // if we don't have enough room beneath ceiling, pick a point about midway thru, otherwise use normal target height
        return distanceToCeiling < 2 ? ((distanceToGround + this.getBbHeight() + distanceToCeiling) * 0.4) - distanceToGround : this.desiredLevitationHeight - distanceToGround;
    }

    protected double getFreeAbsYDistanceBetween(Vec3 start, float maxYOffsetThatAlsoIndicatesRaycastDirection) {
        Level level = this.level();
        Vec3 end = start.add(0, maxYOffsetThatAlsoIndicatesRaycastDirection, 0);

        BlockHitResult hitResult = BlockGetter.traverseBlocks(start, end, null, (s, pos) -> {
            BlockState state = level.getBlockState(pos);
            VoxelShape shape = state.getCollisionShape(level, pos);
            BlockHitResult hitResult1 = level.clipWithInteractionOverride(start, end, pos, shape, state);

            if (hitResult1 == null || hitResult1.getType().equals(HitResult.Type.MISS)) {
                return null;
            }

            return hitResult1;
        }, (s) -> {
            return BlockHitResult.miss(end, maxYOffsetThatAlsoIndicatesRaycastDirection > 0 ? Direction.UP : Direction.DOWN, BlockPos.containing(end));
        });

        return Math.abs(hitResult.getLocation().y - start.y);
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean canBeCollidedWith() {
        return this.isAlive();
    }

    @Override
    public boolean addEffect(MobEffectInstance effectInstance, @Nullable Entity entity) {
        return false;
    }

    @Override
    public boolean isAffectedByPotions() {
        return false;
    }

    @Override
    public boolean isNoGravity() {
        return this.isAlive();
    }

    @Override
    public int getMaxHeadXRot() {
        return 30;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return super.isInvulnerableTo(source) || source.getEntity() == this || source.getDirectEntity() instanceof ExplosiveDeepslateChunkEntity;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {
        ExplosiveDeepslateChunkEntity chunk = new ExplosiveDeepslateChunkEntity(KlaxonEntityTypes.EXPLOSIVE_DEEPSLATE_CHUNK.value(), this.level());
        chunk.setPos(this.getEyePosition());
        chunk.setData(DEFAULT);
        chunk.setOwner(this);
        double d = target.getEyeY() - 1.1F;
        double e = target.getX() - this.getX();
        double f = d - chunk.getY();
        double g = target.getZ() - this.getZ();
        double h = Math.sqrt(e * e + g * g) * 0.2F;
        chunk.shoot(e, f + h, g, 0.8f, 12.0f);
        level().addFreshEntity(chunk);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean(KlaxonNBTIds.RAMMING, this.isRamming());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setRamming(compound.getBoolean(KlaxonNBTIds.RAMMING));
    }

    public void setRamming(boolean ramming) {
        this.getEntityData().set(RAMMING, ramming);
    }

    public boolean isRamming() {
        return this.getEntityData().get(RAMMING);
    }

    public boolean isRamCooldownFinished() {
        return this.ramCooldown <= 0;
    }

    @Override
    public MoveControl getMoveControl() {
        return (MoveControl) super.getMoveControl();
    }

    static class LookControl extends net.minecraft.world.entity.ai.control.LookControl {

        public LookControl(Mob mob) {
            super(mob);
        }

        public void setWantedYRot(double wantedYRot) {
            this.wantedY = wantedYRot;
        }
    }

    public static class MoveControl extends net.minecraft.world.entity.ai.control.MoveControl {
        public MoveControl(Mob mob) {
            super(mob);
        }

        public void clear() {
            this.operation = Operation.WAIT;
        }

        @Override
        public void tick() {
            super.tick();
            if (this.operation == Operation.MOVE_TO) {
                Vec3 motionVec = new Vec3(this.wantedX - this.mob.getX(), this.wantedY - this.mob.getY(), this.wantedZ - this.mob.getZ());
                double distance = motionVec.length();
                motionVec = motionVec.normalize();
                this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(motionVec.scale(0.05)));
                if (this.canReach(motionVec, Mth.ceil(distance))) {
                } else {
                    this.operation = Operation.WAIT;
                }
            }
        }

        private boolean canReach(Vec3 pos, int length) {
            return true;/*
            AABB aABB = this.mob.getBoundingBox();

            for (int i = 1; i < length; i++) {
                aABB = aABB.move(pos);
                if (!this.mob.level().noCollision(this.mob, aABB)) {
                    return false;
                }
            }

            return true;*/
        }
    }

    static class ApproachTargetGoal extends Goal {

        @Override
        public boolean canUse() {
            return false;
        }
    }

    enum State {
        IDLE,
        ADVANCING,
        STARTUP,
        RETREATING,
        ENRAGED;
    }
}
