package net.myriantics.klaxon.entity.entities.grapple_claw;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.GrappleWinchItem;
import net.myriantics.klaxon.networking.KlaxonServerPlayNetworkHandler;
import net.myriantics.klaxon.networking.s2c.ItemUsageLockoutTrigger;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementTriggers;
import net.myriantics.klaxon.registry.entity.KlaxonEntityTypes;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.registry.misc.KlaxonNBTIds;
import net.myriantics.klaxon.registry.misc.KlaxonSoundEvents;
import net.myriantics.klaxon.tag.klaxon.KlaxonDamageTypeTags;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.GrappleWinchNetworkUtil;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.PlayerEntityGrappleAccess;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.function.Consumer;

public class GrappleClawEntity extends PersistentProjectileEntity {

    protected static final TrackedData<Integer> HOOKED_ENTITY_ID = DataTracker.registerData(GrappleClawEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final int HIT_INVINCIBILITY_TICKS = 5;

    private int ticksSinceDamaged = 0;

    protected final HashSet<ItemEntity> draggedItems = new HashSet<>();

    public final GrappleClawCableAttachmentHandler cableAttachmentHandler = new GrappleClawCableAttachmentHandler(this);
    public final GrappleClawHookedEntityHandler hookedEntityHandler = new GrappleClawHookedEntityHandler(this);
    public final GrappleClawBlockDestructionHandler blockDestructionHandler = new GrappleClawBlockDestructionHandler(this);

    public GrappleClawEntity(EntityType<? extends GrappleClawEntity> entityType, World world) {
        super(entityType, world);
    }

    public GrappleClawEntity(World world, double x, double y, double z, ItemStack stack, @Nullable ItemStack shotFrom) {
        super(KlaxonEntityTypes.STEEL_GRAPPLE_CLAW, x, y, z, world, stack, shotFrom);
    }

    public GrappleClawEntity(World world, LivingEntity owner, ItemStack stack, @Nullable ItemStack shotFrom) {
        super(KlaxonEntityTypes.STEEL_GRAPPLE_CLAW, owner, world, stack, shotFrom);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(HOOKED_ENTITY_ID, 0);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        this.hookedEntityHandler.onTrackedDataSet(data);
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return new ItemStack(KlaxonItems.STEEL_GRAPPLE_CLAW);
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        ItemStack handStack = player.getStackInHand(hand);

        if (this.isCableAttached() || this.equals(((PlayerEntityGrappleAccess)player).klaxon$getGrappleClaw())) {
            // attempt to detach grapple winch cable if shears are used on it
            if (handStack.isIn(KlaxonItemTags.GRAPPLE_WINCH_CABLE_DETACHERS)) {
                if (player instanceof ServerPlayerEntity) {
                    return this.cableAttachmentHandler.detachCable(false) ? ActionResult.SUCCESS : ActionResult.PASS;
                }

                return ActionResult.SUCCESS;
            }
        } else {
            // attempt to pick up / load the attached grapple claw
            // if that fails, just pick self up and discard
            if (!this.tryFastReload(player, player.getStackInHand(hand))) {
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
            if (weaponStack != null && attacker instanceof PlayerEntity player && (!this.isCableAttached() || player.equals(attachedPlayer)) && this.tryFastReload(player, weaponStack)) {
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
        return !this.hookedEntityHandler.hasHookedEntity();
    }

    @Override
    public boolean isAttackable() {
        return !this.hookedEntityHandler.hasHookedEntity();
    }

    public boolean isAnchored() {
        return this.inGround || this.hookedEntityHandler.isHookedEntityHeavy();
    }

    @Override
    public float getTargetingMargin() {
        return 0.0f;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity hitEntity = entityHitResult.getEntity();
        @Nullable PlayerEntity attachedPlayer = this.getAttachedPlayer();

        // if we're already attached to an entity, don't process further
        if (this.hookedEntityHandler.hasHookedEntity()) {
            return;
        }

        // check that we're attached to a cable
        if (this.isCableAttached()) {
            // if we hit the attached player, attempt to fast reload
            if (hitEntity.equals(attachedPlayer)) {
                // attempt to pickup items into attached player when hitting
                if (!this.draggedItems.isEmpty()) {
                    for (ItemEntity itemEntity : this.draggedItems) {
                        itemEntity.onPlayerCollision(attachedPlayer);
                    }
                }

                if (!(this.tryFastReload(attachedPlayer, attachedPlayer.getMainHandStack()) || this.tryFastReload(attachedPlayer, attachedPlayer.getOffHandStack()))) {
                    // if we can't be picked up, bonk all velocity
                    setVelocity(Vec3d.ZERO);
                }
            } else {
                // attempt to hook into entity
                this.hookedEntityHandler.hookEntity(hitEntity);
            }
        } else {
            this.hookedEntityHandler.snapClawToHookPos(hitEntity);
            super.onEntityHit(entityHitResult);
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        // prep variables
        World world = getWorld();
        BlockPos hitPos = blockHitResult.getBlockPos();
        BlockState hitState = getWorld().getBlockState(hitPos);
        PlayerEntity attachedPlayer = this.getAttachedPlayer();

        if (this.blockDestructionHandler.tryBreakingBlocks(world, hitState, hitPos)) {
            this.setVelocity(this.getVelocity().multiply(0.85));
        } else {
            // if a block was broken, we don't call the super method
            super.onBlockHit(blockHitResult);

            this.draggedItems.clear();

            if (attachedPlayer != null) {
                if (this.isCableAttached()) {
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

    @Override
    public void tick() {
        // update damage reset ticker
        ticksSinceDamaged++;

        @Nullable PlayerEntity attachedPlayer = this.getAttachedPlayer();
        World world = this.getWorld();

        if (attachedPlayer != null) {
            PlayerEntityGrappleAccess access = (PlayerEntityGrappleAccess) attachedPlayer;

            boolean retracting = access.klaxon$isRetracting();

            this.cableAttachmentHandler.tick(world, attachedPlayer, retracting);

            if (!world.isClient()) {
                // collect item entities and update their velocity & position
                if (retracting) {
                    // yonk nearby entities and add to list
                    this.draggedItems.addAll(world.getEntitiesByType(
                            TypeFilter.instanceOf(ItemEntity.class),
                            this.getBoundingBox().expand(this.getHeight()),
                            (entity) -> !entity.isRemoved()
                    ));

                    // purge removed item entities
                    this.draggedItems.removeIf(Entity::isRemoved);

                    // update pos of item entities
                    for (ItemEntity itemEntity : this.draggedItems) {
                        itemEntity.setPosition(this.getPos());
                    }
                }
            }
        }

        // only tick if we're not attached to an entity
        if (!this.hookedEntityHandler.hasHookedEntity()) {
            super.tick();
        } else {
            this.hookedEntityHandler.snapClawToHookPos();
        }

        // sync to clients if attached and not in ground
        if (!this.inGround && this.getAttachedPlayer() instanceof ServerPlayerEntity serverPlayer) {
            GrappleWinchNetworkUtil.syncToClients(serverPlayer, this);
        }
    }

    @Override
    protected void age() {
        // only age up if in ground and disconnected
        if (inGround && !this.isCableAttached()) {
            super.age();
        }
    }

    /**
     * Attempt to perform a fast-reloading operation. Plays a sound, emits game event, discards self, and detaches grapple cable if successful.
     * @param pickupPlayer - Player that is attempting to fast-reload this Grapple Claw into their Grapple Winch
     * @param winchStack - Stack that we're attempting to load into
     * @return Whether the fast loading succeeded or not
     */
    public boolean tryFastReload(PlayerEntity pickupPlayer, ItemStack winchStack) {
        World world = pickupPlayer.getWorld();

        // check if the winch stack is a grapple winch
        if (!(winchStack.getItem() instanceof GrappleWinchItem)) {
            return false;
        }

        ChargedProjectilesComponent projectiles = winchStack.getOrDefault(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.DEFAULT);

        // make sure that the grapple winch is empty and that we're either unattached or being loaded into the attached player's grapple winch
        if (projectiles.isEmpty() && this.isAttachedToPlayer(pickupPlayer)) {
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
            this.cableAttachmentHandler.detachCable(true);
            return true;
        }

        return false;
    }

    @Override
    protected boolean tryPickup(PlayerEntity pickupPlayer) {
        PlayerEntityGrappleAccess access = (PlayerEntityGrappleAccess) pickupPlayer;

        boolean isAttachedToPickupPlayer = this.equals(access.klaxon$getGrappleClaw());

        // don't allow players to pick up attached grapple claws that aren't theirs
        if (this.isCableAttached()) {
            if (isAttachedToPickupPlayer) {
                BlockPos steppingPos = pickupPlayer.getSteppingPos();
                BlockPos anchoredPos = this.getBlockPos();

                // don't pick up grapple claw while you're being supported by it
                if ((!pickupPlayer.isOnGround() && anchoredPos.getY() > steppingPos.getY())) {
                    return false;
                }
            } else {
                return false;
            }
        }

        // if we're allowed to be picked up by this player, only return false if this was handled by fast loading!
        if (super.tryPickup(pickupPlayer)) {
            return !this.isCableAttached() || !(this.tryFastReload(pickupPlayer, pickupPlayer.getMainHandStack()) || this.tryFastReload(pickupPlayer, pickupPlayer.getOffHandStack()));
        }

        // if all else failed, we can't be picked up - return false
        return false;
    }

    public @Nullable PlayerEntity getAttachedPlayer() {
        // if there's no cable attached, return null.
        if (!this.isCableAttached()) {
            return null;
        }

        // if the grapple winch cable is attached, the claw should be able to trust that its owner is the wielding player
        // if it's not, throw an error
        switch (this.getOwner()) {
            case PlayerEntity player -> {
                return player;
            }
            case null -> {
                return null;
            }
            default -> throw new IllegalStateException("Grapple Winch Cable is attached, however it's owner " + this.getOwner() + " is not a PlayerEntity!");
        }
    }

    public boolean deAnchorIfPossible(Vec3d deAnchoringDirection) {
        boolean success = false;
        if (this.inGround) {
            HitResult hitResult = this.getWorld().raycast(new RaycastContext(
                    this.getPos(),
                    this.getPos().add(deAnchoringDirection.normalize().multiply(0.05)),
                    RaycastContext.ShapeType.COLLIDER,
                    RaycastContext.FluidHandling.NONE,
                    this
            ));

            if (!hitResult.getType().equals(HitResult.Type.BLOCK)) {
                this.inGround = false;
                success = true;
            }
        } else if (this.hookedEntityHandler.isHookedEntityHeavy()) {
            this.hookedEntityHandler.releaseHookedEntity();
        }

        if (success) {
            KlaxonAdvancementTriggers.triggerGrappleWinchDeAnchorGrappleClaw((ServerPlayerEntity) this.getAttachedPlayer());
        }

        return success;
    }

    public boolean attachCable(ServerPlayerEntity serverPlayer) {
        return this.cableAttachmentHandler.attachCable(serverPlayer);
    }

    public boolean isCableAttached() {
        return this.cableAttachmentHandler.isCableAttached();
    }

    public boolean isAttachedToPlayer(PlayerEntity player) {
        @Nullable PlayerEntity attachedPlayer = this.getAttachedPlayer();
        return attachedPlayer != null && attachedPlayer.equals(player) && this.equals(((PlayerEntityGrappleAccess) player).klaxon$getGrappleClaw());
    }

    protected void playSoundAtSelfAndThroughCableIfPossible(
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

        if (nbt.contains(KlaxonNBTIds.TICKS_SINCE_DAMAGED)) {
            ticksSinceDamaged = nbt.getInt(KlaxonNBTIds.TICKS_SINCE_DAMAGED);
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);

        nbt.putInt(KlaxonNBTIds.TICKS_SINCE_DAMAGED, ticksSinceDamaged);
    }

    @Override
    public void remove(RemovalReason reason) {
        if (!reason.equals(RemovalReason.UNLOADED_WITH_PLAYER)) {
            this.cableAttachmentHandler.detachCable(true);
        }
        if (this.hookedEntityHandler.hasHookedEntity()) {
            this.hookedEntityHandler.releaseHookedEntity();
        }
        super.remove(reason);
    }
}