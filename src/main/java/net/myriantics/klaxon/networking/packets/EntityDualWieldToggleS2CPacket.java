package net.myriantics.klaxon.networking.packets;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.myriantics.klaxon.registry.minecraft.KlaxonPackets;

public record EntityDualWieldToggleS2CPacket(int entityId, boolean isDualWielding) implements CustomPayload {

    public static final CustomPayload.Id<EntityDualWieldToggleS2CPacket> ID = new CustomPayload.Id<>(KlaxonPackets.DUAL_WIELD_TOGGLE_BIDIRECTIONAL_PACKET);

    public static final PacketCodec<RegistryByteBuf, EntityDualWieldToggleS2CPacket> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT, EntityDualWieldToggleS2CPacket::entityId,
            PacketCodecs.BOOL, EntityDualWieldToggleS2CPacket::isDualWielding,
            EntityDualWieldToggleS2CPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

}
