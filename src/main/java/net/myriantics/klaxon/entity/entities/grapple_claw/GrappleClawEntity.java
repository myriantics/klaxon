package net.myriantics.klaxon.entity.entities.grapple_claw;

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
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.myriantics.klaxon.component.configuration.GrappleClawComponent;
import net.myriantics.klaxon.item.equipment.tools.GrappleWinchItem;
import net.myriantics.klaxon.mechanics.entity_weight.EntityWeightHelper;
import net.myriantics.klaxon.mechanics.grapple_winch.CableDetachmentReason;
import net.myriantics.klaxon.mechanics.grapple_winch.GrapplingHook;
import net.myriantics.klaxon.mechanics.grapple_winch.connection.GrappleWinchConnection;
import net.myriantics.klaxon.mechanics.grapple_winch.connection.ServerGrappleWinchConnection;
import net.myriantics.klaxon.mechanics.grapple_winch.hooking.HookingGrappleClawAccess;
import net.myriantics.klaxon.mechanics.grapple_winch.hooking.HookingGrappleClawContainer;
import net.myriantics.klaxon.mechanics.grapple_winch.manager.GrappleWinchConnectionManager;
import net.myriantics.klaxon.mechanics.grapple_winch.manager.ServerGrappleWinchConnectionManager;
import net.myriantics.klaxon.mixin.minecraft.grapple_winch.grapple_claw.EnderDragonEntityAccessor;
import net.myriantics.klaxon.networking.KlaxonServerPlayNetworkHandler;
import net.myriantics.klaxon.networking.s2c.ItemUsageLockoutTrigger;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementTriggers;
import net.myriantics.klaxon.registry.dynamic.KlaxonDamageTypes;
import net.myriantics.klaxon.registry.entity.KlaxonEntityTypes;
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
    private static final int HIT_INVINCIBILITY_TICKS = 5;

    private int ticksSinceDamaged = 0;
    public final HookedEntityContainer hookedEntityContainer = new HookedEntityContainer();

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
        // i can't delegate this to the hooked entity container because this method is called before the container is initialized!!! madge
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
        } else {
            // attempt to pick up / load the attached grapple claw
            // if that fails, just pick self up and discard
            if (!this.klaxon$tryFastReload(player, player.getStackInHand(hand))) {
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

        // if we're already attached to an entity, don't process further
        if (this.hookedEntityContainer.isPresent()) {
            return;
        }

        GrappleWinchConnectionManager manager = ((GrappleWinchConnectionManager.Access) this.getWorld()).klaxon$get();
        assert manager != null;
        @Nullable GrappleWinchConnection connection = manager.fromHook(this);

        // check that we're attached to a cable and that the target entity can be hooked
        if (connection != null && this.hookedEntityContainer.canHookEntity(hitEntity)) {
            // if we hit the attached player, attempt to fast reload
            if (hitEntity.equals(connection.getPlayer())) {
                // attempt to pickup items into attached player when hitting
                if (!this.draggedItems.isEmpty()) {
                    for (ItemEntity itemEntity : this.draggedItems) {
                        itemEntity.onPlayerCollision(connection.getPlayer());
                    }
                }

                if (!(this.klaxon$tryFastReload(connection.getPlayer(), connection.getPlayer().getMainHandStack()) || this.klaxon$tryFastReload(connection.getPlayer(), connection.getPlayer().getOffHandStack()))) {
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
        super.onBlockHit(blockHitResult);
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

        World world = this.getWorld();

        GrappleWinchConnectionManager manager = ((GrappleWinchConnectionManager.Access) world).klaxon$get();
        if (manager != null && manager.fromHook(this) instanceof GrappleWinchConnection connection) {
            if (!world.isClient()) {
                // collect item entities and update their velocity & position
                if (connection.isRetracting()) {
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
        boolean isAttachedToPickupPlayer = this.isAttachedToPlayer(pickupPlayer);

        // don't allow players to pick up attached grapple claws that aren't theirs
        if (this.isConnected()) {
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
            return !this.isConnected() || !(this.klaxon$tryFastReload(pickupPlayer, pickupPlayer.getMainHandStack()) || this.klaxon$tryFastReload(pickupPlayer, pickupPlayer.getOffHandStack()));
        }

        // if all else failed, we can't be picked up - return false
        return false;
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
    public void setVelocity(Vec3d velocity) {
        if (this.hookedEntityContainer.isPresent()) {
            this.hookedEntityContainer.get().setVelocity(velocity);
            super.setVelocity(Vec3d.ZERO);
        } else {
            super.setVelocity(velocity);
        }
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
        this.draggedItems.clear();
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
            KlaxonAdvancementTriggers.triggerGrappleWinchDeAnchorGrappleClaw((ServerPlayerEntity) this.getAttachedPlayer());
        }

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
                    Optional.ofNullable(((HookingGrappleClawAccess) this.hookedEntity).klaxon$get()).ifPresent(HookingGrappleClawContainer::clear);
                }
            } else {
                ((HookingGrappleClawAccess) entity).klaxon$get().setGrappleClaw(GrappleClawEntity.this);
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
            if (claw.getAttachedPlayer() instanceof ServerPlayerEntity serverPlayer) {
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

            if (((HookingGrappleClawAccess) entity).klaxon$get().isPresent()) {
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
}