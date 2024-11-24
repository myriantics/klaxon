package net.myriantics.klaxon.networking.packets;

import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.myriantics.klaxon.networking.KlaxonPackets;
import net.myriantics.klaxon.recipes.blast_processing.BlastProcessingOutputState;

public record BlastProcessorScreenSyncPacket(double explosionPowerMin, double explosionPowerMax, ItemStack result, BlastProcessingOutputState outputState, double explosionPower, boolean producesFire) implements CustomPayload {

    public static final CustomPayload.Id<BlastProcessorScreenSyncPacket> ID = new CustomPayload.Id<>(KlaxonPackets.BLAST_PROCESSOR_SCREEN_SYNC_PACKET_S2C_ID);

    // beeg packet
    public static final PacketCodec<RegistryByteBuf, BlastProcessorScreenSyncPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.DOUBLE, BlastProcessorScreenSyncPacket::explosionPowerMin,
            PacketCodecs.DOUBLE, BlastProcessorScreenSyncPacket::explosionPowerMax,
            ItemStack.PACKET_CODEC, BlastProcessorScreenSyncPacket::result,
            PacketCodecs.indexed((index) -> BlastProcessingOutputState.values()[index], Enum::ordinal), BlastProcessorScreenSyncPacket::outputState,
            PacketCodecs.DOUBLE, BlastProcessorScreenSyncPacket::explosionPower,
            PacketCodecs.BOOL, BlastProcessorScreenSyncPacket::producesFire,
            BlastProcessorScreenSyncPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
