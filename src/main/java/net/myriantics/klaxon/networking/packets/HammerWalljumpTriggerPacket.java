package net.myriantics.klaxon.networking.packets;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.myriantics.klaxon.registry.minecraft.KlaxonPackets;

public record HammerWalljumpTriggerPacket(BlockPos pos, Direction direction) implements CustomPayload {

    public static final CustomPayload.Id<HammerWalljumpTriggerPacket> ID = new CustomPayload.Id<>(KlaxonPackets.HAMMER_WALLJUMP_TRIGGER_PACKET_C2S_ID);

    public static final PacketCodec<RegistryByteBuf, HammerWalljumpTriggerPacket> PACKET_CODEC = PacketCodec.tuple(
            BlockPos.PACKET_CODEC, HammerWalljumpTriggerPacket::pos,
            Direction.PACKET_CODEC, HammerWalljumpTriggerPacket::direction,
            HammerWalljumpTriggerPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
