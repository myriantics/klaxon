package net.myriantics.klaxon.networking.s2c;


import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.myriantics.klaxon.mechanics.grapple_winch.CableDetachmentReason;
import net.myriantics.klaxon.registry.misc.KlaxonPackets;

public record GrappleWinchConnectionDiscardPacket(int connectionId, CableDetachmentReason reason) implements CustomPacketPayload{
    public static final CustomPacketPayload.Type<GrappleWinchConnectionDiscardPacket> ID = new CustomPacketPayload.Type<>(KlaxonPackets.GRAPPLE_WINCH_CONNECTION_DISCARD_S2C_ID);

    public static StreamCodec<RegistryFriendlyByteBuf, GrappleWinchConnectionDiscardPacket> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, GrappleWinchConnectionDiscardPacket::connectionId,
            CableDetachmentReason.PACKET_CODEC, GrappleWinchConnectionDiscardPacket::reason,
            GrappleWinchConnectionDiscardPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
