package net.myriantics.klaxon.networking.clientbound;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.myriantics.klaxon.mechanics.machinery.part.DynamicMachinePartDefinition;
import net.myriantics.klaxon.mechanics.machinery.part.KlaxonDynamicMachinePartLoader;
import net.myriantics.klaxon.registry.misc.KlaxonPackets;

import java.util.List;

public record ClientboundDynamicMachinePartSyncPayload(List<DynamicMachinePartDefinition> definitions) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundDynamicMachinePartSyncPayload> STREAM_CODEC = StreamCodec.composite(
            KlaxonDynamicMachinePartLoader.DEFINITION_LIST_STREAM_CODEC, ClientboundDynamicMachinePartSyncPayload::definitions,
            ClientboundDynamicMachinePartSyncPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return KlaxonPackets.CLIENTBOUND_DYNAMIC_MACHINE_PART_SYNC;
    }
}
