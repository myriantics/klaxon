package net.myriantics.klaxon.infra.reloadable_registry;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface ClientSynced<T extends CustomPacketPayload> {

    T getSyncPacket();

    void loadFromPacket(T packet);
}
