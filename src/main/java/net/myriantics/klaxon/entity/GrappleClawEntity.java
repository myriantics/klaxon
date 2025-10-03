package net.myriantics.klaxon.entity;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.myriantics.klaxon.api.Offset;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.GrappleWinchItem;
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
import net.myriantics.klaxon.tag.klaxon.KlaxonDamageTypeTags;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;
import net.myriantics.klaxon.util.BoundingBoxHelper;
import net.myriantics.klaxon.mechanics.entity_weight.EntityWeightHelper;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.GrappleWinchNetworkUtil;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.PlayerEntityGrappleAccess;
import net.myriantics.klaxon.util.KlaxonItemStackHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class GrappleClawEntity extends PersistentProjectileEntity {

    private static final int HIT_INVINCIBILITY_TICKS = 5;

    private boolean isWinchCableAttached = false;
    private int ticksSinceDamaged = 0;

    private PlayerEntity attachedPlayerEntity = null;
    private UUID attachedPlayerEntityUUID = null;

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
    protected ItemStack getDefaultItemStack() {
        return new ItemStack(KlaxonItems.STEEL_GRAPPLE_CLAW);
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        // If we're on the server and succeed in attaching cable to the player, succeed!
        if (player instanceof ServerPlayerEntity serverPlayer && attachCable(serverPlayer)) {
            return ActionResult.SUCCESS;
        } else if (detachCable(false)) {
            return ActionResult.SUCCESS;
        }

        return super.interact(player, hand);
    }

    @Override
    public void kill() {
        this.dropStack(getItemStack());
        super.kill();
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        return super.isInvulnerableTo(damageSource) || damageSource.isIn(DamageTypeTags.BYPASSES_ARMOR);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        World world = this.getWorld();
        @Nullable PlayerEntity attachedPlayer = getAttachedPlayer();

        // conduct electrical damage to the attached player if present because get trolled haha
        if (source.isIn(KlaxonDamageTypeTags.ELECTRICAL) && attachedPlayer != null) {
            attachedPlayer.damage(source, amount);
        }

        if (world.isClient() || this.isRemoved()) {
            return true;
        } else if (this.isInvulnerableTo(source) || ticksSinceDamaged < HIT_INVINCIBILITY_TICKS) {
            return false;
        } else {

            // players in creative can instantly kill grapple claws
            if (source.getAttacker() instanceof PlayerEntity && ((PlayerEntity)source.getAttacker()).getAbilities().creativeMode) {
                this.discard();
                return true;
            }

            // attacking grapple claw with an unloaded grapple winch attempts to load claw into winch
            ItemStack weaponStack = source.getWeaponStack();
            if (weaponStack != null && weaponStack.isOf(KlaxonItems.GRAPPLE_WINCH) && source.getAttacker() instanceof LivingEntity livingAttacker) {
                // if loading succeeded, play indicator sound and discard.
                if (GrappleWinchItem.loadIfPossible(weaponStack, this.getItemStack(), livingAttacker)) {
                    world.playSound(
                            null,
                            this.getX(),
                            this.getEyeY(),
                            this.getZ(),
                            KlaxonSoundEvents.ITEM_GRAPPLE_WINCH_FAST_LOAD,
                            SoundCategory.PLAYERS,
                            0.8f + world.getRandom().nextFloat() * 0.3f,
                            0.7f + world.getRandom().nextFloat() * 0.3f
                    );
                    world.emitGameEvent(
                            GameEvent.ENTITY_ACTION,
                            getPos(),
                            GameEvent.Emitter.of(livingAttacker)
                    );
                    this.discard();
                    return true;
                }
            }

            // retrievers do not damage grapple claw - make sure to return so we don't do damage anyways
            if (weaponStack != null && weaponStack.isIn(KlaxonItemTags.GRAPPLE_CLAW_RETRIEVERS)) {
                this.kill();
                return true;
            }

            ItemStack grappleClawStack = this.getItemStack();

            int damage = grappleClawStack.isDamageable() ? grappleClawStack.getMaxDamage() / 12 : 0;

            // unless hit with a tool
            if (weaponStack != null && weaponStack.isIn(KlaxonItemTags.EFFECTIVE_AGAINST_METAL_ENTITIES)) {
                damage *= 2;
            }

            // damage claw stack and trigger kill advancement if needed
            // also return the value
            return damageClawStack((ServerWorld) getWorld(), source, damage, (item -> {
                if (source.getAttacker() instanceof ServerPlayerEntity serverPlayer) {
                    Criteria.PLAYER_KILLED_ENTITY.trigger(serverPlayer, this, source);
                }

                // play destroy sound
                this.getWorld().playSound(
                        null,
                        getBlockPos(),
                        KlaxonSoundEvents.ENTITY_GRAPPLE_CLAW_DESTROY,
                        SoundCategory.PLAYERS,
                        0.7f + getWorld().getRandom().nextFloat() * 0.3f,
                        0.7f + getWorld().getRandom().nextFloat() * 0.3f
                );
            }));
        }
    }

    /**
     * Damages the contained ItemStack, and returns whether any damage was successfully dealt or not.
     * @param serverWorld needed for cool stuff
     * @param source Optionally the damage source
     * @param damage The raw amount of damage to deal to the grapple claw stack
     * @param consumer Consumer run on item break
     * @return Whether the stack was successfully damaged or not
     */
    private boolean damageClawStack(ServerWorld serverWorld, @Nullable DamageSource source, int damage, Consumer<Item> consumer) {
        ItemStack grappleClawStack = this.getItemStack();

        int appliedDamage = grappleClawStack.getDamage();

        grappleClawStack.damage(
                damage,
                serverWorld,
                source != null && source.getAttacker() instanceof ServerPlayerEntity serverPlayer ? serverPlayer : null,
                (item) -> {
                    // increment broken stat if needed
                    if (source != null && source.getAttacker() instanceof ServerPlayerEntity serverPlayer) {
                        serverPlayer.incrementStat(Stats.BROKEN.getOrCreateStat(item));
                    }

                    consumer.accept(item);
                    this.kill();
                }
        );

        // play damage sound if grapple claw wasn't killed
        if (!grappleClawStack.isEmpty()) {
            this.getWorld().playSound(
                    null,
                    getBlockPos(),
                    KlaxonSoundEvents.ENTITY_GRAPPLE_CLAW_DAMAGE,
                    SoundCategory.PLAYERS,
                    0.8f + getWorld().getRandom().nextFloat() * 0.2f,
                    0.7f + getWorld().getRandom().nextFloat() * 0.3f
            );
            this.getWorld().emitGameEvent(
                    GameEvent.ENTITY_DAMAGE,
                    this.getEyePos(),
                    source == null ? GameEvent.Emitter.of(this) : GameEvent.Emitter.of(source.getAttacker())
            );
        }

        // determine the amount that was actually applied
        appliedDamage -= grappleClawStack.getDamage();

        // proc entity hurt advancement - registers as blocked if stack is unbreakable
        if (source != null && source.getAttacker() instanceof ServerPlayerEntity serverPlayer) {
            Criteria.PLAYER_HURT_ENTITY.trigger(serverPlayer, this, source, damage, appliedDamage, !grappleClawStack.isDamageable());
        }

        if (appliedDamage > 0) {
            this.ticksSinceDamaged = 0;
        }

        return appliedDamage > 0;
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

        if (entity.equals(getAttachedPlayer()) && entity instanceof ServerPlayerEntity serverPlayer) {

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

        boolean blockBrokenSuccess = false;

        // try to veinmine before breaking the block :)
        if (isWinchCableAttached && owner instanceof PlayerEntityGrappleAccess access && access.klaxon$isRetracting()) {
            blockBrokenSuccess = veinmineBlocksIfValid(world, hitState, hitPos, owner);
        }

        // if we didn't veinmine the block, try to break it
        if (!blockBrokenSuccess) {
            blockBrokenSuccess = breakBlockIfValid(world, hitState, hitPos, owner);
        }

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
                        KlaxonSoundEvents.ENTITY_GRAPPLE_CLAW_ANCHOR,
                        SoundCategory.PLAYERS,
                        1.0F,
                        1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F)
                );

                // needs to be here to let client know about grapple claw coords if it lands outside client render distance
                GrappleWinchNetworkUtil.syncToClients(serverPlayer, this);
            }
        } else {
            this.setVelocity(velocity);
        }
    }

    @Override
    public void tick() {
        // update damage reset ticker
        ticksSinceDamaged++;

        @Nullable PlayerEntity attachedPlayer = getAttachedPlayer();
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

        if (attachedPlayer != null) {
            Vec3d attachedEyePos = attachedPlayer.getEyePos();

            double ownerDistance = this.getPos().distanceTo(attachedEyePos);

            PlayerEntityGrappleAccess access = (PlayerEntityGrappleAccess) attachedPlayer;

            Vec3d selfVec = new Vec3d(0,0, 0);

            if (this.equals(access.klaxon$getGrappleClaw())) {

                double currentWinchCableLength = attachedPlayer.getAttributeValue(KlaxonEntityAttributes.WINCH_CABLE_LENGTH);

                // limit fall distance to give players more leeway
                if (attachedPlayer.getVelocity().getY() > -1 && attachedPlayer.fallDistance > 1.0F) {
                    attachedPlayer.fallDistance = 1.0F;
                }

                // owner being heavy overrides anchoring
                if (!this.isAnchored() || EntityWeightHelper.isHeavy(attachedPlayer)) {

                    // retract grapple claw if owner pulls back before landing
                    if (access.klaxon$isRetracting()) {
                        Vec3d pulling = attachedPlayer.getEyePos().subtract(getPos()).normalize();
                        // Direction pullingTowards = Direction.getFacing(pulling);

                        if (this.inGround && !world.isClient()) {
                            this.inGround = false;
                        }

                        Vec3d vec = pulling.multiply(4f/20);
                        selfVec = selfVec.add(vec);
                    }

                    // retract grapple claw if it hits limit
                    if (ownerDistance >= currentWinchCableLength) {
                        Vec3d vec = attachedEyePos.subtract(this.getPos()).normalize().multiply(4f/20);
                        selfVec = selfVec.add(vec);

                        this.playSoundAtSelfAndThroughCableIfPossible(
                                KlaxonSoundEvents.ENTITY_GRAPPLE_CLAW_REBOUND_AT_LIMIT,
                                0.7f + world.getRandom().nextFloat() * 0.3f,
                                0.7f + world.getRandom().nextFloat() * 0.3f
                        );
                        world.emitGameEvent(
                                GameEvent.ENTITY_ACTION,
                                this.getEyePos(),
                                GameEvent.Emitter.of(attachedPlayer)
                        );
                    }
                }

                // players can extend target range by sprinting
                if (attachedPlayer.isSprinting() && attachedPlayer.isOnGround() && ownerDistance > currentWinchCableLength) {
                    access.klaxon$resetWinchCableLength();
                }

            }

            // commit the total velocity edits
            if (!getWorld().isClient()) this.addVelocity(selfVec);
        }

        this.detachIfInvalid();
        super.tick();

        // sync to clients if attached and not in ground
        if (!this.inGround && this.getAttachedPlayer() instanceof ServerPlayerEntity serverPlayer) {
            GrappleWinchNetworkUtil.syncToClients(serverPlayer, this);
        }
    }

    @Override
    protected void age() {
        // only age up if in ground and disconnected
        if (inGround && !isWinchCableAttached) {
            super.age();
        }
    }

    @Override
    protected boolean tryPickup(PlayerEntity player) {
        // if a player is attached, only that player can pick up the grapple claw
        if (isWinchCableAttached && !player.equals(getAttachedPlayer())) {
            return false;
        }

        World world = player.getWorld();

        ItemStack winchStack = player.getMainHandStack().isOf(KlaxonItems.GRAPPLE_WINCH)
                ? player.getMainHandStack()
                : player.getOffHandStack();

        if (GrappleWinchItem.loadIfPossible(winchStack, this.getItemStack(), player)) {

            // this is needed so players can choose whether they want to recast grapple claw or not
            if (player instanceof ServerPlayerEntity serverPlayer) {
                // update usage lockout if true
                KlaxonServerPlayNetworkHandler.send(serverPlayer, new ItemUsageLockoutTrigger());
            }

            world.playSound(
                    player,
                    player.getX(),
                    player.getEyeY(),
                    player.getZ(),
                    KlaxonSoundEvents.ITEM_GRAPPLE_WINCH_LOAD,
                    SoundCategory.PLAYERS,
                    0.7f + world.getRandom().nextFloat() * 0.3f,
                    0.7f + world.getRandom().nextFloat() * 0.3f
            );
            world.emitGameEvent(
                    GameEvent.ENTITY_ACTION,
                    player.getEyePos(),
                    GameEvent.Emitter.of(player)
            );

            this.discard();
            return true;
        } else {
            return super.tryPickup(player);
        }
    }

    private boolean veinmineBlocksIfValid(World world, BlockState originState, BlockPos originPos, @NotNull Entity owner) {
        if (!world.getGameRules().getBoolean(GameRules.PROJECTILES_CAN_BREAK_BLOCKS)) {
            return false;
        }

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

        // counts how many blocks we've broken - used to increment stat at the end
        int blocksBroken = 0;

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

                        blocksBroken++;
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

        // pop advancement trigger and increase mined stat
        if (owner instanceof ServerPlayerEntity serverPlayer) {
            KlaxonAdvancementTriggers.triggerGrappleWinchVeinMine(serverPlayer, originState);
            serverPlayer.increaseStat(Stats.MINED.getOrCreateStat(originState.getBlock()), blocksBroken);
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
        if (!world.getGameRules().getBoolean(GameRules.PROJECTILES_CAN_BREAK_BLOCKS)) {
            return false;
        }

        if (this.canBreakBlock(world, targetState, targetPos)) {

            // don't break blocks on clientside
            if (!world.isClient()) {
                world.breakBlock(targetPos, true, owner);
            }

            if (owner instanceof ServerPlayerEntity serverPlayer) {
                serverPlayer.incrementStat(Stats.MINED.getOrCreateStat(targetState.getBlock()));
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
            // make sure we're not reattaching
            if (!isAttachedToPlayer(serverPlayer)) {
                ((PlayerEntityGrappleAccess) serverPlayer).klaxon$setGrappleClaw(this);
                this.setOwner(serverPlayer);
                GrappleWinchNetworkUtil.syncToClients(serverPlayer, this);
                isWinchCableAttached = true;
                this.attachedPlayerEntity = serverPlayer;
                this.attachedPlayerEntityUUID = serverPlayer.getUuid();
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
    public boolean detachCable(boolean silent) {
        PlayerEntity attachedPlayer = this.getAttachedPlayer();

        if (this.isWinchCableAttached && attachedPlayer != null) {
            if (!silent) {
                // play sound before detaching so we know where to direct the sound
                this.playSoundAtSelfAndThroughCableIfPossible(
                        KlaxonSoundEvents.ENTITY_GRAPPLE_CLAW_DETACH,
                        0.8f + getWorld().getRandom().nextFloat() * 0.2f,
                        0.7f + getWorld().getRandom().nextFloat() * 0.3f
                );
            }

            this.isWinchCableAttached = false;
            this.attachedPlayerEntity = null;
            this.attachedPlayerEntityUUID = null;
            ((PlayerEntityGrappleAccess) attachedPlayer).klaxon$setGrappleClaw(null);

            if (attachedPlayer instanceof ServerPlayerEntity serverPlayer) {
                GrappleWinchNetworkUtil.clearFromClients(serverPlayer, this);
            }
        }

        return false;
    }

    /**
     * Detaches cable if attached and owner is no longer holding a Grapple Winch <br>
     * Also detaches if player is removed, dead, too far away, or in a different dimension. <br>
     * Called every tick.
     */
    private void detachIfInvalid() {
        if (isWinchCableAttached && getAttachedPlayer() instanceof ServerPlayerEntity serverPlayer) {
            ItemStack itemStack = serverPlayer.getMainHandStack();
            ItemStack itemStack2 = serverPlayer.getOffHandStack();
            boolean bl = itemStack.isOf(KlaxonItems.GRAPPLE_WINCH);
            boolean bl2 = itemStack2.isOf(KlaxonItems.GRAPPLE_WINCH);

            boolean cableTooLong = this.getPos().distanceTo(serverPlayer.getEyePos()) > serverPlayer.getAttributeValue(KlaxonEntityAttributes.WINCH_CABLE_LENGTH) * 1.5f;

            if (serverPlayer.isRemoved() || !serverPlayer.isAlive() || !(bl || bl2) || !serverPlayer.getWorld().equals(this.getWorld()) || cableTooLong) {
                this.detachCable(false);
            }
        }
    }

    public @Nullable PlayerEntity getAttachedPlayer() {
        // if there's no cable attached, return null.
        if (!isWinchCableAttached) {
            return null;
        }

        // return the attached player if it's present
        if (attachedPlayerEntity != null) {
            return attachedPlayerEntity;
        }

        // update attached player if it's missing but the UUID is present.
        if (attachedPlayerEntityUUID != null && this.getWorld() instanceof ServerWorld serverWorld) {
            Entity entity = serverWorld.getEntity(attachedPlayerEntityUUID);

            if (entity instanceof PlayerEntity player) {
                attachedPlayerEntity = player;
            }

            return attachedPlayerEntity;
        }

        // if all else fails, return null
        return null;
    }

    public boolean isAttachedToPlayer(PlayerEntity player) {
        return isWinchCableAttached && attachedPlayerEntity != null && attachedPlayerEntity.equals(player) && this.equals(((PlayerEntityGrappleAccess) player).klaxon$getGrappleClaw());
    }

    private void playSoundAtSelfAndThroughCableIfPossible(
            SoundEvent soundEvent,
            float volume,
            float pitch
    ) {
        @Nullable PlayerEntity attachedPlayer = getAttachedPlayer();
        SoundCategory category = attachedPlayer == null ? SoundCategory.NEUTRAL : SoundCategory.PLAYERS;

        World world = this.getWorld();

        world.playSound(
                attachedPlayer,
                attachedPlayer.getX(),
                attachedPlayer.getY(),
                attachedPlayer.getZ(),
                soundEvent,
                category,
                volume,
                pitch
        );

        if (!this.getWorld().isClient()) {
            world.playSound(
                    null,
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    soundEvent,
                    category,
                    volume,
                    pitch
            );
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        if (nbt.contains(KlaxonNBTIds.IS_WINCH_CABLE_ATTACHED)) {
            isWinchCableAttached = nbt.getBoolean(KlaxonNBTIds.IS_WINCH_CABLE_ATTACHED);
        }

        if (nbt.contains(KlaxonNBTIds.TICKS_SINCE_DAMAGED)) {
            ticksSinceDamaged = nbt.getInt(KlaxonNBTIds.TICKS_SINCE_DAMAGED);
        }

        if (nbt.contains(KlaxonNBTIds.WINCH_ATTACHED_PLAYER)) {
            attachedPlayerEntityUUID = nbt.getUuid(KlaxonNBTIds.WINCH_ATTACHED_PLAYER);
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);

        nbt.putBoolean(KlaxonNBTIds.IS_WINCH_CABLE_ATTACHED, isWinchCableAttached);
        nbt.putInt(KlaxonNBTIds.TICKS_SINCE_DAMAGED, ticksSinceDamaged);

        if (this.attachedPlayerEntityUUID != null) {
            nbt.putUuid(KlaxonNBTIds.WINCH_ATTACHED_PLAYER, attachedPlayerEntityUUID);
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        detachCable(true);
        super.remove(reason);
    }
}