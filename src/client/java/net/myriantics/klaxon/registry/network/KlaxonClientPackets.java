package net.myriantics.klaxon.registry.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.myriantics.klaxon.infra.ClientSynced;
import net.myriantics.klaxon.infra.KlaxonResourceReloadListener;
import net.myriantics.klaxon.infra.ReloadableRegistryInstance;
import net.myriantics.klaxon.networking.KlaxonClientPlayNetworkHandler;
import net.myriantics.klaxon.registry.KlaxonBuiltInRegistries;
import net.myriantics.klaxon.registry.KlaxonReloadableRegistries;
import net.myriantics.klaxon.registry.misc.KlaxonPackets;
import org.jetbrains.annotations.Nullable;

public abstract class KlaxonClientPackets {

    // client only
    public static void registerS2CPacketRecievers() {
        registerReciever(KlaxonPackets.BLAST_PROCESSOR_SCREEN_SYNC_PACKET_S2C_ID, KlaxonClientPlayNetworkHandler::blastProcessorScreenSync);
        registerReciever(KlaxonPackets.KLAXON_WORLD_EVENT_TRIGGER_PACKET_S2C_ID, KlaxonClientPlayNetworkHandler::processKlaxonWorldEvent);
        registerReciever(KlaxonPackets.GRAPPLE_WINCH_CONNECTION_SYNC_S2C_ID, KlaxonClientPlayNetworkHandler::grappleWinchConnectionSync);
        registerReciever(KlaxonPackets.GRAPPLE_WINCH_CONNECTION_DISCARD_S2C_ID, KlaxonClientPlayNetworkHandler::grappleWinchConnectionDiscard);
        registerReciever(KlaxonPackets.ITEM_USAGE_LOCKOUT_TRIGGER_S2C_ID, KlaxonClientPlayNetworkHandler::triggerItemUsageLockout);
        registerReciever(KlaxonPackets.STEEL_BLAST_PROCESSOR_EXHAUST_LAUNCH_S2C_ID, KlaxonClientPlayNetworkHandler::applyVelocity);

        for (ReloadableRegistryInstance<? extends KlaxonResourceReloadListener<?>, ?, ?> instance : KlaxonBuiltInRegistries.RELOADABLE_REGISTRY) {
            @Nullable CustomPacketPayload.Type<? extends CustomPacketPayload> type = instance.getPacketType();
            if (type != null) {
                registerReciever(type, ((payload, context) -> {
                    KlaxonResourceReloadListener<?> listener = ((KlaxonReloadableRegistries.Access) context.client()).klaxon$getRegistry(instance.asHolder());
                    if (listener instanceof ClientSynced<?> synced) {
                        iLoveGenerics(synced, payload);
                    }
                }));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends CustomPacketPayload> void iLoveGenerics(ClientSynced<T> synced, CustomPacketPayload payload) {
        synced.loadFromPacket((T) payload);
    }

    private static  <T extends CustomPacketPayload> void registerReciever(CustomPacketPayload.Type<T> type, ClientPlayNetworking.PlayPayloadHandler<T> handler) {
        ClientPlayNetworking.registerGlobalReceiver(type, handler);
    }
}
