package net.myriantics.klaxon.networking.s2c;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.myriantics.klaxon.registry.misc.KlaxonPackets;
import net.myriantics.klaxon.registry.misc.KlaxonWorldEvents;
import org.joml.Vector3f;

public record KlaxonWorldEventPacket(int eventId, Vector3f position, int data, boolean global) implements CustomPacketPayload{
    public KlaxonWorldEventPacket(int eventId, BlockPos pos, int data, boolean global) {
        this(eventId, new Vector3f(pos.getX(), pos.getY(), pos.getZ()), data, global);
    }

    public static final CustomPacketPayload.Type<KlaxonWorldEventPacket> ID = new CustomPacketPayload.Type<>(KlaxonPackets.KLAXON_WORLD_EVENT_TRIGGER_PACKET_S2C_ID);

    public static StreamCodec<FriendlyByteBuf, KlaxonWorldEventPacket> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, KlaxonWorldEventPacket::eventId,
            ByteBufCodecs.VECTOR3F, KlaxonWorldEventPacket::position,
            ByteBufCodecs.INT, KlaxonWorldEventPacket::data,
            ByteBufCodecs.BOOL, KlaxonWorldEventPacket::global,
            KlaxonWorldEventPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
