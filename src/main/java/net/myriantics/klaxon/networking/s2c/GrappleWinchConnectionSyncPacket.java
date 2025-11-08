package net.myriantics.klaxon.networking.s2c;

import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.Vec3d;
import net.myriantics.klaxon.client.GrappleWinchClientConnectionManager;
import net.myriantics.klaxon.entity.entities.grapple_claw.GrappleClawEntity;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.GrappleWinchConnectionData;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.PlayerEntityGrappleAccess;
import net.myriantics.klaxon.mechanics.grapple_winch.GrappleWinchConnection;
import net.myriantics.klaxon.mechanics.grapple_winch.GrappleWinchConnectionManager;
import net.myriantics.klaxon.registry.misc.KlaxonPackets;
import org.jetbrains.annotations.Nullable;

public record GrappleWinchConnectionSyncPacket(
       int connectionId,
       int playerId,
       int hookId,
       Vec3d playerFallbackPos,
       Vec3d hookFallbackPos,
       boolean hookAnchored,
       double cableLength
) implements CustomPayload {

    public static final CustomPayload.Id<GrappleWinchConnectionSyncPacket> ID = new CustomPayload.Id<>(KlaxonPackets.GRAPPLE_WINCH_CONNECTION_SYNC_S2C_ID);

    private static final PacketCodec<ByteBuf, Vec3d> VEC3D_PACKET_CODEC = PacketCodecs.VECTOR3F.xmap(Vec3d::new, Vec3d::toVector3f);

    public static final PacketCodec<RegistryByteBuf, GrappleWinchConnectionSyncPacket> PACKET_CODEC = new PacketCodec<RegistryByteBuf, GrappleWinchConnectionSyncPacket>() {
        @Override
        public GrappleWinchConnectionSyncPacket decode(RegistryByteBuf buf) {
            int connectionId = PacketCodecs.VAR_INT.decode(buf);
            int playerId = PacketCodecs.VAR_INT.decode(buf);
            int hookId = PacketCodecs.VAR_INT.decode(buf);
            Vec3d playerFallbackPos = VEC3D_PACKET_CODEC.decode(buf);
            Vec3d hookFallbackPos = VEC3D_PACKET_CODEC.decode(buf);
            boolean hookAnchored = PacketCodecs.BOOL.decode(buf);
            double cableLength = PacketCodecs.DOUBLE.decode(buf);
            return new GrappleWinchConnectionSyncPacket(connectionId, playerId, hookId, playerFallbackPos, hookFallbackPos, hookAnchored, cableLength);
        }

        @Override
        public void encode(RegistryByteBuf buf, GrappleWinchConnectionSyncPacket packet) {
            PacketCodecs.VAR_INT.encode(buf, packet.connectionId());
            PacketCodecs.VAR_INT.encode(buf, packet.playerId());
            PacketCodecs.VAR_INT.encode(buf, packet.hookId());
            VEC3D_PACKET_CODEC.encode(buf, packet.playerFallbackPos());
            VEC3D_PACKET_CODEC.encode(buf, packet.hookFallbackPos);
            PacketCodecs.BOOL.encode(buf, packet.hookAnchored());
            PacketCodecs.DOUBLE.encode(buf, packet.cableLength());
        }
    };

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public void execute(ClientPlayNetworking.Context context) {
        MinecraftClient client = context.client();

        client.execute(() -> {
            if (client.world instanceof GrappleWinchConnectionManager.ClientAccess access) {
                GrappleWinchConnectionManager.Client manager = access.klaxon$get();
                @Nullable GrappleWinchConnection.Client connection = manager.fromConnectionId(this.connectionId);
                if (connection == null) {
                    manager.connect(this);
                } else {
                    connection.sync(this);
                }
            }
        });
    }
}
