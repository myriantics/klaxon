package net.myriantics.klaxon.registry;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.customblocks.blast_processor.deepslate.DeepslateBlastProcessorScreenHandler;
import net.myriantics.klaxon.item.equipment.tools.HammerItem;
import net.myriantics.klaxon.networking.packets.BlastProcessorScreenSyncPacket;
import net.myriantics.klaxon.networking.packets.HammerWalljumpTriggerPacket;

public class KlaxonPackets {
    public static final Identifier BLAST_PROCESSOR_SCREEN_SYNC_PACKET_S2C_ID = KlaxonCommon.locate("blast_processor_screen_sync_s2c");
    public static final Identifier HAMMER_WALLJUMP_TRIGGER_PACKET_C2S_ID = KlaxonCommon.locate("hammer_walljump_trigger_packet_c2s");

    public static void registerModPackets() {
        KlaxonCommon.LOGGER.info("Registering KLAXON's Packets!");
    }

    // client only
    public static void registerS2CPacketRecievers() {
        ClientPlayNetworking.registerGlobalReceiver(BLAST_PROCESSOR_SCREEN_SYNC_PACKET_S2C_ID, ((client, handler, buf, responseSender) -> {
            buf.retain();
            client.execute(() -> {
                BlastProcessorScreenSyncPacket payload = BlastProcessorScreenSyncPacket.decode(buf);

                if (client.player != null && client.player.currentScreenHandler instanceof DeepslateBlastProcessorScreenHandler screenHandler) {
                    if (screenHandler.syncId == payload.syncId())
                        screenHandler.setRecipeData(payload);
                }
            });
        }));
    }

    // server only
    public static void registerC2SPacketRecievers() {
        ServerPlayNetworking.registerGlobalReceiver(HAMMER_WALLJUMP_TRIGGER_PACKET_C2S_ID, ((server, player, handler, buf, responseSender) -> {
            buf.retain();
            server.execute(() -> {
                HammerWalljumpTriggerPacket payload = HammerWalljumpTriggerPacket.decode(buf);

                HammerItem.processHammerWalljump(player, player.getWorld(), payload.pos(), payload.direction());
            });
        }));
    }
}
