package net.myriantics.klaxon.networking.clientbound;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.myriantics.klaxon.registry.misc.KlaxonPackets;
import org.joml.Vector3f;

public record KlaxonWorldEventPacket(int eventId, Vector3f position, int data, boolean global) implements CustomPacketPayload{
    public KlaxonWorldEventPacket(int eventId, BlockPos pos, int data, boolean global) {
        this(eventId, new Vector3f(pos.getX(), pos.getY(), pos.getZ()), data, global);
    }

    public static StreamCodec<RegistryFriendlyByteBuf, KlaxonWorldEventPacket> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, KlaxonWorldEventPacket::eventId,
            ByteBufCodecs.VECTOR3F, KlaxonWorldEventPacket::position,
            ByteBufCodecs.INT, KlaxonWorldEventPacket::data,
            ByteBufCodecs.BOOL, KlaxonWorldEventPacket::global,
            KlaxonWorldEventPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return KlaxonPackets.KLAXON_WORLD_EVENT_TRIGGER_PACKET_S2C_ID;
    }
}
