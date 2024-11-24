package net.myriantics.klaxon.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.blockentities.blast_processor.DeepslateBlastProcessorScreenHandler;
import net.myriantics.klaxon.networking.packets.BlastProcessorScreenSyncPacket;

public class KlaxonPackets {
    public static final Identifier BLAST_PROCESSOR_SCREEN_SYNC_PACKET_S2C_ID = KlaxonCommon.locate("blast_processor_screen_sync_s2c");

    public static void registerModPackets() {
        KlaxonCommon.LOGGER.info("Registering KLAXON's Packets!");

        // s2c
        PayloadTypeRegistry.playS2C().register(BlastProcessorScreenSyncPacket.ID, BlastProcessorScreenSyncPacket.CODEC);

        // c2s
    }

    // client only
    public static void registerS2CPacketRecievers() {
        ClientPlayNetworking.registerGlobalReceiver(BlastProcessorScreenSyncPacket.ID, ((payload, context) -> {
            context.client().execute(() -> {
                MinecraftClient client = context.client();

                if (client.player != null && client.player.currentScreenHandler instanceof DeepslateBlastProcessorScreenHandler screenHandler) {
                    screenHandler.setRecipeData(payload.explosionPower(),
                            payload.explosionPowerMin(),
                            payload.explosionPowerMax(),
                            payload.producesFire(),
                            payload.outputState());
                }
            });
        }));
    }

    // server only
    public static void registerC2SPacketRecievers() {

    }
}
