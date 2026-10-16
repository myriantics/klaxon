package net.myriantics.klaxon.networking.clientbound;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.Item;
import net.myriantics.klaxon.mechanics.machinery.part.KlaxonDynamicMachinePartLoader;
import net.myriantics.klaxon.registry.misc.KlaxonPackets;

import java.util.Map;

public record ClientboundDynamicMachinePartSyncPayload(Map<Item, DataComponentPatch> registry) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundDynamicMachinePartSyncPayload> STREAM_CODEC = StreamCodec.composite(
            KlaxonDynamicMachinePartLoader.PART_DATA_CACHE_CODEC, ClientboundDynamicMachinePartSyncPayload::registry,
            ClientboundDynamicMachinePartSyncPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return KlaxonPackets.CLIENTBOUND_DYNAMIC_MACHINE_PART_SYNC;
    }
}
