package net.myriantics.klaxon.networking.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;
import net.myriantics.klaxon.registry.misc.KlaxonPackets;
import org.jetbrains.annotations.Nullable;

public record GrappleWinchConnectionSyncPacket(
       int connectionId,
       int playerId,
       int hookId,
       Vec3 playerFallbackPos,
       Vec3 hookFallbackPos,
       boolean hookAnchored,
       double cableLength,
       double maxCableLength
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<GrappleWinchConnectionSyncPacket> ID = new CustomPacketPayload.Type<>(KlaxonPackets.GRAPPLE_WINCH_CONNECTION_SYNC_S2C_ID);

    private static final StreamCodec<ByteBuf, Vec3> VEC3D_PACKET_CODEC = ByteBufCodecs.VECTOR3F.map(Vec3::new, Vec3::toVector3f);

    public static final StreamCodec<RegistryFriendlyByteBuf, GrappleWinchConnectionSyncPacket> PACKET_CODEC = new StreamCodec<>() {
        @Override
        public GrappleWinchConnectionSyncPacket decode(RegistryFriendlyByteBuf buf) {
            int connectionId = ByteBufCodecs.VAR_INT.decode(buf);
            int playerId = ByteBufCodecs.VAR_INT.decode(buf);
            int hookId = ByteBufCodecs.VAR_INT.decode(buf);
            Vec3 playerFallbackPos = VEC3D_PACKET_CODEC.decode(buf);
            Vec3 hookFallbackPos = VEC3D_PACKET_CODEC.decode(buf);
            boolean hookAnchored = ByteBufCodecs.BOOL.decode(buf);
            double cableLength = ByteBufCodecs.DOUBLE.decode(buf);
            double maxCableLength = ByteBufCodecs.DOUBLE.decode(buf);
            return new GrappleWinchConnectionSyncPacket(connectionId, playerId, hookId, playerFallbackPos, hookFallbackPos, hookAnchored, cableLength, maxCableLength);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, GrappleWinchConnectionSyncPacket packet) {
            ByteBufCodecs.VAR_INT.encode(buf, packet.connectionId());
            ByteBufCodecs.VAR_INT.encode(buf, packet.playerId());
            ByteBufCodecs.VAR_INT.encode(buf, packet.hookId());
            VEC3D_PACKET_CODEC.encode(buf, packet.playerFallbackPos());
            VEC3D_PACKET_CODEC.encode(buf, packet.hookFallbackPos());
            ByteBufCodecs.BOOL.encode(buf, packet.hookAnchored());
            ByteBufCodecs.DOUBLE.encode(buf, packet.cableLength());
            ByteBufCodecs.DOUBLE.encode(buf, packet.maxCableLength());
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
