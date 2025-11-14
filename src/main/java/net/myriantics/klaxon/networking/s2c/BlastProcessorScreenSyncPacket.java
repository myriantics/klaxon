package net.myriantics.klaxon.networking.s2c;

import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorScreenHandler;
import net.myriantics.klaxon.registry.misc.KlaxonPackets;

import java.util.Arrays;

public record BlastProcessorScreenSyncPacket(double explosionPowerMin, double explosionPowerMax, ItemStack[] displayStacks, double explosionPower, boolean producesFire) implements CustomPayload {

    public static final CustomPayload.Id<BlastProcessorScreenSyncPacket> ID = new CustomPayload.Id<>(KlaxonPackets.BLAST_PROCESSOR_SCREEN_SYNC_PACKET_S2C_ID);

    // beeg packet
    public static final PacketCodec<RegistryByteBuf, BlastProcessorScreenSyncPacket> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.DOUBLE, BlastProcessorScreenSyncPacket::explosionPowerMin,
            PacketCodecs.DOUBLE, BlastProcessorScreenSyncPacket::explosionPowerMax,
            ItemStack.LIST_PACKET_CODEC.<ItemStack[]>xmap(
                    (stacks -> stacks.toArray(new ItemStack[0])),
                    (Arrays::asList)
            ), BlastProcessorScreenSyncPacket::displayStacks,
            PacketCodecs.DOUBLE, BlastProcessorScreenSyncPacket::explosionPower,
            PacketCodecs.BOOL, BlastProcessorScreenSyncPacket::producesFire,
            BlastProcessorScreenSyncPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
