package net.myriantics.klaxon.registry.misc;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.networking.serverbound.GrappleWinchCableForceDisconnectC2S;
import net.myriantics.klaxon.networking.serverbound.GrappleWinchCableLengthUpdateC2S;
import net.myriantics.klaxon.networking.serverbound.HammerWalljumpTriggerPacket;
import net.myriantics.klaxon.networking.clientbound.*;

public abstract class KlaxonPackets {

    // clientbound
    public static final CustomPacketPayload.Type<SteelBlastProcessorExhaustLaunchPacket> STEEL_BLAST_PROCESSOR_EXHAUST_LAUNCH_S2C_ID = registerClientbound("steel_blast_processor_exhaust_launch", SteelBlastProcessorExhaustLaunchPacket.STREAM_CODEC);
    public static final CustomPacketPayload.Type<GrappleWinchConnectionSyncPacket> GRAPPLE_WINCH_CONNECTION_SYNC_S2C_ID = registerClientbound("grapple_winch_connection_sync", GrappleWinchConnectionSyncPacket.PACKET_CODEC);
    public static final CustomPacketPayload.Type<GrappleWinchConnectionDiscardPacket> GRAPPLE_WINCH_CONNECTION_DISCARD_S2C_ID = registerClientbound("grapple_winch_connection_discard", GrappleWinchConnectionDiscardPacket.PACKET_CODEC);
    public static final CustomPacketPayload.Type<ItemUsageLockoutTrigger> ITEM_USAGE_LOCKOUT_TRIGGER_S2C_ID = registerClientbound("item_usage_lockout", ItemUsageLockoutTrigger.PACKET_CODEC);
    public static final CustomPacketPayload.Type<BlastProcessorMenuPowerSyncPacket> BLAST_PROCESSOR_SCREEN_SYNC_PACKET_S2C_ID = registerClientbound("blast_processor_screen_sync", BlastProcessorMenuPowerSyncPacket.PACKET_CODEC);
    public static final CustomPacketPayload.Type<KlaxonWorldEventPacket> KLAXON_WORLD_EVENT_TRIGGER_PACKET_S2C_ID = registerClientbound("klaxon_world_event", KlaxonWorldEventPacket.PACKET_CODEC);
    public static final CustomPacketPayload.Type<ClientboundDynamicMachinePartSyncPayload> CLIENTBOUND_DYNAMIC_MACHINE_PART_SYNC = registerClientbound("dynamic_machine_part_sync", ClientboundDynamicMachinePartSyncPayload.STREAM_CODEC);

    // serverbound
    public static final CustomPacketPayload.Type<GrappleWinchCableForceDisconnectC2S> GRAPPLE_WINCH_CABLE_FORCE_DISCONNECT_C2S_ID = registerServerbound("grapple_winch_cable_force_disconnect", GrappleWinchCableForceDisconnectC2S.PACKET_CODEC, GrappleWinchCableForceDisconnectC2S::execute);
    public static final CustomPacketPayload.Type<GrappleWinchCableLengthUpdateC2S> GRAPPLE_WINCH_CABLE_LENGTH_UPDATE_C2S_ID = registerServerbound("grapple_winch_cable_length_update", GrappleWinchCableLengthUpdateC2S.PACKET_CODEC, GrappleWinchCableLengthUpdateC2S::execute);
    public static final CustomPacketPayload.Type<HammerWalljumpTriggerPacket> HAMMER_WALLJUMP_TRIGGER_PACKET_C2S_ID = registerServerbound("hammer_walljump_trigger_packet", HammerWalljumpTriggerPacket.PACKET_CODEC, HammerWalljumpTriggerPacket::execute);

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Packets!");
    }

    private static <T extends CustomPacketPayload> CustomPacketPayload.Type<T> registerServerbound(String name, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec, ServerPlayNetworking.PlayPayloadHandler<T> handler) {
        CustomPacketPayload.Type<T> type = new CustomPacketPayload.Type<>(KlaxonCommon.locate("serverbound/" + name));
        PayloadTypeRegistry.playC2S().register(type, streamCodec);
        ServerPlayNetworking.registerGlobalReceiver(type, handler);
        return type;
    }

    private static <T extends CustomPacketPayload> CustomPacketPayload.Type<T> registerClientbound(String name, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        CustomPacketPayload.Type<T> type = new CustomPacketPayload.Type<>(KlaxonCommon.locate("clientbound/" + name));
        PayloadTypeRegistry.playS2C().register(type, streamCodec);
        return type;
    }
}
