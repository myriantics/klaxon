package net.myriantics.klaxon.entity.entities.grapple_claw;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.*;
import net.myriantics.klaxon.component.configuration.GrappleClawComponent;
import net.myriantics.klaxon.mechanics.entity_weight.EntityWeightHelper;
import net.myriantics.klaxon.mechanics.grapple_winch.CableDetachmentReason;
import net.myriantics.klaxon.mechanics.grapple_winch.GrapplingHook;
import net.myriantics.klaxon.mechanics.grapple_winch.connection.GrappleWinchConnection;
import net.myriantics.klaxon.mechanics.grapple_winch.connection.ServerGrappleWinchConnection;
import net.myriantics.klaxon.mechanics.grapple_winch.manager.GrappleWinchConnectionManager;
import net.myriantics.klaxon.mechanics.grapple_winch.manager.ServerGrappleWinchConnectionManager;
import net.myriantics.klaxon.mixin.minecraft.grapple_winch.grapple_claw.EnderDragonAccessor;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementTriggers;
import net.myriantics.klaxon.registry.dynamic.KlaxonDamageTypes;
import net.myriantics.klaxon.registry.entity.KlaxonEntityTypes;
import net.myriantics.klaxon.registry.entity.KlaxonTrackedDataHandlerRegistry;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.registry.misc.KlaxonSoundEvents;
import net.myriantics.klaxon.tag.klaxon.KlaxonEntityTypeTags;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;

public class GrappleClawEntity extends AbstractArrow implements GrapplingHook {

    protected static final EntityDataAccessor<Integer> HOOKED_ENTITY_ID = SynchedEntityData.defineId(GrappleClawEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<List<Integer>> DRAGGED_ITEM_IDS = SynchedEntityData.defineId(GrappleClawEntity.class, KlaxonTrackedDataHandlerRegistry.INT_LIST);

    private final HookedEntityContainer hookedEntityContainer = new HookedEntityContainer();
    public final DraggedItemsContainer draggedItemsContainer = new DraggedItemsContainer();

    public GrappleClawEntity(EntityType<? extends GrappleClawEntity> entityType, Level world) {
        super(entityType, world);
    }

    public GrappleClawEntity(Level world, double x, double y, double z, ItemStack stack, @Nullable ItemStack shotFrom) {
        super(KlaxonEntityTypes.GRAPPLE_CLAW.value(), x, y, z, world, stack, shotFrom);
    }

    public GrappleClawEntity(Level world, LivingEntity owner, ItemStack stack, @Nullable ItemStack shotFrom) {
        super(KlaxonEntityTypes.GRAPPLE_CLAW.value(), owner, world, stack, shotFrom);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HOOKED_ENTITY_ID, 0);
        builder.define(DRAGGED_ITEM_IDS, List.of());
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> data) {
        super.onSyncedDataUpdated(data);
        if (data.equals(HOOKED_ENTITY_ID)) {
            this.hookedEntityContainer.onTrackedDataSet(this.getEntityData().get(HOOKED_ENTITY_ID));
        }
        if (data.equals(DRAGGED_ITEM_IDS)) {
            this.draggedItemsContainer.onTrackedDataUpdate(this.getEntityData().get(DRAGGED_ITEM_IDS));
        }
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(KlaxonItems.STEEL_GRAPPLE_CLAW);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        return super.isInvulnerableTo(damageSource) || damageSource.is(DamageTypeTags.BYPASSES_ARMOR);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        ItemStack handStack = player.getItemInHand(hand);

        GrappleWinchConnectionManager manager = GrappleWinchConnectionManager.get(player.level());
        @Nullable GrappleWinchConnection connection = manager.fromHook(this);

        if (connection != null) {
            // attempt to detach grapple winch cable if shears are used on it
            if (handStack.is(KlaxonItemTags.GRAPPLE_WINCH_CABLE_DETACHERS)) {
                if (manager instanceof ServerGrappleWinchConnectionManager serverManager) {
                    serverManager.disconnect(connection.getId(), CableDetachmentReason.MANUAL_DISCONNECT);
                }

                return InteractionResult.SUCCESS;
            }
        } else if (this.onGround()) {
            // attempt to pick up / load the attached grapple claw
            // if that fails, just pick self up and discard
            if (!this.level().isClientSide() && this.tryPickup(player)) {
                player.take(this, 1);
                this.discard();
            }
            return InteractionResult.SUCCESS;
        }

        return super.interact(player, hand);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        Level world = this.level();

        if (world.isClientSide() || this.isRemoved()) {
            return true;
        } else if (this.isInvulnerableTo(source)) {
            return false;
        } else {

            ServerGrappleWinchConnectionManager manager = ServerGrappleWinchConnectionManager.get((ServerLevel) world);
            @Nullable GrappleWinchConnection connection = manager.fromHook(this);

            // players in creative can instantly kill grapple claws with no drop
            if (source.getEntity() instanceof Player player && player.getAbilities().instabuild) {
                this.discard();
                return true;
            }

            ItemStack weaponStack = source.getWeaponItem();
            Entity attacker = source.getEntity();

            // make sure the attacker is a player entity - then, check if we're either not attached or the player is the attached player
            // if this passes, attempt fast reloading - and return true if that passes.
            if (weaponStack != null && attacker instanceof Player player && (connection == null || player.equals(connection.getPlayer())) && this.klaxon$tryFastReload(player, weaponStack)) {
                return true;
            }

            // any properly tagged items instakill grapple claws
            // by default includes mining tools and melee weapons
            if (weaponStack != null && weaponStack.is(KlaxonItemTags.GRAPPLE_CLAW_INSTAKILL)) {
                this.kill();
                this.playSoundAtBothCableEndsIfPossible(
                        KlaxonSoundEvents.ENTITY_GRAPPLE_CLAW_DESTROY,
                        0.8f + this.level().getRandom().nextFloat() * 0.2f,
                        0.7f + this.level().getRandom().nextFloat() * 0.3f
                );
                this.level().gameEvent(this, GameEvent.ENTITY_DAMAGE, this.position());
                return true;
            }
        }

        return false;
    }

    private void playSoundAtBothCableEndsIfPossible(SoundEvent soundEvent, float volume, float pitch) {
        GrappleWinchConnectionManager manager = GrappleWinchConnectionManager.get(this.level());
        if (manager instanceof ServerGrappleWinchConnectionManager serverManager) {
            ServerGrappleWinchConnection connection = serverManager.fromHook(this);
            if (connection == null) {
                this.playSound(soundEvent, volume, pitch);
            } else {
                connection.playSoundAtBothCableEnds(soundEvent, volume, pitch);
            }
        }
    }

    @Override
    public void kill() {
        if (this.pickup.equals(Pickup.ALLOWED)) {
            this.spawnAtLocation(getPickupItemStackOrigin());
        }
        super.kill();
    }

    @Override
    public boolean isPickable() {
        return !this.hookedEntityContainer.isPresent();
    }

    @Override
    public boolean isAttackable() {
        return true;
    }

    @Override
    public float getPickRadius() {
        return 0.0f;
    }

    @Override
    public void thunderHit(ServerLevel world, LightningBolt lightning) {
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        Entity hitEntity = entityHitResult.getEntity();

        // if we're already attached to an entity, don't process further
        if (this.hookedEntityContainer.isPresent()) {
            return;
        }

        GrappleWinchConnectionManager manager = GrappleWinchConnectionManager.get(this.level());
        @Nullable GrappleWinchConnection connection = manager.fromHook(this);

        // if we hit the attached player, attempt to fast reload
        if (connection != null && hitEntity.equals(connection.getPlayer())) {
            // attempt to pickup items into attached player when hitting
            this.draggedItemsContainer.forEach((itemEntity -> {
                if (!itemEntity.isRemoved()) {
                    itemEntity.playerTouch(connection.getPlayer());
                }
            }));

            if (!(this.klaxon$tryFastReload(connection.getPlayer(), connection.getPlayer().getMainHandItem()) || this.klaxon$tryFastReload(connection.getPlayer(), connection.getPlayer().getOffhandItem()))) {
                // if we can't be picked up, bonk all velocity
                setDeltaMovement(Vec3.ZERO);
            }
        } else if (!this.hookedEntityContainer.tryHook(hitEntity)) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01, -0.1, -0.01));
        }
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return super.canHitEntity(entity) && !entity.getType().is(KlaxonEntityTypeTags.GRAPPLE_CLAW_COLLISION_DENYLIST) && !(entity instanceof GrappleClawEntity);
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        this.getEntityData().set(DRAGGED_ITEM_IDS, List.of());
    }

    @Override
    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return KlaxonSoundEvents.ENTITY_GRAPPLE_CLAW_ANCHOR;
    }

    private DamageSource createDamageSource(ResourceKey<DamageType> key) {
        return this.level().damageSources().source(
                key,
                this,
                this.getOwner() == null ? this : this.getOwner()
        );
    }

    @Override
    public void lerpTo(double x, double y, double z, float yaw, float pitch, int interpolationSteps) {
        // needed so that it doesn't look like jittery bullshit when hooking an entity
        if (!hookedEntityContainer.isPresent()) {
            this.setPos(x, y, z);
        }
        this.setRot(yaw, pitch);
    }

    @Override
    public void tick() {
        Level world = this.level();

        GrappleWinchConnectionManager manager = GrappleWinchConnectionManager.get(world);
        @Nullable GrappleWinchConnection connection = manager.fromHook(this);
        if (connection != null && connection.isRetracting()) {
            this.draggedItemsContainer.tick(connection);
        }

        if (this.hookedEntityContainer.isPresent()) {
            this.hookedEntityContainer.tick();
        }

        // important to call this after the hooked entity container resets our velocity to 0 - it updates position in the super method
        super.tick();

        if (this.hookedEntityContainer.isPresent()) {
            Entity hookedEntity = this.hookedEntityContainer.get();
            this.xOld = hookedEntity.xOld;
            this.yOld = hookedEntity.yOld + this.hookedEntityContainer.getHookOffsetForEntity(hookedEntity);
            this.zOld = hookedEntity.zOld;
        }

        // boom look at me using &= how snazzy
        this.inGround &= !this.hookedEntityContainer.isPresent();

        this.reapplyPosition();
    }

    @Override
    protected void tickDespawn() {
        // only age up if in ground and disconnected
        if (inGround && !this.isConnected()) {
            super.tickDespawn();
        }
    }

    @Override
    protected boolean tryPickup(Player pickupPlayer) {
        GrappleWinchConnectionManager manager = GrappleWinchConnectionManager.get(this.level());
        @Nullable GrappleWinchConnection fromHook = manager.fromHook(this);
        @Nullable GrappleWinchConnection fromPlayer = manager.fromPlayer(pickupPlayer);

        boolean pickupTypeValid = true;
        switch (this.pickup) {
            case DISALLOWED -> pickupTypeValid = false;
            case CREATIVE_ONLY -> pickupTypeValid = pickupPlayer.isCreative();
        }

        if (fromHook == fromPlayer) {
            if (fromHook != null && pickupTypeValid) {
                BlockPos steppingPos = pickupPlayer.getOnPos();
                BlockPos anchoredPos = this.blockPosition();

                // don't pick up grapple claws that are hooked into entities.
                if (this.hookedEntityContainer.isPresent()) {
                    return false;
                }

                // don't pick up grapple claw while you're being supported by it
                if ((!pickupPlayer.onGround() && anchoredPos.getY() > steppingPos.getY())) {
                    return false;
                }

                if (this.klaxon$tryFastReload(pickupPlayer, pickupPlayer.getMainHandItem()) || this.klaxon$tryFastReload(pickupPlayer, pickupPlayer.getOffhandItem())) {
                    return true;
                }
            }

            return super.tryPickup(pickupPlayer);
        } else { // don't permit pickups if there's a connection mismatch between hook & player
            return false;
        }
    }

    public boolean isConnected() {
        return GrappleWinchConnectionManager.get(this.level()).fromHook(this) != null;
    }

    public boolean isAttachedToPlayer(Player player) {
        return player.equals(this.getAttachedPlayer());
    }

    public @Nullable Player getAttachedPlayer() {
        return GrappleWinchConnectionManager.get(this.level()).fromHook(this) instanceof GrappleWinchConnection connection ? connection.getPlayer() : null;
    }

    @Override
    public Vec3 getDeltaMovement() {
        // don't override velocity if hooked entity is main player because that makes the grapple claw desync from hooked player's eye position in their client view
        if (this.hookedEntityContainer.isPresent() && !(this.hookedEntityContainer.get() instanceof Player player && player.isLocalPlayer())) {
            return this.hookedEntityContainer.get().getDeltaMovement();
        } else {
            return super.getDeltaMovement();
        }
    }

    @Override
    public void push(double deltaX, double deltaY, double deltaZ) {
        if (this.hookedEntityContainer.isPresent() && this.hookedEntityContainer.get().isControlledByLocalInstance()) {
            this.hookedEntityContainer.get().push(deltaX, deltaY, deltaZ);
        } else {
            super.push(deltaX, deltaY, deltaZ);
        }
    }

    @Override
    public void setYRot(float yaw) {
        if (!this.hookedEntityContainer.isPresent()) {
            super.setYRot(yaw);
        }
    }

    @Override
    public void setXRot(float pitch) {
        if (!this.hookedEntityContainer.isPresent()) {
            super.setXRot(pitch);
        }
    }

    public float getHookYOffset() {
        @Nullable Entity hooked = this.hookedEntityContainer.get();
        return hooked == null ? 0 : this.hookedEntityContainer.getHookOffsetForEntity(hooked);
    }

    @Override
    public boolean canUsePortal(boolean allowVehicles) {
        return !this.hookedEntityContainer.isPresent();
    }

    @Override
    public boolean isControlledByLocalInstance() {
        return super.isControlledByLocalInstance() || (this.hookedEntityContainer.isPresent() && this.hookedEntityContainer.get().isControlledByLocalInstance());
    }

    @Override
    public SoundSource getSoundSource() {
        return this.isConnected() ? SoundSource.PLAYERS : super.getSoundSource();
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
    }

    @Override
    public void remove(RemovalReason reason) {
        this.hookedEntityContainer.release(false);
        super.remove(reason);
    }

    public boolean hasHookedEntity() {
        return this.hookedEntityContainer.isPresent();
    }

    @Override
    public boolean mayInteract(Level world, BlockPos pos) {
        return !super.mayInteract(world, pos) || !(this.getOwner() instanceof Player player) || player.mayBuild();
    }

    @Override
    public @Nullable ItemStack getPickResult() {
        return this.getPickupItemStackOrigin().copy();
    }

    @Override
    public Entity klaxon$asEntity() {
        return this;
    }

    @Override
    public @Nullable Entity klaxon$getHookedEntity() {
        return this.hookedEntityContainer.get();
    }

    @Override
    public void klaxon$onConnect(ServerPlayer serverPlayer) {
        this.setOwner(serverPlayer);
    }

    @Override
    public void klaxon$onDisconnect(CableDetachmentReason reason) {
        this.hookedEntityContainer.release(false);
        this.draggedItemsContainer.clear();
    }

    @Override
    public ItemStack klaxon$getItemStack() {
        return this.getPickupItemStackOrigin();
    }

    @Override
    public boolean klaxon$isAnchored() {
        return this.inGround || this.hookedEntityContainer.isPresent() && EntityWeightHelper.isHeavy(this.hookedEntityContainer.get());
    }

    @Override
    public void klaxon$deAnchor(Vec3 deAnchoringDirection) {
        boolean success = false;
        if (this.inGround) {
            BlockHitResult hitResult = GrappleClawBlockDestructionHelper.raycast(
                    this,
                    this.position(),
                    this.position().add(deAnchoringDirection),
                    true
            );

            // make sure it's either a miss or we've got space to move a significant amount
            if (hitResult.getType().equals(HitResult.Type.MISS) || hitResult.getLocation().distanceTo(this.position()) > 0.1) {
                this.inGround = false;
                success = true;
            }
        } else if (this.hookedEntityContainer.isHeavy()) {
            success = this.hookedEntityContainer.release(true);
        }

        if (success) {
            KlaxonAdvancementTriggers.triggerGrappleWinchDeAnchorGrappleClaw((ServerPlayer) this.getAttachedPlayer());
        }

    }

    public class DraggedItemsContainer {
        private final HashSet<ItemEntity> draggedItems = new HashSet<>();

        private void tick(GrappleWinchConnection connection) {
            if (connection instanceof ServerGrappleWinchConnection) {
                this.gatherNearbyItemEntities(GrappleClawEntity.this.getBoundingBox().inflate(GrappleClawEntity.this.getBbHeight()));
            }
            if (!connection.isHookAnchored()) {
                this.moveItems();
            }
        }

        private void onTrackedDataUpdate(List<Integer> newItemIds) {
            if (newItemIds.isEmpty()) {
                this.clear();
            } else {
                // purge stale items
                this.draggedItems.removeIf((itemEntity -> !newItemIds.contains(itemEntity.getId())));

                // add the new entities
                for (int id : newItemIds) {
                    Entity entity = GrappleClawEntity.this.level().getEntity(id);
                    if (entity != null && !entity.isRemoved() && entity instanceof ItemEntity itemEntity) {
                        this.draggedItems.add(itemEntity);
                    }
                }
            }
        }

        public void add(ItemEntity entity) {
            this.draggedItems.add(entity);
            this.sync();
        }

        private void gatherNearbyItemEntities(AABB box) {
            // yonk nearby entities and add to list
            this.draggedItems.addAll(GrappleClawEntity.this.level().getEntities(
                    EntityTypeTest.forClass(ItemEntity.class),
                    box,
                    (entity) -> !entity.isRemoved()
            ));
            this.sync();
        }

        private void moveItems() {
            for (ItemEntity itemEntity : this.draggedItems) {
                itemEntity.setDeltaMovement(GrappleClawEntity.this.getDeltaMovement());
                itemEntity.setPos(GrappleClawEntity.this.position());
            }
        }

        private void forEach(Consumer<ItemEntity> consumer) {
            for (ItemEntity entity : this.draggedItems) {
                consumer.accept(entity);
            }
        }

        private void clear() {
            this.draggedItems.clear();
            this.sync();
        }

        public void sync() {
            GrappleClawEntity.this.getEntityData().set(DRAGGED_ITEM_IDS, this.draggedItems.stream().map(Entity::getId).toList());
        }
    }

    private class HookedEntityContainer {
        private Entity hookedEntity = null;

        private HookedEntityContainer() {
        }

        public void onTrackedDataSet(int newId) {
            // id is offset by one to allow for entities with an id of 0
            // was initially confused by this when i saw it in the fishing bobber entity so im dropping this explanation here for myself or whatever future person reads this
            newId--;

            if (newId < 0) {
                this.hookedEntity = null;
            } else {
                Entity entity = GrappleClawEntity.this.level().getEntity(newId);
                switch (entity) {
                    case EnderDragonAccessor access -> this.setHookedEntity(access.getBody());
                    case null, default -> this.hookedEntity = entity;
                }
            }
        }

        public void tick() {
            if (!this.isPresent()) {
                return;
            }

            GrappleWinchConnectionManager manager = GrappleWinchConnectionManager.get(GrappleClawEntity.this.level());
            @Nullable GrappleWinchConnection connection = manager.fromHook(GrappleClawEntity.this);

            // clear grappled entity if it was removed
            if (connection == null || this.hookedEntity.isRemoved() || !this.hookedEntity.isAlive()) {
                if (this.hookedEntity.getRemovalReason() == RemovalReason.CHANGED_DIMENSION) {
                    GrappleClawEntity.this.setPortalCooldown();
                }
                this.release(false);
            } else {
                this.hookedEntity.checkSlowFallDistance();
            }
        }

        private void setHookedEntity(Entity entity) {

            switch (entity) {
                case EnderDragonPart part -> {
                    GrappleClawEntity.this.getEntityData().set(GrappleClawEntity.HOOKED_ENTITY_ID, part.parentMob.getId() + 1);
                }
                case null, default -> {
                    GrappleClawEntity.this.getEntityData().set(GrappleClawEntity.HOOKED_ENTITY_ID, entity == null ? 0 : entity.getId() + 1);
                }
            }

            this.hookedEntity = entity;
        }

        public boolean tryHook(Entity entity) {
            // this is only for the server
            if (GrappleClawEntity.this.level().isClientSide()) {
                return false;
            }

            GrappleClawEntity claw = GrappleClawEntity.this;

            // hook onto the ender dragon body if possible
            entity = entity instanceof EnderDragonPart part
                    ? ((EnderDragonAccessor) part.parentMob).getBody()
                    : entity;

            DamageSource source = claw.createDamageSource(KlaxonDamageTypes.GRAPPLING);

            if (entity.isInvulnerableTo(source) || (entity instanceof LivingEntity livingEntity && livingEntity.isDamageSourceBlocked(source))) {
                return false;
            }

            // try to damage entity
            // EXCEPT if the entity is an item frame
            // this allows you to yoink it off the wall in a cool way instead of just dropping its item on initial hit
            // top 10 changes people will notice
            // this causes endermen to tp
            if (!entity.getType().is(KlaxonEntityTypeTags.GRAPPLE_CLAW_GENTLY_HOOKED_ENTITIES) || !this.canHookEntity(entity)) {
                entity.hurt(
                        source,
                        claw.getPickupItemStackOrigin().getOrDefault(
                                KlaxonDataComponentTypes.GRAPPLE_CLAW_COMPONENT.value(),
                                GrappleClawComponent.DEFAULT
                        ).computeGrappling(claw.getPickupItemStackOrigin())
                );
            }

            if (!this.canHookEntity(entity)) {
                return false;
            }

            // update position
            this.snapClawToHookPos(entity);

            // hook onto entity
            this.setHookedEntity(entity);

            // pop advancement
            if (claw.getAttachedPlayer() instanceof ServerPlayer serverPlayer) {
                KlaxonAdvancementTriggers.triggerEntityGrapple(serverPlayer, entity);
            }

            GrappleClawEntity.this.draggedItemsContainer.clear();

            // play sound
            claw.playSoundAtBothCableEndsIfPossible(
                    KlaxonSoundEvents.ENTITY_GRAPPLE_CLAW_HOOK,
                    0.8f + claw.level().getRandom().nextFloat() * 0.2f,
                    0.7f + claw.level().getRandom().nextFloat() * 0.3f
            );

            return true;
        }

        public boolean release(boolean damage) {
            if (!this.isPresent()) {
                return false;
            }

            GrappleClawEntity claw = GrappleClawEntity.this;

            if (damage) {
                hookedEntity.hurt(
                        claw.createDamageSource(KlaxonDamageTypes.RENDING),
                        claw.getPickupItemStackOrigin().getOrDefault(
                                KlaxonDataComponentTypes.GRAPPLE_CLAW_COMPONENT.value(),
                                GrappleClawComponent.DEFAULT
                        ).computeRending(claw.getPickupItemStackOrigin())
                );
            }

            // make sure claws aren't stuck in blocks when releasing entities
            // now you can pull item frames off the floor
            if (!claw.level().noCollision(AABB.ofSize(claw.position(), 0.01, 0.01, 0.01))) {
                claw.setPos(claw.getX(), this.hookedEntity.getBoundingBox().minY, claw.getZ());
            }

            Vec3 hookedVelocity = this.hookedEntity.getDeltaMovement();
            this.setHookedEntity(null);
            if (!damage) {
                claw.setDeltaMovement(hookedVelocity);
            }

            return true;
        }

        public boolean canHookEntity(Entity entity) {
            if (entity == null || this.isPresent()) {
                return false;
            }

            GrappleWinchConnectionManager manager = GrappleWinchConnectionManager.get(GrappleClawEntity.this.level());
            if (manager.fromHook(GrappleClawEntity.this) == null) {
                return false;
            }

            return entity.canBeHitByProjectile() && !entity.getType().is(KlaxonEntityTypeTags.GRAPPLE_CLAW_HOOKING_DENYLIST);
        }

        public void snapClawToHookPos() {
            this.snapClawToHookPos(this.hookedEntity);
        }

        public void snapClawToHookPos(Entity target) {
            Vec3 targetPos = target.position().add(0, this.getHookOffsetForEntity(target), 0);

            GrappleClawEntity grappleClaw = GrappleClawEntity.this;

            grappleClaw.setPos(targetPos);
        }

        public float getHookOffsetForEntity(Entity entity) {
            float original = switch (entity) {
                case EnderDragonPart part -> part.getBbHeight() / 2;
                default -> entity.getEyeHeight();
            };

            return original - (GrappleClawEntity.this.getBbHeight() / 2);
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
}