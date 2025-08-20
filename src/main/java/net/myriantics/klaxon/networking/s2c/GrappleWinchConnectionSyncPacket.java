package net.myriantics.klaxon.networking.s2c;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.myriantics.klaxon.registry.misc.KlaxonPackets;
import net.myriantics.klaxon.util.grapple_winch.GrappleWinchConnectionData;

public record GrappleWinchConnectionSyncPacket(GrappleWinchConnectionData connectionData) implements CustomPayload {

    public static final CustomPayload.Id<GrappleWinchConnectionSyncPacket> ID = new CustomPayload.Id<>(KlaxonPackets.GRAPPLE_WINCH_CONNECTION_SYNC_S2C_ID);

    public static final PacketCodec<RegistryByteBuf, GrappleWinchConnectionSyncPacket> PACKET_CODEC = PacketCodec.tuple(
            GrappleWinchConnectionData.PACKET_CODEC, GrappleWinchConnectionSyncPacket::connectionData,
            GrappleWinchConnectionSyncPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
