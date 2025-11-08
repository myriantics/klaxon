package net.myriantics.klaxon.mechanics.grapple_winch;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.myriantics.klaxon.networking.s2c.GrappleWinchConnectionSyncPacket;
import net.myriantics.klaxon.registry.misc.KlaxonNBTIds;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public abstract sealed class GrappleWinchConnectionManager<T extends GrappleWinchConnection<?>> extends PersistentState {
    protected final Map<Integer, T> playerId2Connection = new HashMap<>();
    protected final Map<Integer, T> hookId2Connection = new HashMap<>();
    protected final Map<Integer, T> connectionId2Connection = new HashMap<>();

    protected final World world;

    private GrappleWinchConnectionManager(World world) {
        this.world = world;
    }

    public static PersistentState.Type<GrappleWinchConnectionManager.Server> getPersistentStateType(ServerWorld serverWorld) {
        return new PersistentState.Type<>(
                () -> new GrappleWinchConnectionManager.Server(serverWorld),
                (nbt, registryLookup) -> Server.fromNbt(serverWorld, nbt),
                null
        );
    }

    public void tick() {
        for (GrappleWinchConnection<?> connection : this.connectionId2Connection.values()) {
            connection.tick();
        }
    }

    public @Nullable T fromPlayerId(int playerId) {
        return playerId2Connection.get(playerId);
    }

    public @Nullable T fromConnectionId(int connectionId) {
        return playerId2Connection.get(connectionId);
    }

    public @Nullable T fromHookId(int anchorId) {
        return hookId2Connection.get(anchorId);
    }

    public void disconnect(int connectionId) {
        T connection = this.connectionId2Connection.remove(connectionId);
        this.playerId2Connection.remove(connection.playerId);
        this.hookId2Connection.remove(connection.hookId);
    }

    public static String nameFor(RegistryEntry<DimensionType> dimensionTypeEntry) {
        if (dimensionTypeEntry.getKey().isPresent()) {
            Identifier id = dimensionTypeEntry.getKey().get().getValue();

            return id.getNamespace() + "_" + id.getPath() + "_grapple_winch_connection_manager";
        }
        return "grapple_winch_connection_manager";
    }

    public static final class Client extends GrappleWinchConnectionManager<GrappleWinchConnection.Client> {
        public Client(ClientWorld clientWorld) {
            super(clientWorld);
        }

        public void connect(GrappleWinchConnectionSyncPacket packet) {
            GrappleWinchConnection.Client connection = new GrappleWinchConnection.Client(packet);
            this.connectionId2Connection.put(packet.connectionId(), connection);
            this.playerId2Connection.put(packet.playerId(), connection);
            this.hookId2Connection.put(packet.hookId(), connection);
        }

        @Override
        public void tick() {
        }

        @Override
        public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
            throw new AssertionError("Attempted to write Client Grapple Winch Connection Manager to NBT. Should never happen.");
        }
    }

    public interface ClientAccess {
        GrappleWinchConnectionManager.Client klaxon$get();
    }

    public static final class Server extends GrappleWinchConnectionManager<GrappleWinchConnection.Server> {
        public Server(ServerWorld serverWorld) {
            super(serverWorld);
        }

        private int currentConnectionId = 0;

        public void connect(ServerPlayerEntity serverPlayer, GrapplingHook hook) {
            int playerId = serverPlayer.getId();
            int hookId = hook.klaxon$getId();

            GrappleWinchConnection.Server connection = new GrappleWinchConnection.Server(serverPlayer.getServerWorld(), this.currentConnectionId, serverPlayer, hook);
            this.playerId2Connection.put(playerId, connection);
            this.hookId2Connection.put(hookId, connection);
            this.connectionId2Connection.put(this.currentConnectionId, connection);
            this.currentConnectionId++;

            // run on connect effects (mainly just setting owner)
            hook.klaxon$onConnect(serverPlayer);
        }

        @Override
        public void disconnect(int connectionId) {
            super.disconnect(connectionId);
        }

        @Override
        public void tick() {
        }

        @Override
        public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
            NbtList list = new NbtList();

            for (GrappleWinchConnection.Server connection : this.connectionId2Connection.values()) {
                list.add(connection.writeNbt(new NbtCompound(), world.getRegistryManager()));
            }

            nbt.put(KlaxonNBTIds.GRAPPLE_WINCH_CONNECTIONS, list);

            return nbt;
        }

        public static GrappleWinchConnectionManager.Server fromNbt(ServerWorld serverWorld, NbtCompound nbt) {
            GrappleWinchConnectionManager.Server manager = new GrappleWinchConnectionManager.Server(serverWorld);

            int currentConnectionId = 0;

            // read all the stored connections and init the maps
            if (nbt.get(KlaxonNBTIds.GRAPPLE_WINCH_CONNECTIONS) instanceof NbtList connections) {
                for (int i = 0; i < connections.size(); i++) {
                    GrappleWinchConnection.Server connection = GrappleWinchConnection.Server.fromNbt(serverWorld, connections.getCompound(i), currentConnectionId);
                    manager.connectionId2Connection.put(connection.connectionId, connection);
                    manager.playerId2Connection.put(connection.playerId, connection);
                    manager.hookId2Connection.put(connection.hookId, connection);
                    currentConnectionId++;
                }
            }

            // initialize connection id to include all the startup connections
            manager.currentConnectionId = currentConnectionId;

            return manager;
        }
    }

    public interface ServerAccess {
        GrappleWinchConnectionManager.Server klaxon$get();
    }
}
