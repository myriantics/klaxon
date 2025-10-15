package net.myriantics.klaxon.entity;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.network.ClientPlayerEntity;
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

    private Entity grappledEntity = null;
    private PlayerEntity attachedPlayerEntity = null;
    private UUID attachedPlayerEntityUUID = null;

    public GrappleClawEntity(EntityType<? extends GrappleClawEntity> entityType, World world) {
        super(entityType, world);
    }

    public GrappleClawEntity(World world, double x, double y, double z, ItemStack stack, @Nullable ItemStack shotFrom) {
        super(KlaxonEntityTypes.STEEL_GRAPPLE_CLAW, x, y, z, world, stack, shotFrom);
    }

    public GrappleClawEntity(World world, LivingEntity owner, ItemStack stack, @Nullable ItemStack shotFrom) {
        super(KlaxonEntityTypes.STEEL_GRAPPLE_CLAW, owner, world, stack, shotFrom);
    }

    public GrappleClawEntity(World world, PlayerEntity player, double x, double y, double z, ItemStack stack, @Nullable ItemStack shotFrom) {
        super(KlaxonEntityTypes.STEEL_GRAPPLE_CLAW, x, y, z, world, stack, shotFrom);
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return new ItemStack(KlaxonItems.STEEL_GRAPPLE_CLAW);
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        ItemStack handStack = player.getStackInHand(hand);

        if (this.isWinchCableAttached || this.equals(((PlayerEntityGrappleAccess)player).klaxon$getGrappleClaw())) {
            // attempt to detach grapple winch cable if shears are used on it
            if (handStack.isIn(KlaxonItemTags.GRAPPLE_WINCH_CABLE_DETACHERS)) {
                if (player instanceof ServerPlayerEntity) {
                    return this.detachCable(false) ? ActionResult.SUCCESS : ActionResult.PASS;
                }

                return ActionResult.SUCCESS;
            }
        } else {
            // attempt to pick up / load the attached grapple claw
            // if that fails, just pick self up and discard
            if (!this.tryFastReload(player)) {
                player.sendPickup(this, 1);
                this.discard();
            }
            return ActionResult.SUCCESS;
        }

        return super.interact(player, hand);
    }

    @Override
    public void kill() {
        if (this.pickupType.equals(PickupPermission.ALLOWED)) {
            this.dropStack(getItemStack());
        }
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
        if (source.isIn(KlaxonDamageTypeTags.GRAPPLE_WINCH_CABLE_TRANSMISSIBLE) && attachedPlayer != null) {
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

            ItemStack weaponStack = source.getWeaponStack();
            Entity attacker = source.getAttacker();

            // make sure the attacker is a player entity - then, check if we're either not attached or the player is the attached player
            // if this passes, attempt fast reloading - and return true if that passes.
            if (attacker instanceof PlayerEntity player && (!this.isWinchCableAttached || player.equals(attachedPlayer)) && this.tryFastReload(player)) {
                return true;
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
        return inGround || (this.grappledEntity != null && EntityWeightHelper.isHeavy(this.grappledEntity));
    }

    @Override
    public float getTargetingMargin() {
        return 0.0f;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity hitEntity = entityHitResult.getEntity();
        @Nullable PlayerEntity attachedPlayer = this.getAttachedPlayer();

        // don't bother reattaching to currently grappled entity
        if (hitEntity.equals(this.grappledEntity)) {
            return;
        }

        // check that we're attached to a cable
        if (this.isWinchCableAttached) {
            // if we hit the attached player, attempt to fast reload
            if (hitEntity.equals(attachedPlayer)) {
                if (!this.tryFastReload(attachedPlayer)) {
                    // if we can't be picked up, bonk all velocity
                    setVelocity(Vec3d.ZERO);
                }
            } else {
                // attempt to hook into entity
                this.updateHookedEntity(hitEntity);
            }
        }
    }

    private void updateHookedEntity(@Nullable Entity entity) {
        this.grappledEntity = entity;
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        // prep variables
        World world = getWorld();
        BlockPos hitPos = blockHitResult.getBlockPos();
        BlockState hitState = getWorld().getBlockState(hitPos);
        PlayerEntity attachedPlayer = this.getAttachedPlayer();

        if (this.tryBreakingBlocks(world, hitState, hitPos)) {
            this.setVelocity(this.getVelocity().multiply(0.85));
        } else {
            // if a block was broken, we don't call the super method
            super.onBlockHit(blockHitResult);

            if (attachedPlayer != null) {
                if (this.isWinchCableAttached) {
                    this.playSoundAtSelfAndThroughCableIfPossible(
                            KlaxonSoundEvents.ENTITY_GRAPPLE_CLAW_ANCHOR,
                            1.0F,
                            1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F)
                    );

                    // needs to be here to let client know about grapple claw coords if it lands outside client render distance
                    if (attachedPlayer instanceof ServerPlayerEntity serverPlayer) {
                        GrappleWinchNetworkUtil.syncToClients(serverPlayer, this);
                    }
                }
            }
        }
    }

    /**
     * Called while checking for block collision, for each currently colliding block.
     *
     * @param world
     * @param occupiedState
     * @param pos
     */
    public void onBlockPosIntersection(World world, BlockState occupiedState, BlockPos pos) {
        // make sure we're neither anchored nor removed
        if (this.isAnchored() || this.isRemoved()) {
            return;
        }

        VoxelShape occupiedStateShape = occupiedState.getCollisionShape(world, pos);

        // make sure we actually collide with the target bounding box
        if (occupiedStateShape.isEmpty() || !this.getBoundingBox().intersects(occupiedStateShape.getBoundingBox().offset(pos))) {
            return;
        }

        this.tryBreakingBlocks(world, occupiedState, pos);
    }

    private boolean tryBreakingBlocks(World world, BlockState occupiedState, BlockPos pos) {
        // make sure projectiles can break blocks
        if (!world.getGameRules().getBoolean(GameRules.PROJECTILES_CAN_BREAK_BLOCKS)) {
            return false;
        }

        PlayerEntity attachedPlayer = this.getAttachedPlayer();

        // try to veinmine before breaking the block :)
        if (this.isWinchCableAttached && attachedPlayer != null && ((PlayerEntityGrappleAccess) attachedPlayer).klaxon$isRetracting()) {
            return veinmineBlocksIfValid(world, occupiedState, pos, attachedPlayer);
        } else {
            return breakBlockIfValid(world, occupiedState, pos, this.getOwner());
        }
    }

    @Override
    public void tick() {
        // update damage reset ticker
        ticksSinceDamaged++;

        @Nullable PlayerEntity attachedPlayer = getAttachedPlayer();
        World world = this.getWorld();

        if (attachedPlayer != null) {
            // clear grappled entity if it was removed
            if (this.grappledEntity != null && this.grappledEntity.isRemoved()) {
                this.grappledEntity = null;
            }

            Vec3d attachedEyePos = attachedPlayer.getEyePos();

            double ownerDistance = this.getPos().distanceTo(attachedEyePos);

            PlayerEntityGrappleAccess access = (PlayerEntityGrappleAccess) attachedPlayer;

            boolean retracting = access.klaxon$isRetracting();

            Vec3d selfVec = new Vec3d(0,0, 0);

            double currentWinchCableLength = attachedPlayer.getAttributeValue(KlaxonEntityAttributes.WINCH_CABLE_LENGTH);

            // limit fall distance to give players more leeway
            if (attachedPlayer.getVelocity().getY() > -1 && attachedPlayer.fallDistance > 1.0F) {
                attachedPlayer.fallDistance = 1.0F;
            }

            // if the attached player is heavy and retracting, de-anchor and pop advancement
            if (!world.isClient && this.isAnchored() && retracting && EntityWeightHelper.isHeavy(attachedPlayer)) {
                this.inGround = false;
                KlaxonAdvancementTriggers.triggerGrappleWinchDeAnchorGrappleClaw((ServerPlayerEntity) attachedPlayer);
            }

            // owner being heavy overrides anchoring
            if (!this.isAnchored()) {

                // retract grapple claw if owner pulls back before landing
                if (retracting) {
                    Vec3d pulling = attachedEyePos.subtract(getPos()).normalize();

                    Vec3d vec = pulling.multiply(4f/20);
                    selfVec = selfVec.add(vec);
                }

                // retract grapple claw if it hits limit
                if (ownerDistance >= currentWinchCableLength) {
                    Vec3d vec = attachedEyePos.subtract(this.getPos()).normalize().multiply(4f/20);
                    selfVec = selfVec.add(vec);

                    this.playSoundAtSelfAndThroughCableIfPossible(
                            KlaxonSoundEvents.ENTITY_GRAPPLE_CLAW_REBOUND_AT_LIMIT,
                            1f + world.getRandom().nextFloat() * 0.3f,
                            0.8f + world.getRandom().nextFloat() * 0.2f
                    );
                    world.emitGameEvent(
                            GameEvent.ENTITY_ACTION,
                            this.getEyePos(),
                            GameEvent.Emitter.of(attachedPlayer)
                    );
                }
            }

            // commit the total velocity edits to self or whatever entity we're attached to
            this.moveSelfOrGrappledEntity(selfVec);
            // after this, update position and velo to the grappled entity position and velo
            if (!this.getWorld().isClient && this.grappledEntity != null) {
                this.setPosition(this.grappledEntity.getEyePos());
            }
        }

        this.detachIfInvalid();
        super.tick();

        // sync to clients if attached and not in ground
        if (!this.inGround && this.getAttachedPlayer() instanceof ServerPlayerEntity serverPlayer) {
            GrappleWinchNetworkUtil.syncToClients(serverPlayer, this);
        }
    }

    public @Nullable Entity getGrappledEntity() {
        return grappledEntity;
    }

    protected void moveSelfOrGrappledEntity(Vec3d velocityToAdd) {
        Entity grappledEntity = this.grappledEntity;
        boolean client = this.getWorld().isClient();

        switch (grappledEntity) {
            case PlayerEntity player -> {
                if (player instanceof ClientPlayerEntity) {
                    player.addVelocity(velocityToAdd);
                }
            }
            case null -> {
                if (!client) {
                    this.addVelocity(velocityToAdd);
                }
            }
            default -> {
                grappledEntity.addVelocity(velocityToAdd);
            }
        }

        if (grappledEntity == null) {
            if (!client) {
                this.addVelocity(velocityToAdd);
            }
        }
    }

    @Override
    protected void age() {
        // only age up if in ground and disconnected
        if (inGround && !isWinchCableAttached) {
            super.age();
        }
    }

    /**
     * Attempt to perform a fast-reloading operation. Plays a sound, emits game event, discards self, and detaches grapple cable if successful.
     * @param pickupPlayer - Player that is attempting to fast-reload this Grapple Claw into their Grapple Winch
     * @return Whether the fast loading succeeded or not
     */
    public boolean tryFastReload(PlayerEntity pickupPlayer) {
        BlockPos steppingPos = pickupPlayer.getSteppingPos();
        BlockPos anchoredPos = this.getBlockPos();

        // don't pick up grapple claw while you're being supported by it
        if ((!pickupPlayer.isOnGround() && anchoredPos.getY() > steppingPos.getY())) {
            return false;
        }

        World world = pickupPlayer.getWorld();

        // find the player's grapple winch stack - main hand priority, then offhand
        ItemStack winchStack = pickupPlayer.getMainHandStack();
        if (!(winchStack.getItem() instanceof GrappleWinchItem)) {
            winchStack = pickupPlayer.getOffHandStack();

            // if both the main hand stack and offhand stack aren't grapple winches, fail the fast reload
            if (!(winchStack.getItem() instanceof GrappleWinchItem)) {
                return false;
            }
        }

        ChargedProjectilesComponent projectiles = winchStack.getOrDefault(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.DEFAULT);

        // make sure that the grapple winch is empty and that we're either unattached or being loaded into the attached player's grapple winch
        if (projectiles.isEmpty() && (!this.isWinchCableAttached || pickupPlayer.equals(this.getAttachedPlayer()))) {
            // this is needed so players can choose whether they want to recast grapple claw or not
            // only trigger this if pickup occurred while retracting
            if (pickupPlayer instanceof ServerPlayerEntity serverPlayer && ((PlayerEntityGrappleAccess) serverPlayer).klaxon$isRetracting()) {
                // update usage lockout if true
                KlaxonServerPlayNetworkHandler.send(serverPlayer, new ItemUsageLockoutTrigger());
            }

            // if we're on the server, update the grapple winch's components to include this one
            if (!world.isClient()) {
                winchStack.set(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.of(this.getItemStack()));
            }

            // play sounds and emit game event
            world.playSound(
                    null,
                    pickupPlayer.getX(),
                    pickupPlayer.getEyeY(),
                    pickupPlayer.getZ(),
                    KlaxonSoundEvents.ITEM_GRAPPLE_WINCH_FAST_LOAD,
                    SoundCategory.PLAYERS,
                    0.7f + world.getRandom().nextFloat() * 0.3f,
                    0.7f + world.getRandom().nextFloat() * 0.3f
            );
            world.emitGameEvent(
                    GameEvent.ENTITY_ACTION,
                    pickupPlayer.getEyePos(),
                    GameEvent.Emitter.of(pickupPlayer)
            );

            this.discard();
            this.detachCable(true);
            return true;
        }

        return false;
    }

    @Override
    protected boolean tryPickup(PlayerEntity pickupPlayer) {
        PlayerEntityGrappleAccess access = (PlayerEntityGrappleAccess) pickupPlayer;

        boolean isAttachedToPickupPlayer = this.equals(access.klaxon$getGrappleClaw());

        // don't allow players to pick up attached grapple claws that aren't theirs
        if (this.isWinchCableAttached && !isAttachedToPickupPlayer) {
            return false;
        }

        // if we're allowed to be picked up by this player, only return false if this was handled by fast loading!
        if (super.tryPickup(pickupPlayer)) {
            return !this.tryFastReload(pickupPlayer);
        }

        // if all else failed, we can't be picked up - return false
        return false;
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

        // counts how many blocks we've broken - used to increment stat at the end
        int blocksBroken = 0;

        for (int x = 0; x < radius; x++) {
            ArrayList<BlockPos> newTargetPositions = new ArrayList<>();

            // iterate through the current target positions
            for (BlockPos newOriginPos : targetPositions) {
                // iterate through all offset directions from the checking pos
                for (Offset offset : Offset.values()) {
                    BlockPos targetPos = newOriginPos.add(offset.getOffsetVector());
                    BlockState targetState = world.getBlockState(targetPos);

                    // make sure we haven't processed position before
                    if (!processedPositions.contains(targetPos) && targetState.isOf(veinminedBlock)) {
                        // condense dropped stacks so we don't get 5 billion item entities
                        for (ItemStack droppedStack : world.getBlockState(targetPos).getDroppedStacks(lootContextBuilder)) {
                            KlaxonItemStackHelper.insertAndMerge(outputStacks, droppedStack);
                        }

                        BlockEntity blockEntity = world.getBlockEntity(targetPos);
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

        if (this.canBreakBlock(world, targetState, targetPos)) {

            // don't break blocks on clientside
            if (!world.isClient()) {
                BlockEntity blockEntity = world.getBlockEntity(targetPos);
                world.breakBlock(targetPos, true, owner);

                if (owner instanceof ServerPlayerEntity serverPlayer) {
                    serverPlayer.incrementStat(Stats.MINED.getOrCreateStat(targetState.getBlock()));
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

            // if we detach while grappled onto an entity, take on its velocity
            Entity grappledEntity = this.grappledEntity;
            if (grappledEntity != null) {
                this.grappledEntity = null;
                this.setVelocity(grappledEntity.getVelocity());
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
            boolean bl = itemStack.getItem() instanceof GrappleWinchItem grappleWinch && grappleWinch.canSupportCable(itemStack);
            boolean bl2 = itemStack2.getItem() instanceof GrappleWinchItem grappleWinch && grappleWinch.canSupportCable(itemStack2);

            boolean cableTooLong = this.getPos().distanceTo(serverPlayer.getEyePos()) > serverPlayer.getAttributeValue(KlaxonEntityAttributes.WINCH_CABLE_LENGTH) * 1.5f;

            if (!bl && !bl2) {
                this.detachCable(false);
                KlaxonAdvancementTriggers.triggerGrappleWinchIntentionallyDisconnectCable(serverPlayer);
            } else if (serverPlayer.isRemoved() || serverPlayer.isSpectator() || !serverPlayer.isAlive() || !serverPlayer.getWorld().equals(this.getWorld()) || cableTooLong) {
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

        if (attachedPlayer != null) {
            attachedPlayer.playSound(
                    soundEvent,
                    volume,
                    pitch
            );
        }

        world.playSound(
                attachedPlayer,
                this.getX(),
                this.getY(),
                this.getZ(),
                soundEvent,
                category,
                volume,
                pitch
        );
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