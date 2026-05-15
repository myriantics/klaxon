package net.myriantics.klaxon.registry.misc;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.networking.c2s.GrappleWinchCableForceDisconnectC2S;
import net.myriantics.klaxon.networking.c2s.GrappleWinchCableLengthUpdateC2S;
import net.myriantics.klaxon.networking.c2s.HammerWalljumpTriggerPacket;
import net.myriantics.klaxon.networking.s2c.*;

public abstract class KlaxonPackets {

    public static final ResourceLocation STEEL_BLAST_PROCESSOR_EXHAUST_LAUNCH_S2C_ID = locateS2C("steel_blast_processor_exhaust_launch");
    public static final ResourceLocation GRAPPLE_WINCH_CONNECTION_SYNC_S2C_ID = locateS2C("grapple_winch_connection_sync");
    public static final ResourceLocation GRAPPLE_WINCH_CONNECTION_DISCARD_S2C_ID = locateS2C("grapple_winch_connection_discard");
    public static final ResourceLocation GRAPPLE_WINCH_CABLE_FORCE_DISCONNECT_C2S_ID = locateC2S("grapple_winch_cable_force_disconnect");
    public static final ResourceLocation GRAPPLE_WINCH_CABLE_LENGTH_UPDATE_C2S_ID = locateC2S("grapple_winch_cable_length_update");
    public static final ResourceLocation ITEM_USAGE_LOCKOUT_TRIGGER_S2C_ID = locateS2C("item_usage_lockout");
    public static final ResourceLocation BLAST_PROCESSOR_SCREEN_SYNC_PACKET_S2C_ID = locateS2C("blast_processor_screen_sync");
    public static final ResourceLocation KLAXON_WORLD_EVENT_TRIGGER_PACKET_S2C_ID = locateS2C("klaxon_world_event");
    public static final ResourceLocation HAMMER_WALLJUMP_TRIGGER_PACKET_C2S_ID = locateC2S("hammer_walljump_trigger_packet");

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Packets!");

        // s2c
        PayloadTypeRegistry.playS2C().register(SteelBlastProcessorExhaustLaunchPacket.ID, SteelBlastProcessorExhaustLaunchPacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(BlastProcessorMenuPowerSyncPacket.ID, BlastProcessorMenuPowerSyncPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(KlaxonWorldEventPacket.ID, KlaxonWorldEventPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(GrappleWinchConnectionSyncPacket.ID, GrappleWinchConnectionSyncPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(GrappleWinchConnectionDiscardPacket.ID, GrappleWinchConnectionDiscardPacket.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(ItemUsageLockoutTrigger.ID, ItemUsageLockoutTrigger.PACKET_CODEC);

        // c2s
        PayloadTypeRegistry.playC2S().register(HammerWalljumpTriggerPacket.ID, HammerWalljumpTriggerPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(GrappleWinchCableForceDisconnectC2S.ID, GrappleWinchCableForceDisconnectC2S.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(GrappleWinchCableLengthUpdateC2S.ID, GrappleWinchCableLengthUpdateC2S.PACKET_CODEC);
    }

    // server only
    public static void initC2SRecievers() {
        ServerPlayNetworking.registerGlobalReceiver(HammerWalljumpTriggerPacket.ID, HammerWalljumpTriggerPacket::execute);
        ServerPlayNetworking.registerGlobalReceiver(GrappleWinchCableForceDisconnectC2S.ID, GrappleWinchCableForceDisconnectC2S::execute);
        ServerPlayNetworking.registerGlobalReceiver(GrappleWinchCableLengthUpdateC2S.ID, GrappleWinchCableLengthUpdateC2S::execute);
    }

    private static ResourceLocation locateS2C(String name) {
        return KlaxonCommon.locate(name + "_s2c");
    }

    private static ResourceLocation locateC2S(String name) {
        return KlaxonCommon.locate(name + "_c2s");
    }
}
