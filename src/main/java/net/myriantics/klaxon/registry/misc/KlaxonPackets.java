package net.myriantics.klaxon.registry.misc;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.networking.c2s.GrappleWinchCableForceDisconnectC2S;
import net.myriantics.klaxon.networking.s2c.*;
import net.myriantics.klaxon.networking.c2s.EntityDualWieldToggleC2SPacket;
import net.myriantics.klaxon.networking.c2s.HammerWalljumpTriggerPacket;

public abstract class KlaxonPackets {

    public static final Identifier GRAPPLE_WINCH_CONNECTION_SYNC_S2C_ID = locateS2C("grapple_winch_connection_sync");
    public static final Identifier GRAPPLE_WINCH_CONNECTION_DISCARD_S2C_ID = locateS2C("grapple_winch_connection_discard");
    public static final Identifier GRAPPLE_WINCH_CABLE_FORCE_DISCONNECT_C2S_ID = locateC2S("grapple_winch_cable_force_disconnect");
    public static final Identifier ITEM_USAGE_LOCKOUT_TRIGGER_S2C_ID = locateS2C("item_usage_lockout");
    public static final Identifier BLAST_PROCESSOR_SCREEN_SYNC_PACKET_S2C_ID = locateS2C("blast_processor_screen_sync");
    public static final Identifier KLAXON_WORLD_EVENT_TRIGGER_PACKET_S2C_ID = locateS2C("klaxon_world_event");
    public static final Identifier HAMMER_WALLJUMP_TRIGGER_PACKET_C2S_ID = locateC2S("hammer_walljump_trigger_packet");
    public static final Identifier DUAL_WIELD_TOGGLE_S2C_PACKET = locateS2C("dual_wield_toggle");
    public static final Identifier DUAL_WIELD_TOGGLE_C2S_PACKET = locateC2S("dual_wield_toggle");

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Packets!");

        // s2c
        PayloadTypeRegistry.playS2C().register(BlastProcessorScreenSyncPacket.ID, BlastProcessorScreenSyncPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(EntityDualWieldToggleS2CPacket.ID, EntityDualWieldToggleS2CPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(KlaxonWorldEventPacket.ID, KlaxonWorldEventPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(GrappleWinchConnectionSyncPacket.ID, GrappleWinchConnectionSyncPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(GrappleWinchConnectionDiscardPacket.ID, GrappleWinchConnectionDiscardPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(ItemUsageLockoutTrigger.ID, ItemUsageLockoutTrigger.PACKET_CODEC);

        // c2s
        PayloadTypeRegistry.playC2S().register(HammerWalljumpTriggerPacket.ID, HammerWalljumpTriggerPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(EntityDualWieldToggleC2SPacket.ID, EntityDualWieldToggleC2SPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(GrappleWinchCableForceDisconnectC2S.ID, GrappleWinchCableForceDisconnectC2S.PACKET_CODEC);
    }

    // client only
    public static void registerS2CPacketRecievers() {
        ClientPlayNetworking.registerGlobalReceiver(BlastProcessorScreenSyncPacket.ID, BlastProcessorScreenSyncPacket::execute);
        ClientPlayNetworking.registerGlobalReceiver(EntityDualWieldToggleS2CPacket.ID, EntityDualWieldToggleS2CPacket::execute);
        ClientPlayNetworking.registerGlobalReceiver(KlaxonWorldEventPacket.ID, KlaxonWorldEventPacket::execute);
        ClientPlayNetworking.registerGlobalReceiver(GrappleWinchConnectionSyncPacket.ID, GrappleWinchConnectionSyncPacket::execute);
        ClientPlayNetworking.registerGlobalReceiver(GrappleWinchConnectionDiscardPacket.ID, GrappleWinchConnectionDiscardPacket::execute);
        ClientPlayNetworking.registerGlobalReceiver(ItemUsageLockoutTrigger.ID, ItemUsageLockoutTrigger::execute);
    }

    // server only
    public static void initC2SRecievers() {
        ServerPlayNetworking.registerGlobalReceiver(HammerWalljumpTriggerPacket.ID, HammerWalljumpTriggerPacket::execute);
        ServerPlayNetworking.registerGlobalReceiver(EntityDualWieldToggleC2SPacket.ID, EntityDualWieldToggleC2SPacket::execute);
        ServerPlayNetworking.registerGlobalReceiver(GrappleWinchCableForceDisconnectC2S.ID, GrappleWinchCableForceDisconnectC2S::execute);
    }

    private static Identifier locateS2C(String name) {
        return KlaxonCommon.locate(name + "_s2c");
    }

    private static Identifier locateC2S(String name) {
        return KlaxonCommon.locate(name + "_c2s");
    }
}
