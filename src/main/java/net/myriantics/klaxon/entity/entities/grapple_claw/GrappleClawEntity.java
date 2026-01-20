package net.myriantics.klaxon.entity.entities.grapple_claw;

import net.minecraft.entity.*;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.BlockAttachedEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.myriantics.klaxon.component.configuration.GrappleClawComponent;
import net.myriantics.klaxon.mechanics.entity_weight.EntityWeightHelper;
import net.myriantics.klaxon.mechanics.grapple_winch.CableDetachmentReason;
import net.myriantics.klaxon.mechanics.grapple_winch.GrapplingHook;
import net.myriantics.klaxon.mechanics.grapple_winch.connection.GrappleWinchConnection;
import net.myriantics.klaxon.mechanics.grapple_winch.connection.ServerGrappleWinchConnection;
import net.myriantics.klaxon.mechanics.grapple_winch.manager.GrappleWinchConnectionManager;
import net.myriantics.klaxon.mechanics.grapple_winch.manager.ServerGrappleWinchConnectionManager;
import net.myriantics.klaxon.mixin.minecraft.grapple_winch.grapple_claw.EnderDragonEntityAccessor;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementTriggers;
import net.myriantics.klaxon.registry.dynamic.KlaxonDamageTypes;
import net.myriantics.klaxon.registry.entity.KlaxonEntityTypes;
import net.myriantics.klaxon.registry.entity.KlaxonTrackedDataHandlerRegistry;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.registry.misc.KlaxonNBTIds;
import net.myriantics.klaxon.registry.misc.KlaxonSoundEvents;
import net.myriantics.klaxon.tag.klaxon.KlaxonDamageTypeTags;
import net.myriantics.klaxon.tag.klaxon.KlaxonEntityTypeTags;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public class GrappleClawEntity extends PersistentProjectileEntity implements GrapplingHook {

    protected static final TrackedData<Integer> HOOKED_ENTITY_ID = DataTracker.registerData(GrappleClawEntity.class, TrackedDataHandlerRegistry.INTEGER);
    protected static final TrackedData<List<Integer>> DRAGGED_ITEM_IDS = DataTracker.registerData(GrappleClawEntity.class, KlaxonTrackedDataHandlerRegistry.INT_LIST);
    private static final int HIT_INVINCIBILITY_TICKS = 5;

    private int ticksSinceDamaged = 0;
    private final HookedEntityContainer hookedEntityContainer = new HookedEntityContainer();
    public final DraggedItemsContainer draggedItemsContainer = new DraggedItemsContainer();

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
        builder.add(DRAGGED_ITEM_IDS, List.of());
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        if (data.equals(HOOKED_ENTITY_ID)) {
            this.hookedEntityContainer.onTrackedDataSet(this.getDataTracker().get(HOOKED_ENTITY_ID));
        }
        if (data.equals(DRAGGED_ITEM_IDS)) {
            this.draggedItemsContainer.onTrackedDataUpdate(this.getDataTracker().get(DRAGGED_ITEM_IDS));
        }
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

        GrappleWinchConnectionManager manager = ((GrappleWinchConnectionManager.Access) player.getWorld()).klaxon$get();
        assert manager != null;
        @Nullable GrappleWinchConnection connection = manager.fromHook(this);

        if (connection != null) {
            // attempt to detach grapple winch cable if shears are used on it
            if (handStack.isIn(KlaxonItemTags.GRAPPLE_WINCH_CABLE_DETACHERS)) {
                if (manager instanceof ServerGrappleWinchConnectionManager serverManager) {
                    serverManager.disconnect(connection.getId(), CableDetachmentReason.MANUAL_DISCONNECT);
                }

                return ActionResult.SUCCESS;
            }
        } else if (this.isOnGround()) {
            // attempt to pick up / load the attached grapple claw
            // if that fails, just pick self up and discard
            if (!this.getWorld().isClient() && this.tryPickup(player)) {
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

        // try to conduct electrical damage if possible
        this.tryConductElectricalDamage(this, source, amount);

        if (world.isClient() || this.isRemoved()) {
            return true;
        } else if (this.isInvulnerableTo(source) || ticksSinceDamaged < HIT_INVINCIBILITY_TICKS) {
            return false;
        } else {

            ServerGrappleWinchConnectionManager manager = ((ServerGrappleWinchConnectionManager.Access) world).klaxon$get();
            @Nullable GrappleWinchConnection connection = manager.fromHook(this);

            // players in creative can instantly kill grapple claws
            if (source.getAttacker() instanceof PlayerEntity && ((PlayerEntity)source.getAttacker()).getAbilities().creativeMode) {
                this.discard();
                return true;
            }

            ItemStack weaponStack = source.getWeaponStack();
            Entity attacker = source.getAttacker();

            // make sure the attacker is a player entity - then, check if we're either not attached or the player is the attached player
            // if this passes, attempt fast reloading - and return true if that passes.
            if (weaponStack != null && attacker instanceof PlayerEntity player && (connection == null || player.equals(connection.getPlayer())) && this.klaxon$tryFastReload(player, weaponStack)) {
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
        }

        return false;
    }

    @Override
    public void kill() {
        if (this.pickupType.equals(PickupPermission.ALLOWED)) {
            this.dropStack(getItemStack());
        }
        super.kill();
    }

    @Override
    public boolean canHit() {
        return !this.hookedEntityContainer.isPresent();
    }

    @Override
    public boolean isAttackable() {
        return !this.hookedEntityContainer.isPresent() || (this.hookedEntityContainer.get() instanceof PlayerEntity player && (this.getWorld().isClient() || player.isMainPlayer()));
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

        // if we're already attached to an entity, don't process further
        if (this.hookedEntityContainer.isPresent()) {
            return;
        }

        GrappleWinchConnectionManager manager = ((GrappleWinchConnectionManager.Access) this.getWorld()).klaxon$get();
        if (manager == null) {
            throw new AssertionError();
        }
        @Nullable GrappleWinchConnection connection = manager.fromHook(this);

        // if we hit the attached player, attempt to fast reload
        if (connection != null && hitEntity.equals(connection.getPlayer())) {
            // attempt to pickup items into attached player when hitting
            this.draggedItemsContainer.forEach((itemEntity -> {
                if (!itemEntity.isRemoved()) {
                    itemEntity.onPlayerCollision(connection.getPlayer());
                }
            }));

            if (!(this.klaxon$tryFastReload(connection.getPlayer(), connection.getPlayer().getMainHandStack()) || this.klaxon$tryFastReload(connection.getPlayer(), connection.getPlayer().getOffHandStack()))) {
                // if we can't be picked up, bonk all velocity
                setVelocity(Vec3d.ZERO);
            }
        } else if (!this.hookedEntityContainer.tryHook(hitEntity)) {
            this.setVelocity(this.getVelocity().multiply(-0.01, -0.1, -0.01));
        }
    }

    @Override
    protected boolean canHit(Entity entity) {
        return super.canHit(entity) && !entity.getType().isIn(KlaxonEntityTypeTags.GRAPPLE_CLAW_COLLISION_DENYLIST) && !(entity instanceof GrappleClawEntity);
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        this.getDataTracker().set(DRAGGED_ITEM_IDS, List.of());
    }

    @Override
    protected SoundEvent getHitSound() {
        return KlaxonSoundEvents.ENTITY_GRAPPLE_CLAW_ANCHOR;
    }

    private DamageSource createDamageSource(RegistryKey<DamageType> key) {
        return this.getWorld().getDamageSources().create(
                key,
                this,
                this.getOwner() == null ? this : this.getOwner()
        );
    }

    @Override
    public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps) {
        // needed so that it doesn't look like jittery bullshit when hooking an entity
        if (!hookedEntityContainer.isPresent()) {
            this.setPosition(x, y, z);
        }
        this.setRotation(yaw, pitch);
    }

    @Override
    public void tick() {
        // update damage reset ticker
        ticksSinceDamaged++;

        World world = this.getWorld();

        GrappleWinchConnectionManager manager = ((GrappleWinchConnectionManager.Access) world).klaxon$get();
        if (manager == null) {
            throw new AssertionError();
        }
        @Nullable GrappleWinchConnection connection = manager.fromHook(this);
        if (connection != null && connection.isRetracting()) {
            this.draggedItemsContainer.tick(connection);
        }

        if (this.hookedEntityContainer.isPresent()) {
            this.hookedEntityContainer.tick();
        }

        // important to call this after the hooked entity container resets our velocity to 0 - it updates position in the super method
        super.tick();

        // boom look at me using &= how snazzy
        this.inGround &= !this.hookedEntityContainer.isPresent();

        this.refreshPosition();
    }

    @Override
    protected void age() {
        // only age up if in ground and disconnected
        if (inGround && !this.isConnected()) {
            super.age();
        }
    }

    public Entity[] conductLightningEffects(ServerWorld serverWorld, List<Entity> struckEntities, LightningEntity lightningEntity) {
        ServerGrappleWinchConnectionManager manager = ((ServerGrappleWinchConnectionManager.Access) serverWorld).klaxon$get();
        @Nullable ServerGrappleWinchConnection connection = manager.fromHook(this);

        @Nullable PlayerEntity attachedPlayer = connection == null ? null : connection.getPlayer();
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
        if (this.getWorld().isClient() || !this.isConnected() || damageSource == lastTransmittedDamageSource || !damageSource.isIn(KlaxonDamageTypeTags.GRAPPLE_WINCH_CABLE_TRANSMISSIBLE)) {
            return;
        }

        ServerGrappleWinchConnectionManager manager = ((ServerGrappleWinchConnectionManager.Access) this.getWorld()).klaxon$get();
        @Nullable ServerGrappleWinchConnection connection = manager.fromHook(this);

        @Nullable PlayerEntity attachedPlayer = connection == null ? null : connection.getPlayer();
        @Nullable Entity hookedEntity = this.hookedEntityContainer.get();

        for (Entity entity : new Entity[]{this, attachedPlayer, hookedEntity}) {
            if (entity == null || entity.equals(originEntity)) {
                continue;
            }

            entity.damage(damageSource, amount);
        }

        this.lastTransmittedDamageSource = damageSource;
    }

    @Override
    protected boolean tryPickup(PlayerEntity pickupPlayer) {
        GrappleWinchConnectionManager manager = ((GrappleWinchConnectionManager.Access) this.getWorld()).klaxon$get();
        if (manager == null) {
            throw new AssertionError();
        }
        @Nullable GrappleWinchConnection fromHook = manager.fromHook(this);
        @Nullable GrappleWinchConnection fromPlayer = manager.fromPlayer(pickupPlayer);

        boolean pickupTypeValid = true;
        switch (this.pickupType) {
            case DISALLOWED -> pickupTypeValid = false;
            case CREATIVE_ONLY -> pickupTypeValid = pickupPlayer.isCreative();
        }

        if (fromHook == fromPlayer) {
            if (fromHook != null && pickupTypeValid) {
                BlockPos steppingPos = pickupPlayer.getSteppingPos();
                BlockPos anchoredPos = this.getBlockPos();

                // don't pick up grapple claws that are hooked into entities.
                if (this.hookedEntityContainer.isPresent()) {
                    return false;
                }

                // don't pick up grapple claw while you're being supported by it
                if ((!pickupPlayer.isOnGround() && anchoredPos.getY() > steppingPos.getY())) {
                    return false;
                }

                if (this.klaxon$tryFastReload(pickupPlayer, pickupPlayer.getMainHandStack()) || this.klaxon$tryFastReload(pickupPlayer, pickupPlayer.getOffHandStack())) {
                    return true;
                }
            }

            return super.tryPickup(pickupPlayer);
        } else { // don't permit pickups if there's a connection mismatch between hook & player
            return false;
        }
    }

    public boolean isConnected() {
        return ((GrappleWinchConnectionManager.Access) this.getWorld()).klaxon$get().fromHook(this) != null;
    }

    public boolean isAttachedToPlayer(PlayerEntity player) {
        return player.equals(this.getAttachedPlayer());
    }

    public @Nullable PlayerEntity getAttachedPlayer() {
        return ((GrappleWinchConnectionManager.Access) this.getWorld()).klaxon$get().fromHook(this) instanceof GrappleWinchConnection connection ? connection.getPlayer() : null;
    }

    @Override
    public Vec3d getVelocity() {
        // don't override velocity if hooked entity is main player because that makes the grapple claw desync from hooked player's eye position in their client view
        if (this.hookedEntityContainer.isPresent() && !(this.hookedEntityContainer.get() instanceof PlayerEntity player && player.isMainPlayer())) {
            return this.hookedEntityContainer.get().getVelocity();
        } else {
            return super.getVelocity();
        }
    }

    @Override
    public void addVelocity(double deltaX, double deltaY, double deltaZ) {
        if (this.hookedEntityContainer.isPresent() && this.hookedEntityContainer.get().isLogicalSideForUpdatingMovement()) {
            this.hookedEntityContainer.get().addVelocity(deltaX, deltaY, deltaZ);
        } else {
            super.addVelocity(deltaX, deltaY, deltaZ);
        }
    }

    @Override
    public void setYaw(float yaw) {
        if (!this.hookedEntityContainer.isPresent()) {
            super.setYaw(yaw);
        }
    }

    @Override
    public void setPitch(float pitch) {
        if (!this.hookedEntityContainer.isPresent()) {
            super.setPitch(pitch);
        }
    }

    @Override
    public boolean canUsePortals(boolean allowVehicles) {
        return !this.hookedEntityContainer.isPresent();
    }

    @Override
    public boolean isLogicalSideForUpdatingMovement() {
        return super.isLogicalSideForUpdatingMovement() || (this.hookedEntityContainer.isPresent() && this.hookedEntityContainer.get().isLogicalSideForUpdatingMovement());
    }

    @Override
    public SoundCategory getSoundCategory() {
        return this.isConnected() ? SoundCategory.PLAYERS : super.getSoundCategory();
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
        this.hookedEntityContainer.release(false);
        super.remove(reason);
    }

    public boolean hasHookedEntity() {
        return this.hookedEntityContainer.isPresent();
    }

    @Override
    public @Nullable ItemStack getPickBlockStack() {
        return this.getItemStack().copy();
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
    public void klaxon$onConnect(ServerPlayerEntity serverPlayer) {
        this.setOwner(serverPlayer);
    }

    @Override
    public void klaxon$onDisconnect(CableDetachmentReason reason) {
        this.hookedEntityContainer.release(false);
        this.draggedItemsContainer.clear();
    }

    @Override
    public ItemStack klaxon$getItemStack() {
        return this.getItemStack();
    }

    @Override
    public boolean klaxon$isAnchored() {
        return this.inGround || this.hookedEntityContainer.isPresent() && EntityWeightHelper.isHeavy(this.hookedEntityContainer.get());
    }

    @Override
    public void klaxon$deAnchor(Vec3d deAnchoringDirection) {
        boolean success = false;
        if (this.inGround) {
            BlockHitResult hitResult = GrappleClawBlockDestructionHelper.raycast(
                    this,
                    this.getPos(),
                    this.getPos().add(deAnchoringDirection),
                    true
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
            KlaxonAdvancementTriggers.triggerGrappleWinchDeAnchorGrappleClaw((ServerPlayerEntity) this.getAttachedPlayer());
        }

    }

    public class DraggedItemsContainer {
        private final HashSet<ItemEntity> draggedItems = new HashSet<>();

        private void tick(GrappleWinchConnection connection) {
            if (connection instanceof ServerGrappleWinchConnection) {
                this.gatherNearbyItemEntities(GrappleClawEntity.this.getBoundingBox().expand(GrappleClawEntity.this.getHeight()));
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
                    Entity entity = GrappleClawEntity.this.getWorld().getEntityById(id);
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

        private void gatherNearbyItemEntities(Box box) {
            // yonk nearby entities and add to list
            this.draggedItems.addAll(GrappleClawEntity.this.getWorld().getEntitiesByType(
                    TypeFilter.instanceOf(ItemEntity.class),
                    box,
                    (entity) -> !entity.isRemoved()
            ));
            this.sync();
        }

        private void moveItems() {
            for (ItemEntity itemEntity : this.draggedItems) {
                itemEntity.setVelocity(GrappleClawEntity.this.getVelocity());
                itemEntity.setPosition(GrappleClawEntity.this.getPos());
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
            GrappleClawEntity.this.getDataTracker().set(DRAGGED_ITEM_IDS, this.draggedItems.stream().map(Entity::getId).toList());
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
                Entity entity = GrappleClawEntity.this.getWorld().getEntityById(newId);
                switch (entity) {
                    case EnderDragonEntityAccessor access -> this.setHookedEntity(access.getBody());
                    case null, default -> this.hookedEntity = entity;
                }
            }
        }

        public void tick() {
            if (!this.isPresent()) {
                return;
            }

            GrappleWinchConnectionManager manager = ((GrappleWinchConnectionManager.Access) GrappleClawEntity.this.getWorld()).klaxon$get();
            if (manager == null) {
                throw new AssertionError();
            }
            @Nullable GrappleWinchConnection connection = manager.fromHook(GrappleClawEntity.this);

            // clear grappled entity if it was removed
            if (connection == null || this.hookedEntity.isRemoved() || !this.hookedEntity.isAlive()) {
                if (this.hookedEntity.getRemovalReason() == RemovalReason.CHANGED_DIMENSION) {
                    GrappleClawEntity.this.resetPortalCooldown();
                }
                this.release(false);
                return;
            } else {
                this.snapClawToHookPos();
                this.hookedEntity.limitFallDistance();
                GrappleClawEntity.this.setVelocity(Vec3d.ZERO);
            }
        }

        private void setHookedEntity(Entity entity) {

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

        public boolean tryHook(Entity entity) {
            // this is only for the server
            if (GrappleClawEntity.this.getWorld().isClient()) {
                return false;
            }

            GrappleClawEntity claw = GrappleClawEntity.this;

            // hook onto the ender dragon body if possible
            entity = entity instanceof EnderDragonPart part
                    ? ((EnderDragonEntityAccessor) part.owner).getBody()
                    : entity;

            DamageSource source = claw.createDamageSource(KlaxonDamageTypes.GRAPPLING);

            if (entity.isInvulnerableTo(source) || (entity instanceof LivingEntity livingEntity && livingEntity.blockedByShield(source))) {
                return false;
            }

            // try to damage entity
            // EXCEPT if the entity is an item frame
            // this allows you to yoink it off the wall in a cool way instead of just dropping its item on initial hit
            // top 10 changes people will notice
            // this causes endermen to tp
            if (!(entity instanceof BlockAttachedEntity)) {
                entity.damage(
                        source,
                        claw.getItemStack().getOrDefault(
                                KlaxonDataComponentTypes.GRAPPLE_CLAW_COMPONENT,
                                GrappleClawComponent.DEFAULT
                        ).computeGrappling(claw.getItemStack())
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
            if (claw.getAttachedPlayer() instanceof ServerPlayerEntity serverPlayer) {
                KlaxonAdvancementTriggers.triggerEntityGrapple(serverPlayer, entity);
            }

            GrappleClawEntity.this.draggedItemsContainer.clear();

            return true;
        }

        public boolean release(boolean damage) {
            if (!this.isPresent()) {
                return false;
            }

            GrappleClawEntity claw = GrappleClawEntity.this;

            if (damage) {
                hookedEntity.damage(
                        claw.createDamageSource(KlaxonDamageTypes.RENDING),
                        claw.getItemStack().getOrDefault(
                                KlaxonDataComponentTypes.GRAPPLE_CLAW_COMPONENT,
                                GrappleClawComponent.DEFAULT
                        ).computeRending(claw.getItemStack())
                );
            }

            // make sure claws aren't stuck in blocks when releasing entities
            // now you can pull item frames off the floor
            if (!claw.getWorld().isSpaceEmpty(Box.of(claw.getPos(), 0.01, 0.01, 0.01))) {
                claw.setPosition(claw.getX(), this.hookedEntity.getBoundingBox().minY, claw.getZ());
            }

            Vec3d hookedVelocity = this.hookedEntity.getVelocity();
            this.setHookedEntity(null);
            if (!damage) {
                claw.setVelocity(hookedVelocity);
            }

            return true;
        }

        public boolean canHookEntity(Entity entity) {
            if (entity == null || this.isPresent()) {
                return false;
            }

            GrappleWinchConnectionManager manager = ((GrappleWinchConnectionManager.Access) GrappleClawEntity.this.getWorld()).klaxon$get();
            if (manager == null) {
                throw new AssertionError();
            }
            if (manager.fromHook(GrappleClawEntity.this) == null) {
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
}