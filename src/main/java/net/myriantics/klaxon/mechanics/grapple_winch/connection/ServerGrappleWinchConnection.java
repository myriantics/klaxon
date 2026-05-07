package net.myriantics.klaxon.mechanics.grapple_winch.connection;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.myriantics.klaxon.entity.entities.grapple_claw.GrappleClawEntity;
import net.myriantics.klaxon.item.equipment.tools.GrappleWinchItem;
import net.myriantics.klaxon.mechanics.entity_weight.EntityWeightHelper;
import net.myriantics.klaxon.mechanics.grapple_winch.CableDetachmentReason;
import net.myriantics.klaxon.mechanics.grapple_winch.GrapplingHook;
import net.myriantics.klaxon.mechanics.grapple_winch.manager.ServerGrappleWinchConnectionManager;
import net.myriantics.klaxon.networking.KlaxonServerPlayNetworkHandler;
import net.myriantics.klaxon.networking.s2c.GrappleWinchConnectionSyncPacket;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementTriggers;
import net.myriantics.klaxon.registry.entity.KlaxonEntityAttributes;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.registry.misc.KlaxonNBTIds;
import net.myriantics.klaxon.registry.misc.KlaxonSoundEvents;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class ServerGrappleWinchConnection extends GrappleWinchConnection {

    private final ServerGrappleWinchConnectionManager manager;
    public final UUID playerUUID;
    public final UUID hookUUID;

    private ServerPlayer player;
    private GrapplingHook hook;

    private @Nullable CompoundTag dormantHookNbt;

    boolean canPlayReboundSound = true;
    private State state = State.DORMANT;

    private ServerGrappleWinchConnection(ServerGrappleWinchConnectionManager manager, int connectionId, UUID playerUUID, UUID hookUUID, CompoundTag dormantHookNbt) {
        super(connectionId);
        this.manager = manager;
        this.playerUUID = playerUUID;
        this.hookUUID = hookUUID;
        this.dormantHookNbt = dormantHookNbt;
        this.tryActivate();
        this.resetMaxCableLength();
    }

    public ServerGrappleWinchConnection(ServerGrappleWinchConnectionManager manager, int connectionId, UUID playerUUID, UUID hookUUID) {
        this(manager, connectionId, playerUUID, hookUUID, null);
    }

    public ServerGrappleWinchConnection(ServerGrappleWinchConnectionManager manager, int connectionId, ServerPlayer serverPlayer, GrapplingHook grapplingHook) {
        this(manager, connectionId, serverPlayer.getUUID(), grapplingHook.klaxon$asEntity().getUUID(), null);
        this.player = serverPlayer;
        this.hook = grapplingHook;
        this.state = State.ACTIVE;
        this.resetMaxCableLength();
    }

    @Override
    public int getPlayerId() {
        if (this.isDormant()) {
            throw new AssertionError();
        }
        return this.player.getId();
    }

    @Override
    public int getHookId() {
        if (this.isDormant()) {
            throw new AssertionError();
        }
        return this.hook.klaxon$asEntity().getId();
    }

    @Override
    public ServerPlayer getPlayer() {
        return this.player;
    }

    @Override
    public GrapplingHook getHook() {
        return this.hook;
    }

    @Override
    public Vec3 getPlayerEyePos() {
        return this.player.getEyePosition();
    }

    @Override
    public Vec3 getHookPos() {
        return this.hook.klaxon$asEntity().position();
    }

    public boolean isDormant() {
        return this.state.equals(State.DORMANT);
    }

    void tryPlayReboundSound() {
        // make sure we don't spam the shit out of the rebound sound
        if (this.canPlayReboundSound) {
            this.playSoundAtBothCableEnds(
                    KlaxonSoundEvents.ENTITY_GRAPPLE_CLAW_REBOUND_AT_LIMIT,
                    1f + this.manager.getWorld().getRandom().nextFloat() * 0.3f,
                    0.8f + this.manager.getWorld().getRandom().nextFloat() * 0.2f
            );
            this.manager.getWorld().gameEvent(
                    GameEvent.ENTITY_ACTION,
                    this.hook.klaxon$asEntity().position(),
                    GameEvent.Context.of(player)
            );

            this.canPlayReboundSound = false;
        }
    }

    public void tick() {
        // if activation does not succeed, don't tick further
        if (this.state.equals(State.DORMANT) && !this.tryActivate()) {
            return;
        }

        if (!this.validate()) {
            if (this.retracting) {
                KlaxonServerPlayNetworkHandler.triggerItemLockout(this.player);
            }
            return;
        }

        this.retracting = this.player.isUsingItem() && this.player.getUseItem().is(KlaxonItems.GRAPPLE_WINCH);
        this.resetMaxCableLength();

        // hacky but i dont care to do it better lol
        if (this.retracting && this.hook instanceof GrappleClawEntity grappleClaw && grappleClaw.hasHookedEntity() && !this.player.onGround()) {
            @Nullable Entity hookedEntity = grappleClaw.klaxon$getHookedEntity();
            @Nullable Entity mount = this.player.getVehicle();
            if (hookedEntity != null && hookedEntity == mount && !mount.onGround() && mount.getDeltaMovement().y() > 0) {
                KlaxonAdvancementTriggers.triggerGrappleWinchLevitationBug(this.player);
            }
        }

        Vec3 hookPos = this.getHookPos();
        Vec3 playerEyePos = this.player.getEyePosition();
        Vec3 normalizedHook2WielderVec = playerEyePos.subtract(hookPos).normalize();

        // try to deanchor
        if (this.hookAnchored && this.retracting && EntityWeightHelper.isHeavy(this.player)) {
            this.hook.klaxon$deAnchor(normalizedHook2WielderVec);
        }

        // update anchor status and play sound
        boolean hookAnchored = this.hook.klaxon$isAnchored();
        if (hookAnchored != this.hookAnchored) {
            if (hookAnchored) {
                this.playSoundAtBothCableEnds(
                        KlaxonSoundEvents.ENTITY_GRAPPLE_CLAW_ANCHOR,
                        1.0F,
                        1.0F / (this.getHook().klaxon$asEntity().level().getRandom().nextFloat() * 0.4F + 1.2F)
                );
            }
        }

        this.hookAnchored = this.hook.klaxon$isAnchored();

        // limit fall distance to give players more leeway - ONLY IF ANCHORED THO
        if (this.hookAnchored && this.player.getDeltaMovement().y() > -1 && this.player.fallDistance > 1.0F) {
            this.player.fallDistance = 1.0F;
        }

        super.tick();

        // init the packet
        GrappleWinchConnectionSyncPacket packet = new GrappleWinchConnectionSyncPacket(
                this.connectionId,
                this.getPlayerId(),
                this.getHookId(),
                this.player.getEyePosition(),
                this.hook.klaxon$asEntity().position(),
                this.hookAnchored,
                this.cableLength,
                this.maxCableLength
        );

        this.sendToTracking(packet);
    }

    public void sendToTracking(CustomPacketPayload customPayload) {
        assert !this.state.equals(State.DORMANT);

        // gather all the players tracking this connection
        HashSet<ServerPlayer> tracking = new HashSet<>();
        if (this.player != null) {
            tracking.add(this.player);
            tracking.addAll(PlayerLookup.tracking(this.player));
        }
        if (this.hook != null) {
            tracking.addAll(PlayerLookup.tracking(this.hook.klaxon$asEntity()));
        }

        // send the packet off to all tracking players
        for (ServerPlayer serverPlayer : tracking) {
            KlaxonServerPlayNetworkHandler.send(serverPlayer, customPayload);
        }
    }

    private boolean tryActivate() {
        if (this.state == State.ACTIVE) {
            return false;
        }

        Entity maybePlayer = this.manager.getWorld().getServer().getPlayerList().getPlayer(this.playerUUID);
        Entity maybeHook;

        // we have to delay activation until the player's winch cable length attribute loads
        // otherwise the connection gets ganked as soon as it's created due to a max cable length of 0.
        if (maybePlayer instanceof ServerPlayer serverPlayer && serverPlayer.getAttributeValue(KlaxonEntityAttributes.WINCH_CABLE_LENGTH) > 0) {
            if (this.dormantHookNbt != null) {
                maybeHook = EntityType.loadEntityRecursive(
                        this.dormantHookNbt,
                        this.manager.getWorld(),
                        grapplingHook -> this.manager.getWorld().addWithUUID(grapplingHook) ? grapplingHook : null
                );
            } else if (this.hookUUID != null) {
                maybeHook = this.manager.getWorld().getEntity(this.hookUUID);
            } else {
                throw new AssertionError();
            }

            if (maybeHook instanceof GrapplingHook grapplingHook) {
                this.player = serverPlayer;
                this.hook = grapplingHook;
                this.state = State.ACTIVE;
                this.resetMaxCableLength();
                return true;
            }
        }

        return false;
    }

    public void makeDormant() {
        ((Entity) this.hook).remove(Entity.RemovalReason.UNLOADED_WITH_PLAYER);
        this.dormantHookNbt = ((Entity) this.hook).saveWithoutId(new CompoundTag());
        this.state = State.DORMANT;
    }

    public boolean validate() {
        // don't invalidate dormant connections
        if (this.state.equals(State.DORMANT)) {
            return true;
        }

        @Nullable CableDetachmentReason reason = this.testValidity();
        if (reason != null) {
            this.manager.disconnect(this.connectionId, reason);
            return false;
        }
        return true;
    }

    public @Nullable CableDetachmentReason testValidity() {

        if (!this.player.isAlive()) {
            return CableDetachmentReason.PLAYER_DIED;
        }

        if (this.hook.klaxon$asEntity().isRemoved()) {
            return CableDetachmentReason.HOOK_REMOVED;
        }

        if (this.player.isSpectator()) {
            return CableDetachmentReason.PLAYER_SPECTATOR;
        }

        if (!this.player.level().equals(this.manager.getWorld()) || !this.hook.klaxon$asEntity().level().equals(this.manager.getWorld())) {
            return CableDetachmentReason.WORLD_MISMATCH;
        }

        ItemStack mainHandStack = this.player.getMainHandItem();
        ItemStack offHandStack = this.player.getOffhandItem();

        boolean mainHandValid = mainHandStack.getItem() instanceof GrappleWinchItem grappleWinch && grappleWinch.canSupportCable(mainHandStack);
        boolean offHandValid = offHandStack.getItem() instanceof GrappleWinchItem grappleWinch && grappleWinch.canSupportCable(offHandStack);

        if (!mainHandValid && !offHandValid) {
            return CableDetachmentReason.INVALID_HELD_ITEMS;
        }

        boolean cableTooLong = this.hook.klaxon$asEntity().position().distanceTo(this.player.getEyePosition()) > this.getMaxCableLength() * 1.5f;

        if (cableTooLong) {
            return CableDetachmentReason.CABLE_TOO_LONG;
        }

        // LGTM, continue :)
        return null;
    }

    public void playSoundAtBothCableEnds(SoundEvent soundEvent, float pitch, float volume) {
        this.player.playNotifySound(soundEvent, SoundSource.PLAYERS, pitch, volume);
        this.hook.klaxon$asEntity().playSound(soundEvent, pitch, volume);
    }

    private void resetMaxCableLength() {
        this.maxCableLength = this.player == null ? -1 : this.player.getAttributeValue(KlaxonEntityAttributes.WINCH_CABLE_LENGTH);
    }

    @Override
    public void resetCableLength() {
    }

    public CompoundTag writeNbt(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        // store uuid and grappling hook data
        nbt.putUUID(KlaxonNBTIds.PLAYER_UUID, this.playerUUID);
        if (this.dormantHookNbt != null) {
            nbt.put(KlaxonNBTIds.GRAPPLING_HOOK, this.dormantHookNbt);
        } else if (this.hook != null) {
            CompoundTag newHookNbt = new CompoundTag();
            // needed to know what entity type to use when decoding entity - don't appreciate having to use an access widener for this method haha
            String id = this.hook.klaxon$asEntity().getEncodeId();
            newHookNbt.putString("id", id);
            // save main hook data to grappling hook compound
            this.hook.klaxon$asEntity().saveWithoutId(newHookNbt);
            // commit grappling hook data to main compound
            nbt.put(KlaxonNBTIds.GRAPPLING_HOOK, newHookNbt);
        } else {
            throw new AssertionError("Error while saving Grapple Winch Connection! Expected cached Grappling Hook NBT or active Grappling Hook, both were null!");
        }

        // store other misc data
        nbt.putDouble(KlaxonNBTIds.CABLE_LENGTH, this.cableLength);
        nbt.putDouble(KlaxonNBTIds.MAX_CABLE_LENGTH, this.maxCableLength);
        nbt.putBoolean(KlaxonNBTIds.HOOK_ANCHORED, this.hookAnchored);

        return nbt;
    }

    public static ServerGrappleWinchConnection fromNbt(ServerGrappleWinchConnectionManager manager, CompoundTag nbt, int currentId) {
        UUID playerUUID = nbt.getUUID(KlaxonNBTIds.PLAYER_UUID);
        CompoundTag hookNbt = nbt.getCompound(KlaxonNBTIds.GRAPPLING_HOOK);
        UUID hookUUID = hookNbt.getUUID("UUID");

        double cableLength = nbt.getDouble(KlaxonNBTIds.CABLE_LENGTH);
        double maxCableLength = nbt.getDouble(KlaxonNBTIds.MAX_CABLE_LENGTH);
        boolean hookAnchored = nbt.getBoolean(KlaxonNBTIds.HOOK_ANCHORED);

        ServerGrappleWinchConnection connection = new ServerGrappleWinchConnection(manager, currentId, playerUUID, hookUUID, hookNbt);
        connection.maxCableLength = maxCableLength;
        connection.setCableLength(cableLength);
        connection.hookAnchored = hookAnchored;
        return connection;
    }

    public enum State {
        DORMANT,
        ACTIVE;
    }
}
