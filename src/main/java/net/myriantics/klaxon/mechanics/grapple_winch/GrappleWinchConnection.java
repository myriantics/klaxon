package net.myriantics.klaxon.mechanics.grapple_winch;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.myriantics.klaxon.mechanics.entity_weight.EntityWeightHelper;
import net.myriantics.klaxon.networking.s2c.GrappleWinchConnectionSyncPacket;
import net.myriantics.klaxon.registry.entity.KlaxonEntityAttributes;
import net.myriantics.klaxon.registry.item.KlaxonItems;
import net.myriantics.klaxon.registry.misc.KlaxonNBTIds;
import net.myriantics.klaxon.registry.misc.KlaxonSoundEvents;

import java.util.Objects;
import java.util.UUID;

public abstract sealed class GrappleWinchConnection<T extends World> {
    protected final int connectionId;
    protected final int playerId;
    protected final int hookId;

    protected final T world;

    protected PlayerEntity player = null;
    protected GrapplingHook hook = null;

    protected boolean retracting = false;
    protected boolean hookAnchored = false;
    protected double cableLength;

    public GrappleWinchConnection(T world, int connectionId, int playerId, int hookId) {
        this.world = world;
        this.connectionId = connectionId;
        this.playerId = playerId;
        this.hookId = hookId;
    }

    public void tick() {

    }

    public int getId() {
        return this.connectionId;
    }

    public PlayerEntity getPlayer() {
        return this.player;
    }

    public GrapplingHook getHook() {
        return this.hook;
    }

    public Vec3d getPlayerPos() {
        return this.player.getPos();
    }

    public Vec3d getHookPos() {
        return this.hook.klaxon$getPos();
    }

    public boolean isHookAnchored() {
        return this.hookAnchored;
    }

    public boolean isRetracting() {
        return this.retracting;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof GrappleWinchConnection<?> connection && connection.player.getUuid().equals(this.player.getUuid());
    }

    public static final class Client extends GrappleWinchConnection<ClientWorld> {
        private Vec3d playerFallbackPos;
        private Vec3d playerFallbackPrevPos;
        private Vec3d hookFallbackPos;
        private Vec3d hookFallbackPrevPos;

        public Client(int connectionId, int playerId, int hookId) {
            super(
                    Objects.requireNonNull(MinecraftClient.getInstance().world),
                    connectionId,
                    playerId,
                    hookId
            );
        }

        public Client(GrappleWinchConnectionSyncPacket packet) {
            this(packet.connectionId(), packet.playerId(), packet.hookId());
            this.playerFallbackPos = packet.playerFallbackPos();
            this.hookFallbackPos = packet.hookFallbackPos();
            this.hookAnchored = packet.hookAnchored();
        }

        public void sync(GrappleWinchConnectionSyncPacket packet) {
            this.playerFallbackPrevPos = this.playerFallbackPos;
            this.playerFallbackPos = packet.playerFallbackPos();
            this.hookFallbackPrevPos = this.hookFallbackPos;
            this.hookFallbackPos = packet.hookFallbackPos();
            this.hookAnchored = packet.hookAnchored();
            if (this.player != null && !this.player.equals(MinecraftClient.getInstance().player)) {
                this.cableLength = packet.cableLength();
            }
        }

        @Override
        public void tick() {
            this.retracting = player.isUsingItem() && player.getActiveItem().isOf(KlaxonItems.GRAPPLE_WINCH);
            super.tick();
        }

        @Override
        public Vec3d getHookPos() {
            return this.hook.klaxon$isRemoved() ? this.hookFallbackPos : super.getHookPos();
        }

        @Override
        public Vec3d getPlayerPos() {
            return this.player.isRemoved() ? this.playerFallbackPos : super.getPlayerPos();
        }

        public Vec3d getLerpedPlayerPos(float delta) {
            double lerpedX = MathHelper.lerp(delta, this.playerFallbackPrevPos.getX(), this.playerFallbackPos.getX());
            double lerpedY = MathHelper.lerp(delta, this.playerFallbackPrevPos.getY(), this.playerFallbackPos.getY());
            double lerpedZ = MathHelper.lerp(delta, this.playerFallbackPrevPos.getZ(), this.playerFallbackPos.getZ());
            return new Vec3d(lerpedX, lerpedY, lerpedZ);
        }

        public Vec3d getLerpedHookPos(float delta) {
            double lerpedX = MathHelper.lerp(delta, this.hookFallbackPrevPos.getX(), this.hookFallbackPos.getX());
            double lerpedY = MathHelper.lerp(delta, this.hookFallbackPrevPos.getY(), this.hookFallbackPos.getY());
            double lerpedZ = MathHelper.lerp(delta, this.hookFallbackPrevPos.getZ(), this.hookFallbackPos.getZ());
            return new Vec3d(lerpedX, lerpedY, lerpedZ);
        }
    }

    public static final class Server extends GrappleWinchConnection<ServerWorld> {
        public Server(ServerWorld serverWorld, int connectionId, ServerPlayerEntity serverPlayer, GrapplingHook hook) {
            super(serverWorld, connectionId, serverPlayer.getId(), hook.klaxon$getId());
            this.player = serverPlayer;
            this.hook = hook;
        }

        private boolean canPlayReboundSound;

        @Override
        public void tick() {
            this.retracting = this.player.isUsingItem() && this.player.getActiveItem().isOf(KlaxonItems.GRAPPLE_WINCH);

            Vec3d compiledHookVec = Vec3d.ZERO;

            Vec3d hookPos = this.getHookPos();
            Vec3d playerEyePos = this.getPlayerPos();
            Vec3d normalizedHook2WielderVec = playerEyePos.subtract(hookPos).normalize();

            double wielderDistance = hookPos.distanceTo(playerEyePos);
            double maxCableLength = player.getAttributeValue(KlaxonEntityAttributes.WINCH_CABLE_LENGTH);

            // update anchor status
            if (this.hookAnchored && this.retracting && EntityWeightHelper.isHeavy(this.player)) {
                this.hook.klaxon$deAnchor(normalizedHook2WielderVec);
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
                        hook.klaxon$setVelocity(hook.klaxon$getVelocity().multiply(0.85));
                    }

                    compiledHookVec = compiledHookVec.add(normalizedHook2WielderVec.multiply(4f/20));

                    // make sure we don't spam the shit out of the rebound sound
                    if (this.canPlayReboundSound) {
                        this.playSoundAtBothCableEnds(
                                KlaxonSoundEvents.ENTITY_GRAPPLE_CLAW_REBOUND_AT_LIMIT,
                                1f + world.getRandom().nextFloat() * 0.3f,
                                0.8f + world.getRandom().nextFloat() * 0.2f
                        );
                        world.emitGameEvent(
                                GameEvent.ENTITY_ACTION,
                                this.getHookPos(),
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

            super.tick();

            // limit fall distance to give players more leeway
            if (this.player.getVelocity().getY() > -1 && this.player.fallDistance > 1.0F) {
                this.player.fallDistance = 1.0F;
            }
        }

        private void playSoundAtBothCableEnds(SoundEvent soundEvent, float pitch, float volume) {

        }

        public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
            nbt.putUuid(KlaxonNBTIds.PLAYER_UUID, this.player.getUuid());
            nbt.putUuid(KlaxonNBTIds.HOOK_UUID, this.hook.klaxon$getUUID());
            return nbt;
        }

        public static GrappleWinchConnection.Server fromNbt(ServerWorld serverWorld, NbtCompound nbt, int connectionId) {
            UUID playerUUID = nbt.getUuid(KlaxonNBTIds.PLAYER_UUID);
            UUID hookUUID = nbt.getUuid(KlaxonNBTIds.HOOK_UUID);
            return new Server(
                    serverWorld,
                    connectionId,
                    (ServerPlayerEntity) serverWorld.getEntity(playerUUID),
                    (GrapplingHook) serverWorld.getEntity(hookUUID)
            );
        }
    }
}
