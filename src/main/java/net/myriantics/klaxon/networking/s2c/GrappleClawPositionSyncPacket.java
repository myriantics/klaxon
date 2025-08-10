package net.myriantics.klaxon.networking.s2c;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.myriantics.klaxon.registry.misc.KlaxonPackets;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Optional;

public record GrappleClawPositionSyncPacket(Optional<Vector3f> pos, int clawId) implements CustomPayload {

    public static final CustomPayload.Id<GrappleClawPositionSyncPacket> ID = new CustomPayload.Id<>(KlaxonPackets.GRAPPLE_CLAW_POSITION_SYNC_S2C_ID);

    public static final PacketCodec<RegistryByteBuf, GrappleClawPositionSyncPacket> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.optional(PacketCodecs.VECTOR3F), GrappleClawPositionSyncPacket::pos,
            PacketCodecs.VAR_INT, GrappleClawPositionSyncPacket::clawId,
            GrappleClawPositionSyncPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
