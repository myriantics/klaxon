package net.myriantics.klaxon.entity;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.myriantics.klaxon.networking.s2c.GrappleWinchSyncPacket;
import net.myriantics.klaxon.registry.entity.KlaxonEntityTypes;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.registry.misc.KlaxonSoundEvents;
import net.myriantics.klaxon.util.grapple_winch.GrappleWinchClientFallbackData;
import net.myriantics.klaxon.util.grapple_winch.PlayerEntityGrappleAccess;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class GrappleClawEntity extends PersistentProjectileEntity {

    public static final int MAX_RANGE_BLOCKS = 128;
    public static final double MAX_RANGE_SQUARED = Math.pow(MAX_RANGE_BLOCKS, 2);

    public GrappleClawEntity(EntityType<? extends GrappleClawEntity> entityType, World world) {
        super(entityType, world);
        if (getOwner() instanceof PlayerEntityGrappleAccess access && !this.equals(access.klaxon$getGrappleClaw())) {
            this.discard();
        }
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

    @Override
    public float getTargetingMargin() {
        return 0.0f;
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        // we have to call super before this because the isAnchored() check will fail otherwise
        if (this.getOwner() instanceof ServerPlayerEntity serverPlayer && serverPlayer instanceof PlayerEntityGrappleAccess access && access.klaxon$hasActiveConnection()) {
            serverPlayer.playSoundToPlayer(
                    KlaxonSoundEvents.ITEM_GRAPPLE_WINCH_ANCHOR,
                    SoundCategory.PLAYERS,
                    1.0F,
                    1.0F / (serverPlayer.getWorld().getRandom().nextFloat() * 0.4F + 1.2F));
            ServerPlayNetworking.send(serverPlayer, new GrappleWinchSyncPacket(Optional.of(
                    new GrappleWinchClientFallbackData(
                            this.getPos(),
                            this.isAnchored()
                    )
            ), this.getId()));
        }
    }

    @Override
    public void tick() {
        @Nullable Entity owner = getOwner();
        if (owner != null) {
            double ownerDistance = getPos().squaredDistanceTo(owner.getPos());

            Vec3d selfVec = new Vec3d(0,0, 0);

            if (owner instanceof PlayerEntity player && player instanceof PlayerEntityGrappleAccess access && this.equals(access.klaxon$getGrappleClaw())) {

                double currentWinchCableLength = MAX_RANGE_SQUARED;

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
                    if (ownerDistance >= currentWinchCableLength) {
                        Vec3d vec = owner.getPos().subtract(this.getPos()).normalize().multiply(5f/20);
                        selfVec = selfVec.add(vec);
                    }
                }

                // players can extend target range by sprinting
                if (player.isSprinting() && player.isOnGround() && ownerDistance > currentWinchCableLength) {
                    access.klaxon$resetWinchCableLength();
                }

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
            access.klaxon$setWinchFallbackData(null);
            if (entity instanceof ServerPlayerEntity serverPlayer) {
                ServerPlayNetworking.send(serverPlayer, new GrappleWinchSyncPacket(Optional.empty(), getId()));
            }
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
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
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
        if (getOwner() instanceof ServerPlayerEntity serverPlayer) {
            ServerPlayNetworking.send(serverPlayer, new GrappleWinchSyncPacket(Optional.empty(), getId()));
        }
        // if (getOwner() instanceof PlayerEntityGrappleAccess access) access.klaxon$resetWinchCableLength();
        clearPlayerGrappleClawIfNeeded();
        super.remove(reason);
    }

    public static void onLoadedServerside(Entity entity, ServerWorld serverWorld) {
        if (entity instanceof GrappleClawEntity grappleClaw && grappleClaw.isAnchored()) {
            Entity owner = grappleClaw.getOwner();

            if (owner instanceof ServerPlayerEntity serverPlayer && owner instanceof PlayerEntityGrappleAccess access) {
                ServerPlayNetworking.send(serverPlayer, new GrappleWinchSyncPacket(Optional.of(
                        new GrappleWinchClientFallbackData(
                                grappleClaw.getPos(),
                                grappleClaw.isAnchored()
                        )
                ), grappleClaw.getId()));
            }
        }
    }
}
