package net.myriantics.klaxon.networking.clientbound;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.myriantics.klaxon.registry.misc.KlaxonPackets;

public record ItemUsageLockoutTrigger() implements CustomPacketPayload {
    @Override
    public Type<ItemUsageLockoutTrigger> type() {
        return KlaxonPackets.ITEM_USAGE_LOCKOUT_TRIGGER_S2C_ID;
    }

    public static StreamCodec<RegistryFriendlyByteBuf, ItemUsageLockoutTrigger> PACKET_CODEC = StreamCodec.unit(new ItemUsageLockoutTrigger());
}
