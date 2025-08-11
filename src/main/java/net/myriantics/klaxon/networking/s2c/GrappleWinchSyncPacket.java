package net.myriantics.klaxon.networking.s2c;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.myriantics.klaxon.registry.misc.KlaxonPackets;
import net.myriantics.klaxon.util.grapple_winch.GrappleWinchClientFallbackData;

import java.util.Optional;

public record GrappleWinchSyncPacket(Optional<GrappleWinchClientFallbackData> winchData, int clawId) implements CustomPayload {

    public static final CustomPayload.Id<GrappleWinchSyncPacket> ID = new CustomPayload.Id<>(KlaxonPackets.GRAPPLE_CLAW_POSITION_SYNC_S2C_ID);

    public static final PacketCodec<RegistryByteBuf, GrappleWinchSyncPacket> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.optional(GrappleWinchClientFallbackData.PACKET_CODEC), GrappleWinchSyncPacket::winchData,
            PacketCodecs.VAR_INT, GrappleWinchSyncPacket::clawId,
            GrappleWinchSyncPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
