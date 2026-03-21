package net.myriantics.klaxon.mechanics.grapple_winch.manager;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.item.equipment.tools.GrappleWinchItem;
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

    public ServerGrappleWinchConnectionManager(ServerLevel world) {
        super(world);
    }

    @Override
    public ServerLevel getWorld() {
        return (ServerLevel) super.getWorld();
    }

    @Override
    public @Nullable ServerGrappleWinchConnection fromPlayer(Player player) {
        for (ServerGrappleWinchConnection connection : this.connectionId2Connection.values()) {
            if (connection.playerUUID.equals(player.getUUID()) && !connection.isDormant()) {
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
            if (connection.hookUUID.equals(hook.klaxon$asEntity().getUUID()) && !connection.isDormant()) {
                return connection;
            }
        }

        return null;
    }

    public void connect(ServerPlayer serverPlayer, GrapplingHook hook) {
        ServerGrappleWinchConnection connection = new ServerGrappleWinchConnection(this, this.currentConnectionId, serverPlayer, hook);
        for (ServerGrappleWinchConnection existing : this.connectionId2Connection.values()) {
            if (existing.playerUUID.equals(connection.playerUUID) || existing.hookUUID.equals(connection.playerUUID)) {
                this.disconnect(existing.getId(), CableDetachmentReason.GENERIC_DISCONNECT);
            }
        }

        this.connectionId2Connection.put(connection.getId(), connection);
        this.currentConnectionId++;

        // run on connect effects (mainly just setting owner)
        hook.klaxon$onConnect(serverPlayer);
        this.setDirty();
    }

    @Override
    protected void disconnectInternal(int connectionId, CableDetachmentReason reason) {
        ServerGrappleWinchConnection connection = this.connectionId2Connection.remove(connectionId);
        if (connection != null) {
            @Nullable ServerPlayer player = connection.getPlayer();

            if (player != null) {
                KlaxonAdvancementTriggers.triggerGrappleWinchIntentionallyDisconnectCable(
                        connection.getPlayer(),
                        connection.getHook(),
                        reason
                );

                ItemStack grappleWinchStack = null;
                ItemStack mainHandStack = player.getMainHandItem();
                ItemStack offHandStack = player.getOffhandItem();

                if (mainHandStack.getItem() instanceof GrappleWinchItem grappleWinchItem && grappleWinchItem.canSupportCable(mainHandStack)) {
                    grappleWinchStack = mainHandStack;
                } else if (offHandStack.getItem() instanceof GrappleWinchItem grappleWinchItem && grappleWinchItem.canSupportCable(offHandStack)) {
                    grappleWinchStack = offHandStack;
                }

                if (grappleWinchStack != null) {
                    grappleWinchStack.hurtAndBreak(
                            (int) (connection.getCableLength() / connection.getMaxCableLength()) * 4,
                            player,
                            grappleWinchStack == mainHandStack ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND
                    );
                }
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
        this.setDirty();
    }

    public void tick() {
        super.tick();
        for (ServerGrappleWinchConnection connection : this.connectionId2Connection.values()) {
            connection.tick();
        }
    }

    public static SavedData.Factory<ServerGrappleWinchConnectionManager> getPersistentStateType(ServerLevel serverWorld) {
        return new SavedData.Factory<>(
                () -> new ServerGrappleWinchConnectionManager(serverWorld),
                (nbt, registryLookup) -> fromNbt(serverWorld, nbt),
                null
        );
    }

    public static String nameFor(ResourceKey<Level> worldKey) {
        ResourceLocation id = worldKey.location();
        return id.getNamespace() + "_" + id.getPath() + "_grapple_winch_connection_manager";
    }

    @Override
    public CompoundTag save(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        ListTag list = new ListTag();

        for (ServerGrappleWinchConnection connection : this.connectionId2Connection.values()) {
            list.add(connection.writeNbt(new CompoundTag(), world.registryAccess()));
        }

        nbt.put(KlaxonNBTIds.GRAPPLE_WINCH_CONNECTIONS, list);

        return nbt;
    }

    public static ServerGrappleWinchConnectionManager fromNbt(ServerLevel serverWorld, CompoundTag nbt) {
        ServerGrappleWinchConnectionManager manager = new ServerGrappleWinchConnectionManager(serverWorld);

        int currentId = 0;

        // read all the stored connections and init the maps
        if (nbt.get(KlaxonNBTIds.GRAPPLE_WINCH_CONNECTIONS) instanceof ListTag connections) {
            for (Tag element : connections) {
                if (element instanceof CompoundTag compound) {
                    ServerGrappleWinchConnection connection = ServerGrappleWinchConnection.fromNbt(manager, compound, currentId);
                    manager.connectionId2Connection.put(currentId, connection);
                    currentId++;
                }
            }
        }

        // initialize connection id to include all the startup connections
        manager.currentConnectionId = currentId;

        if (currentId > 0) {
            KlaxonCommon.LOGGER.info("Loaded {} Dormant Grapple Winch Connections in {}!", currentId, manager.world);
        }

        return manager;
    }

    public interface Access extends GrappleWinchConnectionManager.Access {
        ServerGrappleWinchConnectionManager klaxon$getGrappleWinchConnectionManager();
    }

    public static ServerGrappleWinchConnectionManager get(ServerLevel world) {
        @Nullable ServerGrappleWinchConnectionManager manager = ((net.myriantics.klaxon.mechanics.grapple_winch.manager.ServerGrappleWinchConnectionManager.Access) world).klaxon$getGrappleWinchConnectionManager();
        if (manager == null) {
            throw new AssertionError("Grapple Winch Connection Manager not present in " + world + '.');
        } else {
            return manager;
        }
    }
}
