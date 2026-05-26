package net.myriantics.klaxon.mechanics.grapple_winch;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.myriantics.klaxon.mechanics.grapple_winch.manager.GrappleWinchConnectionManager;
import net.myriantics.klaxon.networking.KlaxonClientPlayNetworkHandler;
import net.myriantics.klaxon.networking.c2s.GrappleWinchCableForceDisconnectC2S;
import net.myriantics.klaxon.networking.s2c.GrappleWinchConnectionSyncPacket;
import org.jetbrains.annotations.Nullable;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.*;

public class ClientGrappleWinchConnectionManager extends GrappleWinchConnectionManager {
    private final Map<Integer, ClientGrappleWinchConnection> connectionId2Connection = new HashMap<>();
    public final GrappleWinchCableRenderer renderer = new GrappleWinchCableRenderer();

    private int ticksSinceUpdated = 0;

    float daylightMultiplier = 1.0f;
    boolean clientPlayerHasNightVision = false;

    public ClientGrappleWinchConnectionManager(ClientLevel world) {
        super(world);
    }

    @Override
    public void tick() {
        this.ticksSinceUpdated++;

        super.tick();

        for (ClientGrappleWinchConnection connection : this.connectionId2Connection.values()) {
            if (connection.getPlayer() == Minecraft.getInstance().player && !connection.validate()) {
                //TODO: Unhardcode this to allow for more disconnection reasons
                this.forceDisconnect(CableDetachmentReason.INVALID_HELD_ITEMS);
            } else {
                connection.tick();
            }
        }

        // compute the daytime multiplier here instead of every render tick
        // kinda funky, look at daylight on the minecraft wiki for more info haha
        // basically keeps the value at 1.0f unless it's night, in which case it gets progressively smaller until midnight, when it starts climbing back up again.
        long timeOfDay = this.getLevel().getDayTime() % 24000L;
        this.daylightMultiplier = timeOfDay < 12040 || timeOfDay > 22331 ? 1.0f : Math.min(Math.abs((18000f - timeOfDay) / 10000), 1.0f);

        // update night vision status so we're not doing it every render tick
        LocalPlayer clientPlayer = Minecraft.getInstance().player;
        this.clientPlayerHasNightVision = clientPlayer != null && clientPlayer.hasEffect(MobEffects.NIGHT_VISION);
    }

    public void render(
            ClientLevel clientWorld,
            Camera camera,
            DeltaTracker renderTickCounter,
            PoseStack matrices,
            MultiBufferSource immediate
    ) {
        this.renderer.render(
                clientWorld,
                camera,
                renderTickCounter,
                matrices,
                immediate,
                this.connectionId2Connection.values(),
                this.daylightMultiplier,
                this.clientPlayerHasNightVision
        );
    }

    public int ticksSinceUpdated() {
        return ticksSinceUpdated;
    }

    public void resetTicksSinceUpdated() {
        this.ticksSinceUpdated = 0;
    }

    @Override
    public ClientLevel getLevel() {
        return (ClientLevel) this.world;
    }

    @Override
    public @Nullable ClientGrappleWinchConnection fromPlayer(Player player) {
        for (ClientGrappleWinchConnection connection : this.connectionId2Connection.values()) {
            if (connection.getPlayerId() == player.getId()) {
                return connection;
            }
        }
        return null;
    }

    @Override
    public @Nullable ClientGrappleWinchConnection fromConnectionId(int connectionId) {
        return this.connectionId2Connection.get(connectionId);
    }

    @Override
    public @Nullable ClientGrappleWinchConnection fromHook(GrapplingHook hook) {
        for (ClientGrappleWinchConnection connection : this.connectionId2Connection.values()) {
            if (connection.getHookId() == hook.klaxon$asEntity().getId()) {
                return connection;
            }
        }
        return null;
    }

    public void connect(GrappleWinchConnectionSyncPacket packet) {
        ClientGrappleWinchConnection connection = new ClientGrappleWinchConnection(this, packet);
        for (ClientGrappleWinchConnection existing : this.connectionId2Connection.values()) {
            if (existing.getPlayerId() == connection.getPlayerId() || existing.getHookId() == connection.getHookId()) {
                this.disconnect(existing.getId(), CableDetachmentReason.GENERIC_DISCONNECT);
            }
        }

        this.connectionId2Connection.put(packet.connectionId(), connection);
    }

    public void forceDisconnect(CableDetachmentReason reason) {
        assert Minecraft.getInstance().player != null;
        ClientGrappleWinchConnection connection = this.fromPlayer(Minecraft.getInstance().player);
        if (connection != null) {
            this.disconnect(connection.getId(), reason);
            KlaxonClientPlayNetworkHandler.send(new GrappleWinchCableForceDisconnectC2S(reason));
        }
    }

    protected void disconnectInternal(int connectionId, CableDetachmentReason reason) {
        @Nullable ClientGrappleWinchConnection connection = this.connectionId2Connection.remove(connectionId);
        if (connection != null) {
            if (connection.getHook() != null) connection.getHook().klaxon$onDisconnect(reason);
        }
    }

    public interface Access extends GrappleWinchConnectionManager.Access {
        ClientGrappleWinchConnectionManager klaxon$getGrappleWinchConnectionManager();
    }

    public static ClientGrappleWinchConnectionManager get(ClientLevel world) {
        @Nullable ClientGrappleWinchConnectionManager manager = ((Access) world).klaxon$getGrappleWinchConnectionManager();
        if (manager == null) {
            throw new AssertionError("Grapple Winch Connection Manager not present in " + world + '.');
        } else {
            return manager;
        }
    }
}
