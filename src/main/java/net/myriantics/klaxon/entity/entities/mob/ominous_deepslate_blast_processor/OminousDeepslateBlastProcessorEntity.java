package net.myriantics.klaxon.entity.entities.mob.ominous_deepslate_blast_processor;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.myriantics.klaxon.registry.misc.KlaxonSoundEvents;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class OminousDeepslateBlastProcessorEntity extends Mob {

    protected final float desiredLevitationHeight;
    protected final float levitationTolerance;

    public OminousDeepslateBlastProcessorEntity(EntityType<? extends OminousDeepslateBlastProcessorEntity> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 10;
        this.lookControl = new LookControl(this);
        this.moveControl = new MoveControl(this);
        this.desiredLevitationHeight = 4;
        this.levitationTolerance = 0.25f;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes();
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide() && this.isAlive()) {
            this.tickLevitation();
        }
    }

    @Override
    public InteractionResult interactAt(Player player, Vec3 vec, InteractionHand hand) {
        ItemStack usedStack = player.getItemInHand(hand);
        if (usedStack.is(KlaxonItemTags.OMINOUS_DEEPSLATE_BLAST_PROCESSOR_HEALING_ITEMS) && this.getHealth() < this.getMaxHealth()) {
            if (!this.level().isClientSide()) {
                this.heal(1);
                usedStack.consume(1, player);
                this.playSound(KlaxonSoundEvents.BLOCK_DEEPSLATE_BLAST_PROCESSOR_INSERT, random.nextFloat() * 0.25F + 0.75F, random.nextFloat() + 0.5F);
            }
            return InteractionResult.SUCCESS;
        }
        return super.interactAt(player, vec, hand);
    }

    protected void tickLevitation() {
        if (Objects.requireNonNull(this.getServer()).getTickCount() % 4 == 0) {
            double diff = this.getLevitationVelocity();
            Vec3 motion = this.getDeltaMovement();
            if (Math.signum(this.getDeltaMovement().y) < 0.5) {
                this.setDeltaMovement(motion.x, diff, motion.z);
            } else {
                this.addDeltaMovement(new Vec3(0, diff, 0));
            }
        }
    }

    protected double getLevitationVelocity() {
        double targetHoverHeight = this.getTargetHoverHeight();
        return (targetHoverHeight < 0 ? targetHoverHeight * 1.2 : targetHoverHeight * 0.3) / 20;
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
        return true;
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

    static class LookControl extends net.minecraft.world.entity.ai.control.LookControl {
        public LookControl(Mob mob) {
            super(mob);
            this.xMaxRotAngle = 30;
        }
    }

    static class MoveControl extends net.minecraft.world.entity.ai.control.MoveControl {
        public MoveControl(Mob mob) {
            super(mob);
        }

        @Override
        public void tick() {
            // super.tick();
            if (this.operation == Operation.MOVE_TO) {
                Vec3 motionVec = new Vec3(this.wantedX - this.mob.getX(), this.wantedY - this.mob.getY(), this.wantedZ - this.mob.getZ());
                double distance = motionVec.length();
                motionVec = motionVec.normalize();
                if (this.canReach(motionVec, Mth.ceil(distance))) {
                    this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(motionVec.scale(0.05)));
                } else {
                    this.operation = Operation.WAIT;
                }
            }
        }

        private boolean canReach(Vec3 pos, int length) {
            AABB aABB = this.mob.getBoundingBox();

            for (int i = 1; i < length; i++) {
                aABB = aABB.move(pos);
                if (!this.mob.level().noCollision(this.mob, aABB)) {
                    return false;
                }
            }

            return true;
        }
    }
}
