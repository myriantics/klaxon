package net.myriantics.klaxon.entity;

import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
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

    private final double MAX_RANGE_SQUARED = 6400;
    private double targetRangeSquared = 6400;
    private boolean isRetracting = false;

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

    public void setTargetRangeSquared(double targetRangeSquared) {
        this.targetRangeSquared = Math.clamp(targetRangeSquared, 0, MAX_RANGE_SQUARED);
    }

    @Override
    public float getTargetingMargin() {
        return 0.0f;
    }

    public void setRetracting(boolean retracting) {
        this.isRetracting = retracting;
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        if (this.getOwner() != null) this.targetRangeSquared = getPos().squaredDistanceTo(getOwner().getPos());
        super.onBlockHit(blockHitResult);
    }

    @Override
    public void tick() {
        @Nullable Entity owner = getOwner();
        if (owner != null) {
            double ownerDistance = getPos().squaredDistanceTo(owner.getPos());

            Vec3d ownerVec = new Vec3d(0, 0, 0);
            Vec3d selfVec = new Vec3d(0,0, 0);

            if (owner instanceof PlayerEntity player && player instanceof PlayerEntityGrappleAccess access && this.equals(access.klaxon$getGrappleClaw())) {
                if (this.isAnchored()) {
                    if (this.isRetracting) {
                        if (getWorld().isClient()) {
                            Vec3d vec = this.getPos().subtract(owner.getPos()).normalize().multiply(0.25);

                            // owner goes towards claw if not sneaking, away if they are sneaking
                            if (!owner.isSneaking()) {
                                ownerVec = ownerVec.add(vec);
                            } else if (ownerDistance < targetRangeSquared && vec.getY() >= 0) {
                                // no free flight
                                ownerVec = ownerVec.add(0, -vec.getY(), 0);
                            }

                        }
                    } else {
                        // put data in actionbar
                        if (getWorld().isClient() && owner instanceof ClientPlayerEntity clientPlayer) clientPlayer.sendMessage(Text.literal("range: " + targetRangeSquared), true);

                        // apply velocity to player if they go past target range
                        if (ownerDistance >= targetRangeSquared && owner instanceof ClientPlayerEntity) {
                            Vec3d vec = this.getPos().subtract(owner.getPos()).normalize().multiply(0.1);
                            // cancel gravity
                            vec = vec.add(0, owner.getFinalGravity(), 0);
                            ownerVec = ownerVec.add(vec);
                        }

                        // allow owner to cancel grapple winch velocity if they're not retracting
                        if (owner.isSneaking()) {
                            ownerVec = ownerVec.multiply(Vec3d.ZERO);

                            // don't drop people out of the sky if they crouch midair - also reset target range
                            if (!owner.isOnGround()) {
                                owner.setVelocity(owner.getVelocity().multiply(new Vec3d(1, 0, 1)));
                                targetRangeSquared = ownerDistance;
                            }
                        }
                    }
                } else {
                    // retract grapple claw if owner pulls back before landing
                    if (this.isRetracting) {
                        Vec3d vec = owner.getPos().subtract(this.getPos()).normalize();
                        selfVec = selfVec.add(vec);
                    }

                    // retract grapple claw if it hits limit
                    if (ownerDistance >= MAX_RANGE_SQUARED + 1) {
                        Vec3d vec = owner.getPos().subtract(this.getPos()).normalize().multiply(0.05);
                        selfVec = selfVec.add(vec);
                    }
                }
            }

            owner.addVelocity(ownerVec);
            this.addVelocity(selfVec);

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
        if (!player.isRemoved() && player.isAlive() && (bl || bl2)) {
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
