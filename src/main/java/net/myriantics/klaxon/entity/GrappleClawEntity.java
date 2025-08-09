package net.myriantics.klaxon.entity;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.myriantics.klaxon.registry.entity.KlaxonEntityTypes;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.util.PlayerEntityGrappleAccess;
import org.jetbrains.annotations.Nullable;

public class GrappleClawEntity extends PersistentProjectileEntity {

    public static final int MAX_RANGE_BLOCKS = 48;
    public static final double MAX_RANGE_SQUARED = Math.pow(MAX_RANGE_BLOCKS, 2);
    private double targetRangeSquared = MAX_RANGE_SQUARED;

    public GrappleClawEntity(EntityType<? extends GrappleClawEntity> entityType, World world) {
        super(entityType, world);
    }

    public GrappleClawEntity(World world, double x, double y, double z, ItemStack stack, @Nullable ItemStack shotFrom) {
        super(KlaxonEntityTypes.STEEL_GRAPPLE_CLAW, x, y, z, world, stack, shotFrom);
    }

    public GrappleClawEntity(World world, PlayerEntity player, double x, double y, double z, ItemStack stack, @Nullable ItemStack shotFrom) {
        super(KlaxonEntityTypes.STEEL_GRAPPLE_CLAW, x, y, z, world, stack, shotFrom);
        setOwner(player);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return new ItemStack(KlaxonItems.STEEL_GRAPPLE_CLAW);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        this.kill();
        return super.damage(source, amount);
    }

    @Override
    protected void onHit(LivingEntity target) {
        super.onHit(target);
    }

    @Override
    public boolean canHit() {
        return true;
    }

    @Override
    public boolean isAttackable() {
        return true;
    }

    public boolean isAnchored() {
        return inGround;
    }

    @Override
    public void checkDespawn() {
        super.checkDespawn();
    }

    public void resetTargetRangeSquared() {
        if (getOwner() != null) this.setTargetRangeSquared(getPos().squaredDistanceTo(getOwner().getPos()));
    }

    public void incrementTargetRangeSquared(double increment) {
        this.setTargetRangeSquared(targetRangeSquared + increment);
    }

    public void setTargetRangeSquared(double targetRangeSquared) {
        this.targetRangeSquared = Math.clamp(targetRangeSquared, 0, MAX_RANGE_SQUARED);
    }

    public double getTargetRangeSquared() {
        return targetRangeSquared;
    }

    @Override
    public float getTargetingMargin() {
        return 0.0f;
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        if (this.getOwner() instanceof PlayerEntityGrappleAccess access) {
            this.resetTargetRangeSquared();
            this.incrementTargetRangeSquared(80);
            access.klaxon$setGrappleClawPos(this.getPos());
        }
        super.onBlockHit(blockHitResult);
    }

    @Override
    public void tick() {
        @Nullable Entity owner = getOwner();
        if (owner != null) {
            double ownerDistance = getPos().squaredDistanceTo(owner.getPos());

            Vec3d selfVec = new Vec3d(0,0, 0);

            if (owner instanceof PlayerEntity player && player instanceof PlayerEntityGrappleAccess access && this.equals(access.klaxon$getGrappleClaw())) {
                access.klaxon$setGrappleClawPos(this.getPos());

                // limit fall distance to give players more leeway
                if (owner.getVelocity().getY() > -1 && owner.fallDistance > 1.0F) {
                    owner.fallDistance = 1.0F;
                }

                if (!this.isAnchored()) {
                    // retract grapple claw if owner pulls back before landing
                    if (access.klaxon$isRetracting()) {
                        Vec3d vec = owner.getPos().subtract(this.getPos()).normalize();
                        selfVec = selfVec.add(vec);
                    }

                    // retract grapple claw if it hits limit
                    if (ownerDistance >= targetRangeSquared) {
                        Vec3d vec = owner.getPos().subtract(this.getPos()).normalize().multiply(5f/20);
                        selfVec = selfVec.add(vec);
                    }
                }

                // players can extend target range by sprinting
                if (player.isSprinting() && player.isOnGround() && ownerDistance > targetRangeSquared) {
                    resetTargetRangeSquared();
                    incrementTargetRangeSquared(80);
                }

                // put data in actionbar
                if (owner instanceof ClientPlayerEntity clientPlayer) clientPlayer.sendMessage(Text.literal("dist: " + ownerDistance), true);
            }

            // commit the total velocity edits
            if (!getWorld().isClient()) this.addVelocity(selfVec);

            if (!(getOwner() instanceof PlayerEntity) || !this.removeIfInvalid((PlayerEntity) getOwner())) super.tick();
        }
    }

    private void setPlayerGrappleClaw(@Nullable GrappleClawEntity grappleClaw) {
        Entity entity = this.getOwner();
        if (entity instanceof PlayerEntityGrappleAccess access) {
            access.klaxon$setGrappleClaw(grappleClaw);
        }
    }

    private void clearPlayerGrappleClawIfNeeded() {
        Entity entity = this.getOwner();
        if (entity instanceof PlayerEntityGrappleAccess access && this.equals(access.klaxon$getGrappleClaw())) {
            access.klaxon$setGrappleClaw(null);
        }
    }

    private boolean removeIfInvalid(PlayerEntity player) {
        ItemStack itemStack = player.getMainHandStack();
        ItemStack itemStack2 = player.getOffHandStack();
        boolean bl = itemStack.isOf(KlaxonItems.GRAPPLE_WINCH);
        boolean bl2 = itemStack2.isOf(KlaxonItems.GRAPPLE_WINCH);
        if (!player.isRemoved() && player.isAlive() && (bl || bl2) && player.getWorld().equals(this.getWorld())) {
            return false;
        } else {
            this.discard();
            return true;
        }
    }

    @Override
    public void setOwner(@Nullable Entity entity) {
        super.setOwner(entity);
        setPlayerGrappleClaw(this);
    }

    @Override
    public void onRemoved() {
        clearPlayerGrappleClawIfNeeded();
        super.onRemoved();
    }

    @Override
    public void remove(RemovalReason reason) {
        clearPlayerGrappleClawIfNeeded();
        super.remove(reason);
    }
}
