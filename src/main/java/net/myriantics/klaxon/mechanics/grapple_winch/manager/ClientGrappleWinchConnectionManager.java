package net.myriantics.klaxon.mechanics.grapple_winch.manager;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.myriantics.klaxon.mechanics.grapple_winch.CableDetachmentReason;
import net.myriantics.klaxon.mechanics.grapple_winch.GrapplingHook;
import net.myriantics.klaxon.mechanics.grapple_winch.connection.ClientGrappleWinchConnection;
import net.myriantics.klaxon.mechanics.grapple_winch.GrappleWinchCableRenderer;
import net.myriantics.klaxon.networking.KlaxonClientPlayNetworkHandler;
import net.myriantics.klaxon.networking.c2s.GrappleWinchCableForceDisconnectC2S;
import net.myriantics.klaxon.networking.s2c.GrappleWinchConnectionSyncPacket;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class ClientGrappleWinchConnectionManager extends GrappleWinchConnectionManager {
    private final Map<Integer, ClientGrappleWinchConnection> playerId2Connection = new HashMap<>();
    private final Map<Integer, ClientGrappleWinchConnection> hookId2Connection = new HashMap<>();
    private final Map<Integer, ClientGrappleWinchConnection> connectionId2Connection = new HashMap<>();

    public final GrappleWinchCableRenderer renderer = new GrappleWinchCableRenderer();;

    float daylightMultiplier = 1.0f;
    boolean clientPlayerHasNightVision = false;

    public ClientGrappleWinchConnectionManager(ClientWorld world) {
        super(world);
    }

    @Override
    public void tick() {
        for (ClientGrappleWinchConnection connection : connectionId2Connection.values()) {
            if (connection.getPlayer() == MinecraftClient.getInstance().player && !connection.validate()) {
                //TODO: Unhardcode this to allow for more disconnection reasons
                this.forceDisconnect(CableDetachmentReason.INVALID_HELD_ITEMS);
            } else {
                connection.tick();
            }
        }

        // compute the daytime multiplier here instead of every render tick
        // kinda funky, look at daylight on the minecraft wiki for more info haha
        // basically keeps the value at 1.0f unless it's night, in which case it gets progressively smaller until midnight, when it starts climbing back up again.
        long timeOfDay = this.getWorld().getTimeOfDay() % 24000L;
        this.daylightMultiplier = timeOfDay < 12040 || timeOfDay > 22331 ? 1.0f : Math.min(Math.abs((18000f - timeOfDay) / 10000), 1.0f);

        // update night vision status so we're not doing it every render tick
        ClientPlayerEntity clientPlayer = MinecraftClient.getInstance().player;
        this.clientPlayerHasNightVision = clientPlayer != null && clientPlayer.hasStatusEffect(StatusEffects.NIGHT_VISION);
    }

    public void render(
            ClientWorld clientWorld,
            Camera camera,
            RenderTickCounter renderTickCounter,
            MatrixStack matrices,
            VertexConsumerProvider immediate
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

    @Override
    public ClientWorld getWorld() {
        return (ClientWorld) this.world;
    }

    @Override
    public @Nullable ClientGrappleWinchConnection fromPlayer(PlayerEntity player) {
        return this.playerId2Connection.get(player.getId());
    }

    @Override
    public @Nullable ClientGrappleWinchConnection fromConnectionId(int connectionId) {
        return this.connectionId2Connection.get(connectionId);
    }

    @Override
    public @Nullable ClientGrappleWinchConnection fromHook(GrapplingHook hook) {
        return this.hookId2Connection.get(hook.klaxon$asEntity().getId());
    }

    public void connect(GrappleWinchConnectionSyncPacket packet) {
        ClientGrappleWinchConnection connection = new ClientGrappleWinchConnection(packet);
        this.connectionId2Connection.put(packet.connectionId(), connection);
        this.playerId2Connection.put(packet.playerId(), connection);
        this.hookId2Connection.put(packet.hookId(), connection);
    }

    public void forceDisconnect(CableDetachmentReason reason) {
        assert MinecraftClient.getInstance().player != null;
        ClientGrappleWinchConnection connection = this.fromPlayer(MinecraftClient.getInstance().player);
        if (connection != null) {
            this.disconnect(connection.getId(), reason);
            KlaxonClientPlayNetworkHandler.send(new GrappleWinchCableForceDisconnectC2S(reason));
        }
    }

    public void disconnect(int connectionId, CableDetachmentReason reason) {
        ClientGrappleWinchConnection connection = this.connectionId2Connection.remove(connectionId);
        this.playerId2Connection.remove(connection.getPlayerId());
        this.hookId2Connection.remove(connection.getHookId());
        connection.getHook().klaxon$onDisconnect(reason);
    }

    public interface Access extends GrappleWinchConnectionManager.Access {
        ClientGrappleWinchConnectionManager klaxon$get();
    }
}
