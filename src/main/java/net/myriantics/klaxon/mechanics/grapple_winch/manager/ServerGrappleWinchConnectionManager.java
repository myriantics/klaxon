package net.myriantics.klaxon.mechanics.grapple_winch.manager;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.mechanics.grapple_winch.CableDetachmentReason;
import net.myriantics.klaxon.mechanics.grapple_winch.GrapplingHook;
import net.myriantics.klaxon.mechanics.grapple_winch.connection.GrappleWinchConnection;
import net.myriantics.klaxon.mechanics.grapple_winch.connection.ServerGrappleWinchConnection;
import net.myriantics.klaxon.networking.KlaxonServerPlayNetworkHandler;
import net.myriantics.klaxon.networking.s2c.GrappleWinchConnectionDiscardPacket;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementTriggers;
import net.myriantics.klaxon.registry.misc.KlaxonNBTIds;
import net.myriantics.klaxon.registry.misc.KlaxonSoundEvents;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ServerGrappleWinchConnectionManager extends GrappleWinchConnectionManager {

    private final Map<Integer, ServerGrappleWinchConnection> connectionId2Connection = new HashMap<>();

    private int currentConnectionId = 0;

    public ServerGrappleWinchConnectionManager(ServerWorld world) {
        super(world);
    }

    @Override
    public ServerWorld getWorld() {
        return (ServerWorld) super.getWorld();
    }

    @Override
    public @Nullable ServerGrappleWinchConnection fromPlayer(PlayerEntity player) {
        for (ServerGrappleWinchConnection connection : this.connectionId2Connection.values()) {
            if (connection.playerUUID.equals(player.getUuid()) && !connection.isDormant()) {
                return connection;
            }
        }

        return null;
    }

    @Override
    public @Nullable ServerGrappleWinchConnection fromConnectionId(int connectionId) {
        ServerGrappleWinchConnection connection = this.connectionId2Connection.get(connectionId);
        return connection == null
                ? null
                : connection.isDormant() ? null : connection;
    }

    @Override
    public @Nullable ServerGrappleWinchConnection fromHook(GrapplingHook hook) {
        for (ServerGrappleWinchConnection connection : this.connectionId2Connection.values()) {
            if (connection.hookUUID.equals(hook.klaxon$asEntity().getUuid()) && !connection.isDormant()) {
                return connection;
            }
        }

        return null;
    }

    public void connect(ServerPlayerEntity serverPlayer, GrapplingHook hook) {
        ServerGrappleWinchConnection connection = new ServerGrappleWinchConnection(this, this.currentConnectionId, serverPlayer.getUuid(), hook.klaxon$asEntity().getUuid());
        for (ServerGrappleWinchConnection existing : this.connectionId2Connection.values()) {
            if (existing.playerUUID.equals(connection.playerUUID) || existing.hookUUID.equals(connection.playerUUID)) {
                this.disconnect(existing.getId(), CableDetachmentReason.GENERIC_DISCONNECT);
            }
        }

        this.connectionId2Connection.put(connection.getId(), connection);
        this.currentConnectionId++;

        // run on connect effects (mainly just setting owner)
        hook.klaxon$onConnect(serverPlayer);
        this.markDirty();
    }

    @Override
    protected void disconnectInternal(int connectionId, CableDetachmentReason reason) {
        ServerGrappleWinchConnection connection = this.connectionId2Connection.remove(connectionId);
        if (connection != null) {
            if (connection.getPlayer() != null) {
                KlaxonAdvancementTriggers.triggerGrappleWinchIntentionallyDisconnectCable(
                        connection.getPlayer(),
                        reason
                );
            }

            if (reason.playsDetachmentSound) {
                connection.playSoundAtBothCableEnds(
                        KlaxonSoundEvents.ENTITY_GRAPPLE_CLAW_DETACH,
                        0.8f + this.world.getRandom().nextFloat() * 0.2f,
                        0.7f + this.world.getRandom().nextFloat() * 0.3f
                );
            }
            connection.sendToTracking(new GrappleWinchConnectionDiscardPacket(connectionId, reason));
        }
        this.markDirty();
    }

    public void tick() {
        super.tick();
        for (ServerGrappleWinchConnection connection : this.connectionId2Connection.values()) {
            connection.tick();
        }
    }

    public static PersistentState.Type<ServerGrappleWinchConnectionManager> getPersistentStateType(ServerWorld serverWorld) {
        return new PersistentState.Type<>(
                () -> new ServerGrappleWinchConnectionManager(serverWorld),
                (nbt, registryLookup) -> fromNbt(serverWorld, nbt),
                null
        );
    }

    public static String nameFor(RegistryKey<World> worldKey) {
        Identifier id = worldKey.getValue();
        return id.getNamespace() + "_" + id.getPath() + "_grapple_winch_connection_manager";
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtList list = new NbtList();

        for (ServerGrappleWinchConnection connection : this.connectionId2Connection.values()) {
            list.add(connection.writeNbt(new NbtCompound(), world.getRegistryManager()));
        }

        nbt.put(KlaxonNBTIds.GRAPPLE_WINCH_CONNECTIONS, list);

        return nbt;
    }

    public static ServerGrappleWinchConnectionManager fromNbt(ServerWorld serverWorld, NbtCompound nbt) {
        ServerGrappleWinchConnectionManager manager = new ServerGrappleWinchConnectionManager(serverWorld);

        int currentId = 0;

        // read all the stored connections and init the maps
        if (nbt.get(KlaxonNBTIds.GRAPPLE_WINCH_CONNECTIONS) instanceof NbtList connections) {
            for (NbtElement element : connections) {
                if (element instanceof NbtCompound compound) {
                    ServerGrappleWinchConnection connection = ServerGrappleWinchConnection.fromNbt(manager, compound, currentId);
                    manager.connectionId2Connection.put(currentId, connection);
                    currentId++;
                }
            }
        }

        // initialize connection id to include all the startup connections
        manager.currentConnectionId = currentId;

        if (currentId > 0) {
            KlaxonCommon.LOGGER.info("Loaded {} Dormant Grapple Winch Connections!", currentId);
        }

        return manager;
    }

    public interface Access extends GrappleWinchConnectionManager.Access {
        ServerGrappleWinchConnectionManager klaxon$get();
    }
}
