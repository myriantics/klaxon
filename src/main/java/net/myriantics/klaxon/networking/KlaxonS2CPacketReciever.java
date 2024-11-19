package net.myriantics.klaxon.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.myriantics.klaxon.block.blockentities.blast_processor.BlastProcessorBlockEntity;
import net.myriantics.klaxon.block.blockentities.blast_processor.BlastProcessorScreenHandler;
import net.myriantics.klaxon.recipes.blast_processing.BlastProcessorOutputState;

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
                BlastProcessorOutputState outputState = buf.readEnumConstant(BlastProcessorOutputState.class);

                if (client.player != null && client.player.currentScreenHandler instanceof BlastProcessorScreenHandler screenHandler) {
                    if (client.player.currentScreenHandler.syncId == syncId) {
                        screenHandler.setRecipeData(explosionPower, explosionPowerMin, explosionPowerMax, producesFire, outputState);
                    }
                }
            });
        });
    }
}
