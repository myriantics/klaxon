package net.myriantics.klaxon.networking.c2s;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.myriantics.klaxon.registry.minecraft.KlaxonPackets;

public record EntityDualWieldToggleC2SPacket(boolean isDualWielding) implements CustomPayload {

    public static final Id<EntityDualWieldToggleC2SPacket> ID = new Id<>(KlaxonPackets.DUAL_WIELD_TOGGLE_BIDIRECTIONAL_PACKET);

    public static final PacketCodec<RegistryByteBuf, EntityDualWieldToggleC2SPacket> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.BOOL, EntityDualWieldToggleC2SPacket::isDualWielding,
            EntityDualWieldToggleC2SPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

}
