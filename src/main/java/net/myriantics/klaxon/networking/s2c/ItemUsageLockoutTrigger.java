package net.myriantics.klaxon.networking.s2c;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.myriantics.klaxon.mechanics.item_usage_lockout.MinecraftClientUsageLockoutAccess;
import net.myriantics.klaxon.registry.misc.KlaxonPackets;

public record ItemUsageLockoutTrigger() implements CustomPacketPayload {
    @Override
    public Type<ItemUsageLockoutTrigger> type() {
        return ID;
    }

    public static CustomPacketPayload.Type<ItemUsageLockoutTrigger> ID = new CustomPacketPayload.Type<>(KlaxonPackets.ITEM_USAGE_LOCKOUT_TRIGGER_S2C_ID);

    public static StreamCodec<RegistryFriendlyByteBuf, ItemUsageLockoutTrigger> PACKET_CODEC = StreamCodec.unit(new ItemUsageLockoutTrigger());
}
