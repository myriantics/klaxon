package net.myriantics.klaxon.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.blockentities.blast_processor.DeepslateBlastProcessorScreenHandler;
import net.myriantics.klaxon.item.KlaxonItems;
import net.myriantics.klaxon.item.tools.HammerItem;
import net.myriantics.klaxon.networking.packets.BlastProcessorScreenSyncPacket;
import net.myriantics.klaxon.networking.packets.HammerWalljumpTriggerPacket;

public class KlaxonPackets {
    public static final Identifier BLAST_PROCESSOR_SCREEN_SYNC_PACKET_S2C_ID = KlaxonCommon.locate("blast_processor_screen_sync_s2c");
    public static final Identifier HAMMER_WALLJUMP_TRIGGER_PACKET_C2S_ID = KlaxonCommon.locate("hammer_walljump_trigger_packet_c2s");

    public static void registerModPackets() {
        KlaxonCommon.LOGGER.info("Registering KLAXON's Packets!");

        // s2c
        PayloadTypeRegistry.playS2C().register(BlastProcessorScreenSyncPacket.ID, BlastProcessorScreenSyncPacket.PACKET_CODEC);

        // c2s
        PayloadTypeRegistry.playC2S().register(HammerWalljumpTriggerPacket.ID, HammerWalljumpTriggerPacket.PACKET_CODEC);
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
        ServerPlayNetworking.registerGlobalReceiver(HammerWalljumpTriggerPacket.ID, ((payload, context) -> {
            context.server().execute(() -> {
                ServerPlayerEntity player = context.player();

                // run the walljump ability :D
                HammerItem.processHammerWalljump(player, player.getWorld(), payload.pos(), payload.direction());
            });
        }));
    }
}
