package net.myriantics.klaxon.mechanics.grapple_winch.connection;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.event.GameEvent;
import net.myriantics.klaxon.item.equipment.tools.GrappleWinchItem;
import net.myriantics.klaxon.mechanics.entity_weight.EntityWeightHelper;
import net.myriantics.klaxon.mechanics.grapple_winch.CableDetachmentReason;
import net.myriantics.klaxon.mechanics.grapple_winch.GrapplingHook;
import net.myriantics.klaxon.mechanics.grapple_winch.manager.ServerGrappleWinchConnectionManager;
import net.myriantics.klaxon.networking.KlaxonServerPlayNetworkHandler;
import net.myriantics.klaxon.networking.s2c.GrappleWinchConnectionSyncPacket;
import net.myriantics.klaxon.registry.entity.KlaxonEntityAttributes;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.registry.misc.KlaxonNBTIds;
import net.myriantics.klaxon.registry.misc.KlaxonSoundEvents;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class ServerGrappleWinchConnection extends GrappleWinchConnection {

    private final ServerGrappleWinchConnectionManager manager;
    private final UUID playerUUID;
    private final UUID hookUUID;

    private ServerPlayerEntity player;
    private GrapplingHook hook;

    private @Nullable NbtCompound dormantHookNbt;

    private boolean canPlayReboundSound = true;
    private State state = State.DORMANT;

    public ServerGrappleWinchConnection(ServerGrappleWinchConnectionManager manager, int connectionId, UUID playerUUID, UUID hookUUID, NbtCompound dormantHookNbt) {
        super(connectionId);
        this.manager = manager;
        this.playerUUID = playerUUID;
        this.hookUUID = hookUUID;
        this.dormantHookNbt = dormantHookNbt;
        this.tryActivate();
    }

    public ServerGrappleWinchConnection(ServerGrappleWinchConnectionManager manager, int connectionId, UUID playerUUID, UUID hookUUID) {
        this(manager, connectionId, playerUUID, hookUUID, null);
    }

    @Override
    public int getPlayerId() {
        assert this.state.equals(State.ACTIVE);
        return this.player.getId();
    }

    @Override
    public int getHookId() {
        assert this.state.equals(State.ACTIVE);
        return this.hook.klaxon$asEntity().getId();
    }

    @Override
    public ServerPlayerEntity getPlayer() {
        return this.player;
    }

    @Override
    public GrapplingHook getHook() {
        return this.hook;
    }

    @Override
    public Vec3d getPlayerPos() {
        return this.player.getPos();
    }

    @Override
    public Vec3d getHookPos() {
        return this.hook.klaxon$asEntity().getPos();
    }

    @Override
    public void setCableLength(double cableLength) {
        this.cableLength = cableLength;
    }

    public boolean isDormant() {
        return this.state.equals(State.DORMANT);
    }

    public void tick() {
        // if activation does not succeed, don't tick further
        if (this.state.equals(State.DORMANT) && !this.tryActivate()) {
            return;
        }

        if (!this.validate()) {
            return;
        }

        this.retracting = this.player.isUsingItem() && this.player.getActiveItem().isOf(KlaxonItems.GRAPPLE_WINCH);

        Vec3d compiledHookVec = Vec3d.ZERO;

        Vec3d hookPos = this.getHookPos();
        Vec3d playerEyePos = this.getPlayerPos();
        Vec3d normalizedHook2WielderVec = playerEyePos.subtract(hookPos).normalize();

        double wielderDistance = hookPos.distanceTo(playerEyePos);
        double maxCableLength = player.getAttributeValue(KlaxonEntityAttributes.WINCH_CABLE_LENGTH);

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
                        1.0F / (this.getHook().klaxon$asEntity().getWorld().getRandom().nextFloat() * 0.4F + 1.2F)
                );
            }
        }
        this.hookAnchored = this.hook.klaxon$isAnchored();


        // if we're not anchored, move the grappling hook
        if (!this.isHookAnchored()) {

            // retract grapple claw if owner pulls back before landing
            if (this.retracting) {
                compiledHookVec = compiledHookVec.add(normalizedHook2WielderVec.multiply(4f/20));
            }

            // retract grapple claw if it hits limit
            if (wielderDistance >= maxCableLength) {

                if (wielderDistance >= maxCableLength * 1.2) {
                    this.hook.klaxon$asEntity().setVelocity(this.hook.klaxon$asEntity().getVelocity().multiply(0.85));
                }

                compiledHookVec = compiledHookVec.add(normalizedHook2WielderVec.multiply(4f/20));

                // make sure we don't spam the shit out of the rebound sound
                if (this.canPlayReboundSound) {
                    this.playSoundAtBothCableEnds(
                            KlaxonSoundEvents.ENTITY_GRAPPLE_CLAW_REBOUND_AT_LIMIT,
                            1f + this.manager.getWorld().getRandom().nextFloat() * 0.3f,
                            0.8f + this.manager.getWorld().getRandom().nextFloat() * 0.2f
                    );
                    this.manager.getWorld().emitGameEvent(
                            GameEvent.ENTITY_ACTION,
                            this.hook.klaxon$asEntity().getPos(),
                            GameEvent.Emitter.of(player)
                    );

                    this.canPlayReboundSound = false;
                }
            } else if (wielderDistance < maxCableLength * 0.95) {
                // if we go back in bounds, we can play the rebound sound again
                // this has a small deadzone because otherwise it would spam the shit out of the sound when dangling at the end of the cable.
                this.canPlayReboundSound = true;
            }
        }

        this.hook.klaxon$asEntity().addVelocity(compiledHookVec);

        // limit fall distance to give players more leeway
        if (this.player.getVelocity().getY() > -1 && this.player.fallDistance > 1.0F) {
            this.player.fallDistance = 1.0F;
        }

        this.sync();
    }

    public void sync() {
        assert !this.state.equals(State.DORMANT);

        // gather all the players tracking this connection
        HashSet<ServerPlayerEntity> tracking = new HashSet<>();
        tracking.addAll(PlayerLookup.tracking(this.player));
        tracking.addAll(PlayerLookup.tracking(this.hook.klaxon$asEntity()));

        // init the packet
        GrappleWinchConnectionSyncPacket packet = new GrappleWinchConnectionSyncPacket(
                this.connectionId,
                this.getPlayerId(),
                this.getHookId(),
                this.player.getPos(),
                this.hook.klaxon$asEntity().getPos(),
                this.hookAnchored,
                this.cableLength
        );

        // send the packet off to all tracking players
        for (ServerPlayerEntity serverPlayer : tracking) {
            KlaxonServerPlayNetworkHandler.send(serverPlayer, packet);
        }
    }

    private boolean tryActivate() {
        if (this.state == State.ACTIVE) {
            return false;
        }

        if (this.manager.getWorld().getEntity(this.playerUUID) instanceof ServerPlayerEntity serverPlayer) {
            assert dormantHookNbt != null;
            Entity entity = EntityType.loadEntityWithPassengers(
                    dormantHookNbt,
                    manager.getWorld(),
                    grapplingHook -> !manager.getWorld().tryLoadEntity(grapplingHook) ? null : grapplingHook
            );

            if (entity instanceof GrapplingHook grapplingHook) {
                this.player = serverPlayer;
                this.hook = grapplingHook;
                this.state = State.ACTIVE;
                return true;
            }
        }

        return false;
    }

    public void makeDormant() {
        ((Entity) this.hook).remove(Entity.RemovalReason.UNLOADED_WITH_PLAYER);
        this.dormantHookNbt = ((Entity) this.hook).writeNbt(new NbtCompound());
        this.player = null;
        this.hook = null;
        this.state = State.DORMANT;
    }

    public boolean validate() {
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

        if (this.player.isSpectator()) {
            return CableDetachmentReason.PLAYER_SPECTATOR;
        }

        if (!this.player.getWorld().equals(this.manager.getWorld()) || !this.hook.klaxon$asEntity().getWorld().equals(this.manager.getWorld())) {
            return CableDetachmentReason.WORLD_MISMATCH;
        }

        ItemStack mainHandStack = this.player.getMainHandStack();
        ItemStack offHandStack = this.player.getOffHandStack();

        boolean mainHandValid = mainHandStack.getItem() instanceof GrappleWinchItem grappleWinch && grappleWinch.canSupportCable(mainHandStack);
        boolean offHandValid = offHandStack.getItem() instanceof GrappleWinchItem grappleWinch && grappleWinch.canSupportCable(offHandStack);

        if (!mainHandValid && !offHandValid) {
            return CableDetachmentReason.INVALID_HELD_ITEMS;
        }

        boolean cableTooLong = this.hook.klaxon$asEntity().getPos().distanceTo(this.player.getEyePos()) > this.player.getAttributeValue(KlaxonEntityAttributes.WINCH_CABLE_LENGTH) * 1.5f;

        if (cableTooLong) {
            return CableDetachmentReason.CABLE_TOO_LONG;
        }

        // LGTM, continue :)
        return null;
    }

    public void playSoundAtBothCableEnds(SoundEvent soundEvent, float pitch, float volume) {

    }

    @Override
    public void resetCableLength() {
    }

    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        nbt.putUuid(KlaxonNBTIds.PLAYER_UUID, this.playerUUID);
        nbt.put(KlaxonNBTIds.GRAPPLING_HOOK, this.hook.klaxon$asEntity().writeNbt(new NbtCompound()));
        return nbt;
    }

    public static ServerGrappleWinchConnection fromNbt(ServerGrappleWinchConnectionManager manager, NbtCompound nbt, int currentId) {
        UUID playerUUID = nbt.getUuid(KlaxonNBTIds.PLAYER_UUID);
        NbtCompound hookNbt = nbt.getCompound(KlaxonNBTIds.GRAPPLING_HOOK);
        UUID hookUUID = hookNbt.getUuid("UUID");
        return new ServerGrappleWinchConnection(manager, currentId, playerUUID, hookUUID, hookNbt);
    }

    public enum State {
        DORMANT,
        ACTIVE;
    }
}
