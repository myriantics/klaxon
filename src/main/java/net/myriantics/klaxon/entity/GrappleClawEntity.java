package net.myriantics.klaxon.entity;

import com.mojang.serialization.Decoder;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
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
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.SimpleVoxelShape;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import net.myriantics.klaxon.networking.s2c.GrappleWinchSyncPacket;
import net.myriantics.klaxon.registry.entity.KlaxonEntityTypes;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.registry.misc.KlaxonSoundEvents;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;
import net.myriantics.klaxon.util.BoundingBoxHelper;
import net.myriantics.klaxon.util.grapple_winch.GrappleWinchClientFallbackData;
import net.myriantics.klaxon.util.grapple_winch.PlayerEntityGrappleAccess;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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
    public ActionResult interact(PlayerEntity player, Hand hand) {
        PlayerEntityGrappleAccess interactorAccess = (PlayerEntityGrappleAccess) player;

        if (!interactorAccess.klaxon$hasActiveConnection() && !(getOwner() instanceof PlayerEntityGrappleAccess ownerAccess && ownerAccess.klaxon$getGrappleClaw().equals(this))) {
            this.setOwner(player);
            interactorAccess.klaxon$setGrappleClaw(this);
            return ActionResult.SUCCESS;
        }

        return super.interact(player, hand);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        ItemStack weaponStack = source.getWeaponStack();
        if (!getWorld().isClient()) {
            if (weaponStack != null && weaponStack.isOf(KlaxonItems.GRAPPLE_WINCH)) {
                ChargedProjectilesComponent chargedProjectilesComponent = weaponStack.get(DataComponentTypes.CHARGED_PROJECTILES);
                if (chargedProjectilesComponent == null || chargedProjectilesComponent.isEmpty()) {
                    weaponStack.set(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.of(this.getItemStack()));
                }
            }
        }
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

        // prep variables
        World world = getWorld();
        BlockPos hitPos = blockHitResult.getBlockPos();
        BlockState hitState = getWorld().getBlockState(hitPos);
        Entity owner = this.getOwner();

        Vec3d velocity = this.getVelocity().multiply(0.85);

        boolean blockBrokenSuccess = breakBlockIfValid(world, hitState, hitPos, owner);

        // break block if we can
        // anchor grapple claw and run super method if block break did not succeed
        if (!blockBrokenSuccess) {

            // if a block was broken, we don't call the super method
            super.onBlockHit(blockHitResult);

            // we have to call super before this because the isAnchored() check will fail otherwise
            if (owner instanceof ServerPlayerEntity serverPlayer && serverPlayer instanceof PlayerEntityGrappleAccess access && access.klaxon$hasActiveConnection()) {
                serverPlayer.playSoundToPlayer(
                        KlaxonSoundEvents.ITEM_GRAPPLE_WINCH_ANCHOR,
                        SoundCategory.PLAYERS,
                        1.0F,
                        1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F));
                ServerPlayNetworking.send(serverPlayer, new GrappleWinchSyncPacket(Optional.of(
                        new GrappleWinchClientFallbackData(
                                this.getPos(),
                                this.isAnchored()
                        )
                ), this.getId()));
            }
        } else {
            this.setVelocity(velocity);
        }
    }

    @Override
    public void tick() {

        @Nullable Entity owner = getOwner();
        World world = this.getWorld();

        // only break blocks if we have velocity
        if (!this.getVelocity().equals(Vec3d.ZERO) && !this.isRemoved() && !this.isAnchored()) {
            ArrayList<BlockPos> checkedPositions = new ArrayList<>();

            VoxelShape clawBoundingBox = VoxelShapes.cuboid(this.getBoundingBox());

            // break blocks on all corners
            for (Vec3d position : BoundingBoxHelper.getCorners(this.getBoundingBox())) {
                BlockPos occupiedPos = BlockPos.ofFloored(position);
                // so we don't check blockpos multiple times if we don't need to
                if (checkedPositions.contains(occupiedPos)) {
                    continue;
                } else {
                    checkedPositions.add(occupiedPos);
                }
                BlockState occupiedState = world.getBlockState(occupiedPos);

                // make sure grapple claw is actually colliding with block hitbox
                /*if (VoxelShapes.matchesAnywhere(occupiedState.getCollisionShape(world, occupiedPos), clawBoundingBox, BooleanBiFunction.AND)) {

                }*/

                breakBlockIfValid(world, occupiedState, occupiedPos, owner);
            }
        }

        if (owner != null) {
            double ownerDistance = this.getPos().squaredDistanceTo(owner.getPos());

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


        }

        // wack shit that i'll fix eventually...
        if (owner instanceof PlayerEntity player) {
            if (!this.removeIfInvalid(player)) {
                super.tick();
            }
        } else {
            super.tick();
        }
    }

    /**
     * @param world - the world
     * @param targetState - state of block to break
     * @param targetPos - block to break
     * @param owner - entity to credit block break to
     * @return - true if succeeded in breaking, false if not
     */
    private boolean breakBlockIfValid(World world, BlockState targetState, BlockPos targetPos, @Nullable Entity owner) {
        if (targetState.isIn(KlaxonBlockTags.GRAPPLE_CLAW_BREAKABLE) || targetState.isReplaceable() || targetState.getHardness(world, targetPos) == 0) {

            // don't break blocks on clientside
            if (!world.isClient()) {
                if (owner != null) {
                    world.breakBlock(targetPos, true, owner);
                } else {
                    world.breakBlock(targetPos, true);
                }
            }

            // a winner is you
            return true;
        }

        return false;
    }

    private void setPlayerGrappleClaw(@Nullable GrappleClawEntity grappleClaw) {
        Entity entity = this.getOwner();
        if (entity instanceof PlayerEntityGrappleAccess access) {
            access.klaxon$setGrappleClaw(grappleClaw);
            if (getOwner() instanceof ServerPlayerEntity serverPlayer) {
                if (grappleClaw == null) {
                    ServerPlayNetworking.send(serverPlayer, new GrappleWinchSyncPacket(Optional.empty(), grappleClaw.getId()));
                } else {
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
}
