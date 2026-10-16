package net.myriantics.klaxon.infra;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface ClientSynced<T extends CustomPacketPayload> {
    CustomPacketPayload.Type<T> getType();

    T getSyncPacket();

    void loadFromPacket(T packet);
}
