package net.myriantics.klaxon.networking.s2c;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.myriantics.klaxon.block.machines.blast_processor.AbstractBlastProcessorMenu;
import net.myriantics.klaxon.registry.misc.KlaxonPackets;
import net.myriantics.klaxon.util.storage.item.KlaxonClientMenuInitializer;

public record BlastProcessorMenuPowerSyncPacket(double explosionPowerMin, double explosionPowerMax, double explosionPower, boolean producesFire) implements CustomPacketPayload, KlaxonClientMenuInitializer<AbstractBlastProcessorMenu> {

    public static final CustomPacketPayload.Type<BlastProcessorMenuPowerSyncPacket> ID = new CustomPacketPayload.Type<>(KlaxonPackets.BLAST_PROCESSOR_SCREEN_SYNC_PACKET_S2C_ID);

    // beeg packet
    public static final StreamCodec<RegistryFriendlyByteBuf, BlastProcessorMenuPowerSyncPacket> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, BlastProcessorMenuPowerSyncPacket::explosionPowerMin,
            ByteBufCodecs.DOUBLE, BlastProcessorMenuPowerSyncPacket::explosionPowerMax,
            ByteBufCodecs.DOUBLE, BlastProcessorMenuPowerSyncPacket::explosionPower,
            ByteBufCodecs.BOOL, BlastProcessorMenuPowerSyncPacket::producesFire,
            BlastProcessorMenuPowerSyncPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    @Override
    public int getContainerSize() {
        return 0;
    }

    @Override
    public void initialize(AbstractBlastProcessorMenu menu) {
        menu.updatePowerData(this);
    }
}
