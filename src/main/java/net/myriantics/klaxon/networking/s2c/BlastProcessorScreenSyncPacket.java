package net.myriantics.klaxon.networking.s2c;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.myriantics.klaxon.registry.misc.KlaxonPackets;

import java.util.List;

public record BlastProcessorScreenSyncPacket(double explosionPowerMin, double explosionPowerMax, List<ItemStack> displayStacks, double explosionPower, boolean producesFire) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<BlastProcessorScreenSyncPacket> ID = new CustomPacketPayload.Type<>(KlaxonPackets.BLAST_PROCESSOR_SCREEN_SYNC_PACKET_S2C_ID);

    // beeg packet
    public static final StreamCodec<RegistryFriendlyByteBuf, BlastProcessorScreenSyncPacket> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, BlastProcessorScreenSyncPacket::explosionPowerMin,
            ByteBufCodecs.DOUBLE, BlastProcessorScreenSyncPacket::explosionPowerMax,
            ItemStack.LIST_STREAM_CODEC, BlastProcessorScreenSyncPacket::displayStacks,
            ByteBufCodecs.DOUBLE, BlastProcessorScreenSyncPacket::explosionPower,
            ByteBufCodecs.BOOL, BlastProcessorScreenSyncPacket::producesFire,
            BlastProcessorScreenSyncPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
