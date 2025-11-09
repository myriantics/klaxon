package net.myriantics.klaxon.mechanics.grapple_winch.manager;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.PersistentState;
import net.minecraft.world.dimension.DimensionType;
import net.myriantics.klaxon.mechanics.grapple_winch.CableDetachmentReason;
import net.myriantics.klaxon.mechanics.grapple_winch.GrapplingHook;
import net.myriantics.klaxon.mechanics.grapple_winch.connection.GrappleWinchConnection;
import net.myriantics.klaxon.mechanics.grapple_winch.connection.ServerGrappleWinchConnection;
import net.myriantics.klaxon.registry.advancement.KlaxonAdvancementTriggers;
import net.myriantics.klaxon.registry.misc.KlaxonNBTIds;
import net.myriantics.klaxon.registry.misc.KlaxonSoundEvents;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ServerGrappleWinchConnectionManager extends GrappleWinchConnectionManager {

    private final Map<Integer, ServerGrappleWinchConnection> connectionId2Connection = new HashMap<>();
    private final Map<UUID, ServerGrappleWinchConnection> playerUUID2Connection = new HashMap<>();
    private final Map<UUID, ServerGrappleWinchConnection> hookUUID2Connection = new HashMap<>();

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
        ServerGrappleWinchConnection connection = this.playerUUID2Connection.get(player.getUuid());
        return connection.isDormant() ? null : connection;
    }

    @Override
    public @Nullable ServerGrappleWinchConnection fromConnectionId(int connectionId) {
        ServerGrappleWinchConnection connection = this.connectionId2Connection.get(connectionId);
        return connection.isDormant() ? null : connection;
    }

    @Override
    public @Nullable ServerGrappleWinchConnection fromHook(GrapplingHook hook) {
        ServerGrappleWinchConnection connection = this.hookUUID2Connection.get(hook.klaxon$asEntity().getUuid());
        return connection.isDormant() ? null : connection;
    }

    public void connect(ServerPlayerEntity serverPlayer, GrapplingHook hook) {
        int playerId = serverPlayer.getId();
        int hookId = hook.klaxon$asEntity().getId();

        ServerGrappleWinchConnection connection = new ServerGrappleWinchConnection(this, this.currentConnectionId, serverPlayer.getUuid(), hook.klaxon$asEntity().getUuid());
        this.connectionId2Connection.put(connection.getId(), connection);
        this.currentConnectionId++;

        // run on connect effects (mainly just setting owner)
        hook.klaxon$onConnect(serverPlayer);
    }

    @Override
    public void disconnect(int connectionId, CableDetachmentReason reason) {
        ServerGrappleWinchConnection connection = this.connectionId2Connection.get(connectionId);
        if (connection != null) {
            KlaxonAdvancementTriggers.triggerGrappleWinchIntentionallyDisconnectCable(
                    connection.getPlayer(),
                    reason
            );
            if (reason.playsDetachmentSound) {
                connection.playSoundAtBothCableEnds(
                        KlaxonSoundEvents.ENTITY_GRAPPLE_CLAW_DETACH,
                        0.8f + this.world.getRandom().nextFloat() * 0.2f,
                        0.7f + this.world.getRandom().nextFloat() * 0.3f
                );
            }
        }
    }

    public void tick() {
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

    public static String nameFor(RegistryEntry<DimensionType> dimensionTypeEntry) {
        if (dimensionTypeEntry.getKey().isPresent()) {
            Identifier id = dimensionTypeEntry.getKey().get().getValue();

            return id.getNamespace() + "_" + id.getPath() + "_grapple_winch_connection_manager";
        }
        return "grapple_winch_connection_manager";
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

        int currentConnectionId = 0;

        // read all the stored connections and init the maps
        if (nbt.get(KlaxonNBTIds.GRAPPLE_WINCH_CONNECTIONS) instanceof NbtList connections) {
            for (int i = 0; i < connections.size(); i++) {
                ServerGrappleWinchConnection connection = ServerGrappleWinchConnection.fromNbt(manager, connections.getCompound(i), currentConnectionId);
                manager.connectionId2Connection.put(connection.getId(), connection);
                currentConnectionId++;
            }
        }

        // initialize connection id to include all the startup connections
        manager.currentConnectionId = currentConnectionId;

        return manager;
    }

    public interface Access extends GrappleWinchConnectionManager.Access {
        ServerGrappleWinchConnectionManager klaxon$get();
    }
}
