package net.myriantics.klaxon.networking;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.myriantics.klaxon.recipes.blast_processing.BlastProcessingRecipeData;
import net.myriantics.klaxon.recipes.item_explosion_power.ItemExplosionPowerData;

// netowrk :)))) (from spectrum github) (only inspiration no yoinkage here) (totally)
public class KlaxonS2CPacketSender {

    public static void sendBlastProcessorScreenSyncData(BlastProcessingRecipeData blastProcessingRecipeData, ItemExplosionPowerData powerData, ServerPlayerEntity playerEntity, int syncId) {

        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeInt(syncId);
        buf.writeDouble(powerData.explosionPower());
        buf.writeDouble(blastProcessingRecipeData.explosionPowerMin());
        buf.writeDouble(blastProcessingRecipeData.explosionPowerMax());
        buf.writeBoolean(powerData.producesFire());
        buf.writeEnumConstant(blastProcessingRecipeData.outputState());

        ServerPlayNetworking.send(playerEntity, KlaxonS2CPackets.BLAST_PROCESSOR_SCREEN_DATA_SYNC_S2C, buf);
    }
}
