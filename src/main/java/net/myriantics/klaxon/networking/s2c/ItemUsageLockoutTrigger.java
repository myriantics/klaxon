package net.myriantics.klaxon.networking.s2c;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.myriantics.klaxon.registry.misc.KlaxonPackets;

public record ItemUsageLockoutTrigger() implements CustomPayload {
    @Override
    public Id<ItemUsageLockoutTrigger> getId() {
        return ID;
    }

    public static CustomPayload.Id<ItemUsageLockoutTrigger> ID = new CustomPayload.Id<>(KlaxonPackets.ITEM_USAGE_LOCKOUT_TRIGGER_S2C_ID);

    public static PacketCodec<RegistryByteBuf, ItemUsageLockoutTrigger> PACKET_CODEC = PacketCodec.unit(new ItemUsageLockoutTrigger());
}
