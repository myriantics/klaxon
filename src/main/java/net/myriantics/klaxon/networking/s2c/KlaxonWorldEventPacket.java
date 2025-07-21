package net.myriantics.klaxon.networking.s2c;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.myriantics.klaxon.registry.misc.KlaxonPackets;

public record KlaxonWorldEventPacket(WorldEventS2CPacket packet) implements CustomPayload{
    public KlaxonWorldEventPacket(int eventId, BlockPos pos, int data, boolean global) {
        this(new WorldEventS2CPacket(eventId, pos, data, global));
    }

    public static final CustomPayload.Id<KlaxonWorldEventPacket> ID = new CustomPayload.Id<>(KlaxonPackets.KLAXON_WORLD_EVENT_TRIGGER_PACKET_S2C_ID);

    public static PacketCodec<PacketByteBuf, KlaxonWorldEventPacket> PACKET_CODEC = WorldEventS2CPacket.CODEC.xmap(
            KlaxonWorldEventPacket::new,
            KlaxonWorldEventPacket::packet
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
