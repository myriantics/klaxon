package net.myriantics.klaxon.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.myriantics.klaxon.block.blockentities.blast_processor.DeepslateBlastProcessorScreenHandler;
import net.myriantics.klaxon.recipes.blast_processing.BlastProcessingOutputState;

import static net.myriantics.klaxon.networking.KlaxonS2CPackets.BLAST_PROCESSOR_SCREEN_DATA_SYNC_S2C;

// networking class layout yoinked from spectrum github
public class KlaxonS2CPacketReciever {

    public static void registerS2CRecievers() {
        ClientPlayNetworking.registerGlobalReceiver(BLAST_PROCESSOR_SCREEN_DATA_SYNC_S2C, (client, handler, buf, responseSender) -> {
            buf.retain();
            client.execute(() -> {
                int syncId = buf.readInt();
                double explosionPower = buf.readDouble();
                double explosionPowerMin = buf.readDouble();
                double explosionPowerMax = buf.readDouble();
                boolean producesFire = buf.readBoolean();
                BlastProcessingOutputState outputState = buf.readEnumConstant(BlastProcessingOutputState.class);

                if (client.player != null && client.player.currentScreenHandler instanceof DeepslateBlastProcessorScreenHandler screenHandler) {
                    if (client.player.currentScreenHandler.syncId == syncId) {
                        screenHandler.setRecipeData(explosionPower, explosionPowerMin, explosionPowerMax, producesFire, outputState);
                    }
                }
            });
        });
    }
}
