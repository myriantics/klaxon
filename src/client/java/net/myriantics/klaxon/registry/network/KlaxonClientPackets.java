package net.myriantics.klaxon.registry.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.myriantics.klaxon.networking.KlaxonClientPlayNetworkHandler;
import net.myriantics.klaxon.networking.s2c.*;

public abstract class KlaxonClientPackets {

    // client only
    public static void registerS2CPacketRecievers() {
        ClientPlayNetworking.registerGlobalReceiver(BlastProcessorMenuPowerSyncPacket.ID, KlaxonClientPlayNetworkHandler::blastProcessorScreenSync);
        ClientPlayNetworking.registerGlobalReceiver(KlaxonWorldEventPacket.ID, KlaxonClientPlayNetworkHandler::processKlaxonWorldEvent);
        ClientPlayNetworking.registerGlobalReceiver(GrappleWinchConnectionSyncPacket.ID, KlaxonClientPlayNetworkHandler::grappleWinchConnectionSync);
        ClientPlayNetworking.registerGlobalReceiver(GrappleWinchConnectionDiscardPacket.ID, KlaxonClientPlayNetworkHandler::grappleWinchConnectionDiscard);
        ClientPlayNetworking.registerGlobalReceiver(ItemUsageLockoutTrigger.ID, KlaxonClientPlayNetworkHandler::triggerItemUsageLockout);
        ClientPlayNetworking.registerGlobalReceiver(SteelBlastProcessorExhaustLaunchPacket.ID, KlaxonClientPlayNetworkHandler::applyVelocity);
    }
}
