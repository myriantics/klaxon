package net.myriantics.klaxon.networking.s2c;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.myriantics.klaxon.registry.misc.KlaxonPackets;

public record KlaxonWorldEventPacket(ClientboundLevelEventPacket packet) implements CustomPacketPayload{
    public KlaxonWorldEventPacket(int eventId, BlockPos pos, int data, boolean global) {
        this(new ClientboundLevelEventPacket(eventId, pos, data, global));
    }

    public static final CustomPacketPayload.Type<KlaxonWorldEventPacket> ID = new CustomPacketPayload.Type<>(KlaxonPackets.KLAXON_WORLD_EVENT_TRIGGER_PACKET_S2C_ID);

    public static StreamCodec<FriendlyByteBuf, KlaxonWorldEventPacket> PACKET_CODEC = ClientboundLevelEventPacket.STREAM_CODEC.map(
            KlaxonWorldEventPacket::new,
            KlaxonWorldEventPacket::packet
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
