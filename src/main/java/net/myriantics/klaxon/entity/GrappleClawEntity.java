package net.myriantics.klaxon.entity;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import net.myriantics.klaxon.api.Offset;
import net.myriantics.klaxon.networking.KlaxonServerPlayNetworkHandler;
import net.myriantics.klaxon.networking.s2c.ItemUsageLockoutTrigger;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementTriggers;
import net.myriantics.klaxon.registry.entity.KlaxonEntityAttributes;
import net.myriantics.klaxon.registry.entity.KlaxonEntityTypes;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.registry.misc.KlaxonGameRules;
import net.myriantics.klaxon.registry.misc.KlaxonNBTIds;
import net.myriantics.klaxon.registry.misc.KlaxonSoundEvents;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;
import net.myriantics.klaxon.util.BoundingBoxHelper;
import net.myriantics.klaxon.mechanics.entity_weight.EntityWeightHelper;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.GrappleWinchUtil;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.PlayerEntityGrappleAccess;
import net.myriantics.klaxon.util.KlaxonItemStackHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GrappleClawEntity extends PersistentProjectileEntity {

    public static final int MAX_DAMAGE = 10;

    protected static final TrackedData<Float> DAMAGE = DataTracker.registerData(GrappleClawEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private boolean isWinchCableAttached = false;
    private int ticksSinceDamaged = 0;


    public GrappleClawEntity(EntityType<? extends GrappleClawEntity> entityType, World world) {
        super(entityType, world);
    }

    public GrappleClawEntity(World world, double x, double y, double z, ItemStack stack, @Nullable ItemStack shotFrom) {
        super(KlaxonEntityTypes.STEEL_GRAPPLE_CLAW, x, y, z, world, stack, shotFrom);
    }

    public GrappleClawEntity(World world, PlayerEntity player, double x, double y, double z, ItemStack stack, @Nullable ItemStack shotFrom) {
        super(KlaxonEntityTypes.STEEL_GRAPPLE_CLAW, x, y, z, world, stack, shotFrom);
        if (player instanceof ServerPlayerEntity serverPlayer) {
            attachCable(serverPlayer);
        }
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(DAMAGE, 0f);
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return new ItemStack(KlaxonItems.STEEL_GRAPPLE_CLAW);
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        // If we're on the server and succeed in attaching cable to the player, succeed!
        if (player instanceof ServerPlayerEntity serverPlayer && attachCable(serverPlayer)) {
            return ActionResult.SUCCESS;
        } else if (detachCable()) {
            return ActionResult.SUCCESS;
        }

        return super.interact(player, hand);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.getWorld().isClient() || this.isRemoved()) {
            return true;
        } else if (this.isInvulnerableTo(source)) {
            return false;
        } else {

            // players in creative can instantly kill grapple claws
            if (source.getAttacker() instanceof PlayerEntity && ((PlayerEntity)source.getAttacker()).getAbilities().creativeMode) {
                this.kill();
                return true;
            }

            // attacking grapple claw with an unloaded grapple winch attempts to load claw into winch
            ItemStack weaponStack = source.getWeaponStack();
            if (weaponStack != null && weaponStack.isOf(KlaxonItems.GRAPPLE_WINCH)) {
                ChargedProjectilesComponent chargedProjectilesComponent = weaponStack.get(DataComponentTypes.CHARGED_PROJECTILES);
                if (chargedProjectilesComponent == null || chargedProjectilesComponent.isEmpty()) {
                    weaponStack.set(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.of(this.getItemStack()));

                    this.kill();
                    return true;
                }
            }

            float newDamage = getDataTracker().get(DAMAGE) + amount;

            // handle damage
            if (newDamage >= MAX_DAMAGE) {
                // proc entity kill advancement
                if (source.getAttacker() instanceof ServerPlayerEntity serverPlayer) {
                    Criteria.PLAYER_KILLED_ENTITY.trigger(serverPlayer, this, source);
                }

                // if damage exceeded threshold, drop stack and kill
                this.dropStack(this.getItemStack());
                this.kill();
            } else {
                // if damage did not exceed threshold, reset ticks and update damage
                ticksSinceDamaged = 0;
                getDataTracker().set(DAMAGE, newDamage);

                // proc entity hurt advancement
                if (source.getAttacker() instanceof ServerPlayerEntity serverPlayer) {
                    Criteria.PLAYER_HURT_ENTITY.trigger(serverPlayer, this, source, amount, amount, false);
                }
            }
        }



        return super.damage(source, amount);
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
    public float getTargetingMargin() {
        return 0.0f;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();

        if (entity.equals(getOwner()) && entity instanceof ServerPlayerEntity serverPlayer) {

            // if we hit the owner entity try to have the owner pick up claw
            if (this.tryPickup(serverPlayer)) {
                this.discard();
            }

            // if we can't be picked up, bonk all velocity
            setVelocity(Vec3d.ZERO);
        } else {
            // we don't want to damage the retracting player haha
            super.onEntityHit(entityHitResult);
        }
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
            if (
                    this.isAnchored()
                            && owner instanceof ServerPlayerEntity serverPlayer
                            && serverPlayer instanceof PlayerEntityGrappleAccess access
                            && access.klaxon$hasActiveConnection()
                            && !access.klaxon$isRetracting()
            ) {
                serverPlayer.playSoundToPlayer(
                        KlaxonSoundEvents.ITEM_GRAPPLE_WINCH_ANCHOR,
                        SoundCategory.PLAYERS,
                        1.0F,
                        1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F));

                // needs to be here to let client know about grapple claw coords if it lands outside client render distance
                GrappleWinchUtil.updateClientFallbackData(serverPlayer, this);
            }
        } else {
            this.setVelocity(velocity);
        }
    }

    @Override
    public void tick() {
        // update damage reset ticker
        ticksSinceDamaged++;
        if (ticksSinceDamaged > 20) {
            // if damage reset ticker passes threshold, reset damage
            getDataTracker().set(DAMAGE, 0f);
        }


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

                // try to veinmine before breaking the block :)
                if (isWinchCableAttached && owner instanceof PlayerEntityGrappleAccess access && access.klaxon$isRetracting()) {
                    veinmineBlocksIfValid(world, occupiedState, occupiedPos, owner);
                } else {
                    breakBlockIfValid(world, occupiedState, occupiedPos, owner);
                }
            }
        }

        if (owner != null) {
            double ownerDistance = this.getPos().distanceTo(owner.getPos());

            Vec3d selfVec = new Vec3d(0,0, 0);

            if (owner instanceof PlayerEntity player && player instanceof PlayerEntityGrappleAccess access && this.equals(access.klaxon$getGrappleClaw())) {

                double currentWinchCableLength = player.getAttributeValue(KlaxonEntityAttributes.WINCH_CABLE_LENGTH);

                // limit fall distance to give players more leeway
                if (owner.getVelocity().getY() > -1 && owner.fallDistance > 1.0F) {
                    owner.fallDistance = 1.0F;
                }

                // owner being heavy overrides anchoring
                if (!this.isAnchored() || EntityWeightHelper.isHeavy(owner)) {

                    // retract grapple claw if owner pulls back before landing
                    if (access.klaxon$isRetracting()) {
                        Vec3d pulling = player.getEyePos().subtract(getPos()).normalize();
                        // Direction pullingTowards = Direction.getFacing(pulling);

                        if (this.inGround && !world.isClient()) {
                            this.inGround = false;
                        }

                        Vec3d vec = pulling.multiply(4f/20);
                        selfVec = selfVec.add(vec);
                    }

                    // retract grapple claw if it hits limit
                    if (ownerDistance >= currentWinchCableLength) {
                        Vec3d vec = owner.getEyePos().subtract(this.getPos()).normalize().multiply(4f/20);
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

        this.detachIfInvalid();
        super.tick();
    }

    @Override
    protected void age() {
        // only age up if in ground and disconnected
        if (inGround && !(getOwner() instanceof PlayerEntityGrappleAccess access && this.equals(access.klaxon$getGrappleClaw()))) {
            super.age();
        }
    }

    @Override
    protected boolean tryPickup(PlayerEntity player) {
        ItemStack winchStack = player.getMainHandStack().isOf(KlaxonItems.GRAPPLE_WINCH)
                ? player.getMainHandStack()
                : player.getOffHandStack();
        if (winchStack.isOf(KlaxonItems.GRAPPLE_WINCH) && winchStack.getOrDefault(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.DEFAULT).isEmpty()) {
            // try picking up claw into held grapple winch
            winchStack.set(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.of(this.getItemStack()));

            if (player instanceof ServerPlayerEntity serverPlayer) {
                // this is needed so players can choose whether they want to recast grapple claw or not
                if (player.getActiveItem().isOf(KlaxonItems.GRAPPLE_WINCH)) {
                    // update usage lockout if true
                    KlaxonServerPlayNetworkHandler.send(serverPlayer, new ItemUsageLockoutTrigger());
                }
            }

            this.discard();
            return true;
        } else {
            return super.tryPickup(player);
        }
    }

    private boolean veinmineBlocksIfValid(World world, BlockState originState, BlockPos originPos, @NotNull Entity owner) {
        int radius = world.getGameRules().getInt(KlaxonGameRules.GRAPPLE_CLAW_VEINMINE_RADIUS);

        // don't veinmine anything if the source block is not veinmineable
        // also declare failure if radius is 0
        if (!originState.isIn(KlaxonBlockTags.GRAPPLE_CLAW_VEINMINEABLE) || radius == 0) {
            return false;
        }

        Block veinminedBlock = originState.getBlock();

        if (world.isClient) {
            return true;
        }

        // init loot context
        LootContextParameterSet.Builder lootContextBuilder = new LootContextParameterSet.Builder(
                (ServerWorld) world
        )
                .add(LootContextParameters.ORIGIN, getPos())
                .add(LootContextParameters.TOOL, getItemStack());

        // Output stacks to be merged and output at the grapple winch's position
        ArrayList<ItemStack> outputStacks = new ArrayList<>();

        // Positions we've already checked through and destroyed if possible - to be ignored when checking for new positions.
        ArrayList<BlockPos> processedPositions = new ArrayList<>();

        // Contains all of the positions to check on the next pass
        List<BlockPos> targetPositions = List.of(originPos);

        for (int x = 0; x < radius; x++) {
            ArrayList<BlockPos> newTargetPositions = new ArrayList<>();

            // iterate through the current target positions
            for (BlockPos newOriginPos : targetPositions) {
                // iterate through all offset directions from the checking pos
                for (Offset offset : Offset.values()) {
                    BlockPos targetPos = newOriginPos.add(offset.getOffsetVector());

                    // make sure we haven't processed position before
                    if (!processedPositions.contains(targetPos) && world.getBlockState(targetPos).isOf(veinminedBlock)) {
                        // condense dropped stacks so we don't get 5 billion item entities
                        for (ItemStack droppedStack : world.getBlockState(targetPos).getDroppedStacks(lootContextBuilder)) {
                            KlaxonItemStackHelper.insertAndMerge(outputStacks, droppedStack);
                        }
                        world.breakBlock(targetPos, false, owner);
                        processedPositions.add(targetPos);
                        newTargetPositions.add(targetPos);
                    }
                }
            }

            // update target positions list
            targetPositions = newTargetPositions;
        }

        // drop all of the output stacks at the grapple claw's location, ready to be dragged
        for (ItemStack stack : outputStacks) {
            dropStack(stack);
        }

        if (owner instanceof ServerPlayerEntity serverPlayer) {
            KlaxonAdvancementTriggers.triggerGrappleWinchVeinMine(serverPlayer, originState);
        }

        return true;
    }

    /**
     * @param world - the world
     * @param targetState - state of block to break
     * @param targetPos - block to break
     * @param owner - entity to credit block break to
     * @return - true if succeeded in breaking, false if not
     */
    private boolean breakBlockIfValid(World world, BlockState targetState, BlockPos targetPos, @Nullable Entity owner) {
        if (canBreakBlock(world, targetState, targetPos)) {

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

    private boolean canBreakBlock(World world, BlockState state, BlockPos pos) {
        return (state.isIn(KlaxonBlockTags.GRAPPLE_CLAW_BREAKABLE) || state.isReplaceable() || state.getHardness(world, pos) == 0);
    }

    /**
     * Sets the provided player's Grapple Claw to itself, updates attached variable, sets owner to the provided player, and sends out needed update packets.
     * @param serverPlayer
     * The player to form a cable connection to.
     * @return
     * Returns false if attachment failed - (if claw is removed or already attached to player)
     */
    public boolean attachCable(ServerPlayerEntity serverPlayer) {
        if (!this.isRemoved()) {
            PlayerEntityGrappleAccess access = (PlayerEntityGrappleAccess) serverPlayer;
            // make sure we're not reattaching
            if (!this.equals(access.klaxon$getGrappleClaw())) {
                access.klaxon$setGrappleClaw(this);
                this.setOwner(serverPlayer);
                GrappleWinchUtil.updateClientFallbackData(serverPlayer, this);
                isWinchCableAttached = true;
                return true;
            }
        }
        return false;
    }

    /**
     * Only does stuff when {@link GrappleClawEntity#isWinchCableAttached} is true. When run, updates attached to false. <br>
     * Sets owner's grapple claw to null if owner's active grapple claw is this one. <br>
     * Sends packet to client indicating detachment
     *
     */
    public boolean detachCable() {
        if (isWinchCableAttached && this.getOwner() instanceof ServerPlayerEntity serverPlayer && this.equals(((PlayerEntityGrappleAccess) serverPlayer).klaxon$getGrappleClaw())) {
            ((PlayerEntityGrappleAccess) serverPlayer).klaxon$setGrappleClaw(null);
            this.isWinchCableAttached = false;
            GrappleWinchUtil.clearClientFallbackData(serverPlayer, this);
            return true;
        }

        return false;
    }

    /**
     * Detaches cable if attached and owner is no longer holding a Grapple Winch <br>
     * Also detaches if player is removed, dead, or in a different dimension. <br>
     * Called every tick.
     */
    private void detachIfInvalid() {
        if (isWinchCableAttached && getOwner() instanceof ServerPlayerEntity serverPlayer) {
            ItemStack itemStack = serverPlayer.getMainHandStack();
            ItemStack itemStack2 = serverPlayer.getOffHandStack();
            boolean bl = itemStack.isOf(KlaxonItems.GRAPPLE_WINCH);
            boolean bl2 = itemStack2.isOf(KlaxonItems.GRAPPLE_WINCH);

            if (serverPlayer.isRemoved() || !serverPlayer.isAlive() || !(bl || bl2) || !serverPlayer.getWorld().equals(this.getWorld())) {
                this.detachCable();
            }
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        isWinchCableAttached = nbt.getBoolean(KlaxonNBTIds.IS_WINCH_CABLE_ATTACHED);
        ticksSinceDamaged = nbt.getInt(KlaxonNBTIds.TICKS_SINCE_DAMAGED);

        super.readNbt(nbt);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putBoolean(KlaxonNBTIds.IS_WINCH_CABLE_ATTACHED, isWinchCableAttached);
        nbt.putInt(KlaxonNBTIds.TICKS_SINCE_DAMAGED, ticksSinceDamaged);

        return super.writeNbt(nbt);
    }

    @Override
    public void remove(RemovalReason reason) {
        detachCable();
        super.remove(reason);
    }
}