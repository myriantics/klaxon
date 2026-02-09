package net.myriantics.klaxon.networking.s2c;


import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.myriantics.klaxon.mechanics.grapple_winch.CableDetachmentReason;
import net.myriantics.klaxon.registry.misc.KlaxonPackets;

public record GrappleWinchConnectionDiscardPacket(int connectionId, CableDetachmentReason reason) implements CustomPayload{
    public static final CustomPayload.Id<GrappleWinchConnectionDiscardPacket> ID = new CustomPayload.Id<>(KlaxonPackets.GRAPPLE_WINCH_CONNECTION_DISCARD_S2C_ID);

    public static PacketCodec<RegistryByteBuf, GrappleWinchConnectionDiscardPacket> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT, GrappleWinchConnectionDiscardPacket::connectionId,
            CableDetachmentReason.PACKET_CODEC, GrappleWinchConnectionDiscardPacket::reason,
            GrappleWinchConnectionDiscardPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
