package net.myriantics.klaxon.registry.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.myriantics.klaxon.infra.reloadable_registry.*;
import net.myriantics.klaxon.networking.KlaxonClientPlayNetworkHandler;
import net.myriantics.klaxon.registry.misc.KlaxonPackets;

import java.util.Map;
import java.util.Objects;

public abstract class KlaxonClientPackets {

    // client only
    public static void registerS2CPacketRecievers() {
        registerReciever(KlaxonPackets.BLAST_PROCESSOR_SCREEN_SYNC_PACKET_S2C_ID, KlaxonClientPlayNetworkHandler::blastProcessorScreenSync);
        registerReciever(KlaxonPackets.KLAXON_WORLD_EVENT_TRIGGER_PACKET_S2C_ID, KlaxonClientPlayNetworkHandler::processKlaxonWorldEvent);
        registerReciever(KlaxonPackets.GRAPPLE_WINCH_CONNECTION_SYNC_S2C_ID, KlaxonClientPlayNetworkHandler::grappleWinchConnectionSync);
        registerReciever(KlaxonPackets.GRAPPLE_WINCH_CONNECTION_DISCARD_S2C_ID, KlaxonClientPlayNetworkHandler::grappleWinchConnectionDiscard);
        registerReciever(KlaxonPackets.ITEM_USAGE_LOCKOUT_TRIGGER_S2C_ID, KlaxonClientPlayNetworkHandler::triggerItemUsageLockout);
        registerReciever(KlaxonPackets.STEEL_BLAST_PROCESSOR_EXHAUST_LAUNCH_S2C_ID, KlaxonClientPlayNetworkHandler::applyVelocity);

        for (Map.Entry<ResourceKey<ReloadableRegistryTemplate<? extends KlaxonResourceReloadListener<?>, ?>>, CustomPacketPayload.Type<?>> entry : ReloadableRegistryManager.SYNCED_REGISTRY_MAP.entrySet()) {
            registerReciever(
                    entry.getValue(),
                    Objects.requireNonNull((payload, context) ->
                            Objects.requireNonNull(ReloadableRegistryManager.get(context.client()))
                                    .getSyncInstance(entry.getKey())
                                    .synchronizer()
                                    .loadFromPacket(payload)
                    )
            );
        }
    }

    private static <T extends CustomPacketPayload> void registerReciever(CustomPacketPayload.Type<T> type, ClientPlayNetworking.PlayPayloadHandler<T> handler) {
        ClientPlayNetworking.registerGlobalReceiver(type, handler);
    }
}
