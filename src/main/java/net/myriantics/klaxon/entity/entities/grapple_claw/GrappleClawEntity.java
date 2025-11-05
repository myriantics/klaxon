package net.myriantics.klaxon.entity.entities.grapple_claw;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.entity.*;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.myriantics.klaxon.component.configuration.GrappleClawComponent;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.GrappleWinchItem;
import net.myriantics.klaxon.mechanics.entity_weight.EntityWeightHelper;
import net.myriantics.klaxon.mechanics.grapple_winch.AttachedGrappleClawContainer;
import net.myriantics.klaxon.mechanics.grapple_winch.EntityGrappleClawContainerAccess;
import net.myriantics.klaxon.mixin.minecraft.grapple_winch.grapple_claw.EnderDragonEntityAccessor;
import net.myriantics.klaxon.networking.KlaxonServerPlayNetworkHandler;
import net.myriantics.klaxon.networking.s2c.ItemUsageLockoutTrigger;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementTriggers;
import net.myriantics.klaxon.registry.dynamic.KlaxonDamageTypes;
import net.myriantics.klaxon.registry.entity.KlaxonEntityAttributes;
import net.myriantics.klaxon.registry.entity.KlaxonEntityTypes;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.registry.misc.KlaxonNBTIds;
import net.myriantics.klaxon.registry.misc.KlaxonSoundEvents;
import net.myriantics.klaxon.tag.klaxon.KlaxonDamageTypeTags;
import net.myriantics.klaxon.tag.klaxon.KlaxonEntityTypeTags;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.GrappleWinchNetworkUtil;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.PlayerEntityGrappleAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public class GrappleClawEntity extends PersistentProjectileEntity {

    protected static final TrackedData<Integer> HOOKED_ENTITY_ID = DataTracker.registerData(GrappleClawEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final int HIT_INVINCIBILITY_TICKS = 5;

    private int ticksSinceDamaged = 0;
    public final HookedEntityContainer hookedEntityContainer = new HookedEntityContainer();
    public final CableAttachmentHandler cableAttachmentHandler = new CableAttachmentHandler();

    protected final HashSet<ItemEntity> draggedItems = new HashSet<>();

    private DamageSource lastTransmittedDamageSource = null;

    public GrappleClawEntity(EntityType<? extends GrappleClawEntity> entityType, World world) {
        super(entityType, world);
    }

    public GrappleClawEntity(World world, double x, double y, double z, ItemStack stack, @Nullable ItemStack shotFrom) {
        super(KlaxonEntityTypes.GRAPPLE_CLAW, x, y, z, world, stack, shotFrom);
    }

    public GrappleClawEntity(World world, LivingEntity owner, ItemStack stack, @Nullable ItemStack shotFrom) {
        super(KlaxonEntityTypes.GRAPPLE_CLAW, owner, world, stack, shotFrom);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(HOOKED_ENTITY_ID, 0);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        this.hookedEntityContainer.onTrackedDataSet(data);
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return new ItemStack(KlaxonItems.STEEL_GRAPPLE_CLAW);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        return super.isInvulnerableTo(damageSource) || damageSource.isIn(DamageTypeTags.BYPASSES_ARMOR) || damageSource.isIn(KlaxonDamageTypeTags.GRAPPLE_WINCH_CABLE_TRANSMISSIBLE);
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        ItemStack handStack = player.getStackInHand(hand);

        if (this.isCableAttached() || this.equals(((PlayerEntityGrappleAccess)player).klaxon$getGrappleClaw())) {
            // attempt to detach grapple winch cable if shears are used on it
            if (handStack.isIn(KlaxonItemTags.GRAPPLE_WINCH_CABLE_DETACHERS)) {
                if (player instanceof ServerPlayerEntity) {
                    this.cableAttachmentHandler.detach(CableDetachmentReason.MANUAL_DISCONNECT);
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
    public boolean damage(DamageSource source, float amount) {
        World world = this.getWorld();
        @Nullable PlayerEntity attachedPlayer = this.cableAttachmentHandler.getAttachedPlayer();

        // try to conduct electrical damage if possible
        this.tryConductElectricalDamage(this, source, amount);

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

    @Override
    public void kill() {
        if (this.pickupType.equals(PickupPermission.ALLOWED)) {
            this.dropStack(getItemStack());
        }
        super.kill();
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
        return !this.hookedEntityContainer.isPresent();
    }

    @Override
    public boolean isAttackable() {
        return !this.hookedEntityContainer.isPresent();
    }

    public boolean isAnchored() {
        return this.inGround || this.hookedEntityContainer.isHeavy();
    }

    @Override
    public float getTargetingMargin() {
        return 0.0f;
    }

    @Override
    public void onStruckByLightning(ServerWorld world, LightningEntity lightning) {
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity hitEntity = entityHitResult.getEntity();
        @Nullable PlayerEntity attachedPlayer = this.cableAttachmentHandler.getAttachedPlayer();

        // if we're already attached to an entity, don't process further
        if (this.hookedEntityContainer.isPresent()) {
            return;
        }

        // check that we're attached to a cable and that the target entity can be hooked
        if (this.isCableAttached() && this.hookedEntityContainer.canHookEntity(hitEntity)) {
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
                this.hookedEntityContainer.hook(hitEntity);
            }
        } else {
            this.hookedEntityContainer.snapClawToHookPos(hitEntity);
            //TODO: Replace the super call here with logic like TridentEntity - so it rebounds off of entities instead of attaching an arrow
            super.onEntityHit(entityHitResult);
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        // this needs to be called BEFORE we it the ground, so isAnchored returns true when we sync data to the client :)
        super.onBlockHit(blockHitResult);

        // prep variables
        PlayerEntity attachedPlayer = this.cableAttachmentHandler.getAttachedPlayer();
        if (attachedPlayer != null) {
            this.playSoundAtSelfAndThroughCableIfPossible(
                    KlaxonSoundEvents.ENTITY_GRAPPLE_CLAW_ANCHOR,
                    1.0F,
                    1.0F / (getWorld().getRandom().nextFloat() * 0.4F + 1.2F)
            );

            // needs to be here to let client know about grapple claw coords if it lands outside client render distance
            if (attachedPlayer instanceof ServerPlayerEntity serverPlayer) {
                GrappleWinchNetworkUtil.syncToClients(serverPlayer, this);
            }
        }

        this.draggedItems.clear();
    }

    @Override
    protected SoundEvent getHitSound() {
        return KlaxonSoundEvents.ENTITY_GRAPPLE_CLAW_ANCHOR;
    }

    @Override
    public void tick() {
        // update damage reset ticker
        ticksSinceDamaged++;

        @Nullable PlayerEntity attachedPlayer = this.cableAttachmentHandler.getAttachedPlayer();
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
        if (!this.hookedEntityContainer.isPresent()) {
            super.tick();
        } else {
            this.hookedEntityContainer.snapClawToHookPos();
        }

        // sync to clients if attached and not in ground
        if (this.cableAttachmentHandler.getAttachedPlayer() instanceof ServerPlayerEntity serverPlayer) {
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

    public Entity[] conductLightningEffects(ServerWorld serverWorld, List<Entity> struckEntities, LightningEntity lightningEntity) {
        @Nullable PlayerEntity attachedPlayer = this.cableAttachmentHandler.getAttachedPlayer();
        @Nullable Entity hookedEntity = this.hookedEntityContainer.get();

        Entity[] conductionTargets = new Entity[] {this, attachedPlayer, hookedEntity};

        for (int i = 0; i < conductionTargets.length; i++) {
            Entity target = conductionTargets[i];

            if (target == null || struckEntities.contains(target)) {
                conductionTargets[i] = null;
            } else {
                target.onStruckByLightning(serverWorld, lightningEntity);
            }
        }

        // Returned array is used to count these entities towards channeling advancement
        // Top 10 things people will notice ... unless this caused a crash or weird issue ... then my bad haha i thought channeling lightning onto yourself while attached to a villager should proc the advancement
        return conductionTargets;
    }

    public void tryConductElectricalDamage(Entity originEntity, DamageSource damageSource, float amount) {
        if (this.getWorld().isClient() || !this.isCableAttached() || damageSource == lastTransmittedDamageSource || !damageSource.isIn(KlaxonDamageTypeTags.GRAPPLE_WINCH_CABLE_TRANSMISSIBLE)) {
            return;
        }

        @Nullable PlayerEntity attachedPlayer = this.cableAttachmentHandler.getAttachedPlayer();
        @Nullable Entity hookedEntity = this.hookedEntityContainer.get();

        for (Entity entity : new Entity[]{this, attachedPlayer, hookedEntity}) {
            if (entity == null || entity.equals(originEntity)) {
                continue;
            }

            entity.damage(damageSource, amount);
        }

        this.lastTransmittedDamageSource = damageSource;
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
        if (projectiles.isEmpty() && (!this.isCableAttached() || this.isAttachedToPlayer(pickupPlayer))) {
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

            if (!this.getWorld().isClient()) {
                this.discard();
                this.cableAttachmentHandler.detach(CableDetachmentReason.FAST_RELOADED);
            }

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

    public void deAnchorIfPossible(Vec3d deAnchoringDirection) {
        if (this.getWorld().isClient()) {
            return;
        }

        boolean success = false;
        if (this.inGround) {
            BlockHitResult hitResult = GrappleClawBlockDestructionHelper.raycast(
                    this,
                    this.getPos(),
                    this.getPos().add(deAnchoringDirection),
                    false
            );

            // make sure it's either a miss or we've got space to move a significant amount
            if (hitResult.getType().equals(HitResult.Type.MISS) || hitResult.getPos().distanceTo(this.getPos()) > 0.1) {
                this.inGround = false;
                success = true;
            }
        } else if (this.hookedEntityContainer.isHeavy()) {
            success = this.hookedEntityContainer.release(true);
        }

        if (success) {
            KlaxonAdvancementTriggers.triggerGrappleWinchDeAnchorGrappleClaw((ServerPlayerEntity) this.cableAttachmentHandler.getAttachedPlayer());
        }
    }

    public void attachCable(ServerPlayerEntity serverPlayer) {
        this.cableAttachmentHandler.attach(serverPlayer);
    }

    public boolean isCableAttached() {
        return this.cableAttachmentHandler.isAttached();
    }

    public boolean isAttachedToPlayer(PlayerEntity player) {
        @Nullable PlayerEntity attachedPlayer = this.cableAttachmentHandler.getAttachedPlayer();
        return attachedPlayer != null && attachedPlayer.equals(player) && this.equals(((PlayerEntityGrappleAccess) player).klaxon$getGrappleClaw());
    }

    @Override
    public SoundCategory getSoundCategory() {
        return this.isCableAttached() ? SoundCategory.PLAYERS : super.getSoundCategory();
    }

    protected void playSoundAtSelfAndThroughCableIfPossible(
            SoundEvent soundEvent,
            float volume,
            float pitch
    ) {
        PlayerEntity attachedPlayer = this.cableAttachmentHandler.getAttachedPlayer();

        if (attachedPlayer != null && attachedPlayer.getEyePos().distanceTo(this.getPos()) > 15) {
            attachedPlayer.playSound(
                    soundEvent,
                    volume,
                    pitch
            );
        }

        this.playSound(
                soundEvent,
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
            this.cableAttachmentHandler.detach(CableDetachmentReason.PLAYER_REMOVED);
        }
        this.hookedEntityContainer.release(false);
        super.remove(reason);
    }

    public class HookedEntityContainer {
        private Entity hookedEntity = null;

        private HookedEntityContainer() {
        }

        public void onTrackedDataSet(TrackedData<?> trackedData) {
            if (!HOOKED_ENTITY_ID.equals(trackedData)) {
                return;
            }

            int id = GrappleClawEntity.this.getDataTracker().get(GrappleClawEntity.HOOKED_ENTITY_ID);

            // id is offset by one to allow for entities with an id of 0
            // was initially confused by this when i saw it in the fishing bobber entity so im dropping this explanation here for myself or whatever future person reads this
            if (id < 0) {
                this.setHookedEntity(null);
            } else {
                Entity entity = GrappleClawEntity.this.getWorld().getEntityById(id - 1);
                switch (entity) {
                    case EnderDragonEntityAccessor access -> this.setHookedEntity(access.getBody());
                    case null, default -> this.setHookedEntity(entity);
                }
            }
        }

        public void tick(Vec3d compiledVec) {
            if (!this.isPresent()) {
                return;
            }

            // clear grappled entity if it was removed
            if (this.hookedEntity.isRemoved() || !this.hookedEntity.isAlive()) {
                this.release(false);
                return;
            } else {
                this.hookedEntity.limitFallDistance();
            }

            if (Objects.requireNonNull(this.hookedEntity) instanceof PlayerEntity player) {
                if (player instanceof ClientPlayerEntity) {
                    player.addVelocity(compiledVec);
                }
            } else {
                this.hookedEntity.addVelocity(compiledVec);
            }
        }

        private void setHookedEntity(Entity entity) {
            if (entity == null) {
                if (this.hookedEntity != null) {
                    Optional.ofNullable(((EntityGrappleClawContainerAccess) this.hookedEntity).klaxon$get()).ifPresent(AttachedGrappleClawContainer::clear);
                }
            } else {
                ((EntityGrappleClawContainerAccess) entity).klaxon$get().setGrappleClaw(GrappleClawEntity.this);
            }

            switch (entity) {
                case EnderDragonPart part -> {
                    GrappleClawEntity.this.getDataTracker().set(GrappleClawEntity.HOOKED_ENTITY_ID, part.owner.getId() + 1);
                }
                case null, default -> {
                    GrappleClawEntity.this.getDataTracker().set(GrappleClawEntity.HOOKED_ENTITY_ID, entity == null ? 0 : entity.getId() + 1);
                }
            }

            this.hookedEntity = entity;
        }

        public void hook(Entity entity) {
            // this is only for the server
            if (GrappleClawEntity.this.getWorld().isClient()) {
                return;
            }

            GrappleClawEntity claw = GrappleClawEntity.this;

            // hook onto the ender dragon body if possible
            entity = entity instanceof EnderDragonPart part
                    ? ((EnderDragonEntityAccessor) part.owner).getBody()
                    : entity;

            // hook onto entity
            this.setHookedEntity(entity);

            // update position and velocity
            this.snapClawToHookPos();

            // pop advancement
            if (claw.cableAttachmentHandler.getAttachedPlayer() instanceof ServerPlayerEntity serverPlayer) {
                KlaxonAdvancementTriggers.triggerEntityGrapple(serverPlayer, entity);
            }

            // damage entity
            entity.damage(
                    claw.getDamageSources().create(
                            KlaxonDamageTypes.GRAPPLING,
                            claw,
                            claw.getOwner() == null ? claw : claw.getOwner()
                    ),
                    claw.getItemStack().getOrDefault(
                            KlaxonDataComponentTypes.GRAPPLE_CLAW_COMPONENT,
                            GrappleClawComponent.DEFAULT
                    ).computeGrappling(claw.getItemStack())
            );

            claw.playSoundAtSelfAndThroughCableIfPossible(
                    KlaxonSoundEvents.ENTITY_GRAPPLE_CLAW_ANCHOR,
                    1.0F,
                    1.0F / (claw.getWorld().getRandom().nextFloat() * 0.4F + 1.2F)
            );

            claw.draggedItems.clear();
        }

        public boolean release(boolean damage) {
            if (!this.isPresent()) {
                return false;
            }

            GrappleClawEntity claw = GrappleClawEntity.this;

            claw.setVelocity(hookedEntity.getVelocity());
            if (damage) {
                hookedEntity.damage(
                        claw.getWorld().getDamageSources().create(
                                KlaxonDamageTypes.RENDING,
                                claw,
                                claw.getOwner() == null ? claw : claw.getOwner()
                        ),
                        claw.getItemStack().getOrDefault(
                                KlaxonDataComponentTypes.GRAPPLE_CLAW_COMPONENT,
                                GrappleClawComponent.DEFAULT
                        ).computeRending(claw.getItemStack())
                );
            }

            this.setHookedEntity(null);

            return true;
        }

        public boolean canHookEntity(Entity entity) {
            if (entity == null || this.isPresent()) {
                return false;
            }

            if (((EntityGrappleClawContainerAccess) entity).klaxon$get().isPresent()) {
                return false;
            }

            return entity.canBeHitByProjectile() && !entity.getType().isIn(KlaxonEntityTypeTags.GRAPPLE_CLAW_HOOKING_DENYLIST);
        }

        public void snapClawToHookPos() {
            this.snapClawToHookPos(this.hookedEntity);
        }

        public void snapClawToHookPos(Entity target) {
            Vec3d targetPos = target instanceof EnderDragonPart
                    ? target.getPos().add(0, target.getHeight() / 2, 0)
                    : target.getEyePos();

            GrappleClawEntity grappleClaw = GrappleClawEntity.this;

            grappleClaw.setVelocity(Vec3d.ZERO);
            grappleClaw.setPosition(targetPos.subtract(0, grappleClaw.getHeight() / 2, 0));
        }

        public boolean matches(Entity entity) {
            return this.hookedEntity == entity;
        }

        public boolean isHeavy() {
            return this.isPresent() && EntityWeightHelper.isHeavy(this.hookedEntity);
        }

        public boolean isPresent() {
            return hookedEntity != null;
        }

        public Entity get() {
            return this.hookedEntity;
        }
    }

    public class CableAttachmentHandler {
        private boolean attached = false;
        private boolean canPlayReboundSound = true;

        private CableAttachmentHandler() {
        }

        protected void tick(World world, @NotNull PlayerEntity attachedPlayer, boolean retracting) {
            CableDetachmentReason reason = this.testValidity();
            if (reason != null) {
                this.detach(reason);
                return;
            }

            GrappleClawEntity grappleClaw = GrappleClawEntity.this;

            Vec3d compiledVec = Vec3d.ZERO;

            Vec3d clawPos = grappleClaw.getPos();
            Vec3d attachedEyePos = attachedPlayer.getEyePos();
            Vec3d normalizedClaw2WielderVec = attachedEyePos.subtract(clawPos).normalize();

            double ownerDistance = clawPos.distanceTo(attachedEyePos);
            double currentWinchCableLength = attachedPlayer.getAttributeValue(KlaxonEntityAttributes.WINCH_CABLE_LENGTH);

            // limit fall distance to give players more leeway
            if (attachedPlayer.getVelocity().getY() > -1 && attachedPlayer.fallDistance > 1.0F) {
                attachedPlayer.fallDistance = 1.0F;
            }

            // if the attached player is heavy and retracting, de-anchor and pop advancement if succeeded
            if (!world.isClient() && retracting && EntityWeightHelper.isHeavy(attachedPlayer)) {
                grappleClaw.deAnchorIfPossible(normalizedClaw2WielderVec);
            }

            // owner being heavy overrides anchoring
            if (!grappleClaw.isAnchored()) {

                // retract grapple claw if owner pulls back before landing
                if (retracting) {
                    compiledVec = compiledVec.add(normalizedClaw2WielderVec.multiply(4f/20));
                }

                // retract grapple claw if it hits limit
                if (ownerDistance >= currentWinchCableLength) {

                    if (ownerDistance >= currentWinchCableLength * 1.2) {
                        grappleClaw.setVelocity(grappleClaw.getVelocity().multiply(0.85));
                    }

                    compiledVec = compiledVec.add(normalizedClaw2WielderVec.multiply(4f/20));

                    // make sure we don't spam the shit out of the rebound sound
                    if (this.canPlayReboundSound) {
                        grappleClaw.playSoundAtSelfAndThroughCableIfPossible(
                                KlaxonSoundEvents.ENTITY_GRAPPLE_CLAW_REBOUND_AT_LIMIT,
                                1f + world.getRandom().nextFloat() * 0.3f,
                                0.8f + world.getRandom().nextFloat() * 0.2f
                        );
                        world.emitGameEvent(
                                GameEvent.ENTITY_ACTION,
                                clawPos,
                                GameEvent.Emitter.of(attachedPlayer)
                        );

                        this.canPlayReboundSound = false;
                    }
                } else if (ownerDistance < currentWinchCableLength * 0.95) {
                    // if we go back in bounds, we can play the rebound sound again
                    // this has a small deadzone because otherwise it would spam the shit out of the sound when dangling at the end of the cable.
                    this.canPlayReboundSound = true;
                }
            }

            // commit the total velocity edits to self or whatever entity we're attached to
            if (grappleClaw.hookedEntityContainer.isPresent()) {
                grappleClaw.hookedEntityContainer.tick(compiledVec);
            } else {
                grappleClaw.addVelocity(compiledVec);
            }
        }

        public boolean canAttach(ServerPlayerEntity serverPlayer) {
            return !GrappleClawEntity.this.isRemoved() && !serverPlayer.equals(this.getAttachedPlayer());
        }

        public void setAttached(boolean attached) {
            this.attached = attached;
        }

        public void attach(ServerPlayerEntity serverPlayer) {
            if (this.canAttach(serverPlayer)) {
                GrappleClawEntity claw = GrappleClawEntity.this;

                ((PlayerEntityGrappleAccess) serverPlayer).klaxon$setGrappleClaw(claw);
                claw.setOwner(serverPlayer);
                this.attached = true;
                GrappleWinchNetworkUtil.syncToClients(serverPlayer, claw);
            }
        }

        public void detach(CableDetachmentReason reason) {
            PlayerEntity attachedPlayer = this.getAttachedPlayer();
            GrappleClawEntity claw = GrappleClawEntity.this;

            if (attachedPlayer != null) {
                if (reason.playsDetachmentSound) {
                    claw.playSoundAtSelfAndThroughCableIfPossible(
                            KlaxonSoundEvents.ENTITY_GRAPPLE_CLAW_DETACH,
                            0.8f + claw.getWorld().getRandom().nextFloat() * 0.2f,
                            0.7f + claw.getWorld().getRandom().nextFloat() * 0.3f
                    );
                }

                ((PlayerEntityGrappleAccess) attachedPlayer).klaxon$setGrappleClaw(null);
                this.setAttached(false);

                if (attachedPlayer instanceof ServerPlayerEntity serverPlayer) {
                    GrappleWinchNetworkUtil.clearFromClients(serverPlayer, claw);
                    KlaxonAdvancementTriggers.triggerGrappleWinchIntentionallyDisconnectCable(serverPlayer, reason);
                    claw.hookedEntityContainer.release(false);
                    claw.draggedItems.clear();
                }
            }
        }

        public boolean isAttached() {
            return attached;
        }

        public @Nullable PlayerEntity getAttachedPlayer() {
            // if there's no cable attached, return null.
            if (!this.attached) {
                return null;
            }

            // if the grapple winch cable is attached, the claw should be able to trust that its owner is the wielding player
            // if it's not, throw an error
            switch (GrappleClawEntity.this.getOwner()) {
                case PlayerEntity player -> {
                    return player;
                }
                case null -> {
                    return null;
                }
                default -> throw new IllegalStateException("Grapple Winch Cable is attached, however it's owner " + GrappleClawEntity.this.getOwner() + " is not a PlayerEntity!");
            }
        }

        public @Nullable CableDetachmentReason testValidity() {
            PlayerEntity attachedPlayer = this.getAttachedPlayer();

            if (attachedPlayer.isRemoved()) {
                return CableDetachmentReason.PLAYER_REMOVED;
            }

            if (!attachedPlayer.isAlive()) {
                return CableDetachmentReason.PLAYER_DIED;
            }

            if (attachedPlayer.isSpectator()) {
                return CableDetachmentReason.PLAYER_SPECTATOR;
            }

            if (!attachedPlayer.getWorld().equals((GrappleClawEntity.this.getWorld()))) {
                return CableDetachmentReason.WORLD_MISMATCH;
            }

            ItemStack mainHandStack = attachedPlayer.getMainHandStack();
            ItemStack offHandStack = attachedPlayer.getOffHandStack();

            boolean mainHandValid = mainHandStack.getItem() instanceof GrappleWinchItem grappleWinch && grappleWinch.canSupportCable(mainHandStack);
            boolean offHandValid = offHandStack.getItem() instanceof GrappleWinchItem grappleWinch && grappleWinch.canSupportCable(offHandStack);

            if (!mainHandValid && !offHandValid) {
                return CableDetachmentReason.INVALID_HELD_ITEMS;
            }

            boolean cableTooLong = GrappleClawEntity.this.getPos().distanceTo(attachedPlayer.getEyePos()) > attachedPlayer.getAttributeValue(KlaxonEntityAttributes.WINCH_CABLE_LENGTH) * 1.5f;

            if (cableTooLong) {
                return CableDetachmentReason.CABLE_TOO_LONG;
            }

            // LGTM, continue :)
            return null;
        }
    }

    public enum CableDetachmentReason implements StringIdentifiable {
        INVALID_HELD_ITEMS(0, true),
        PLAYER_REMOVED(1, false),
        PLAYER_DIED(2, false),
        PLAYER_SPECTATOR(3, false),
        PLAYER_TELEPORTED(4, true),
        WORLD_MISMATCH(5, false),
        MANUAL_DISCONNECT(6, true),
        CABLE_TOO_LONG(7, true),
        FAST_RELOADED(8, false),
        PICKUP_RELOADED(9, false),
        PICKUP(10, false),
        GENERIC_DISCONNECT(11, false);

        public static final Codec<CableDetachmentReason> CODEC = StringIdentifiable.createCodec(CableDetachmentReason::values);
        public static final PacketCodec<ByteBuf, CableDetachmentReason> PACKET_CODEC = PacketCodecs.indexed(
                (index) -> CableDetachmentReason.values()[index],
                CableDetachmentReason::getIndex
        );

        private final int index;
        public final boolean playsDetachmentSound;

        CableDetachmentReason(int index, boolean playsDetachmentSound) {
            this.index = index;
            this.playsDetachmentSound = playsDetachmentSound;
        }

        public int getIndex() {
            return this.index;
        }

        @Override
        public String asString() {
            return this.name().toLowerCase();
        }
    }
}