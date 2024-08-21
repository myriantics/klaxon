package net.myriantics.klaxon.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.myriantics.klaxon.KlaxonMain;
import net.myriantics.klaxon.block.blockentities.blast_processor.BlastProcessorBlockEntity;
import net.myriantics.klaxon.block.blockentities.blast_processor.BlastProcessorScreenHandler;
import net.myriantics.klaxon.recipes.blast_processing.BlastProcessorOutputState;

public class KlaxonMessages {
    public static final Identifier FAST_INPUT_SYNC_S2C = new Identifier(KlaxonMain.MOD_ID, "fast_input_sync_s2c");
    public static final Identifier BLAST_PROCESSOR_SCREEN_DATA_SYNC_S2C = new Identifier(KlaxonMain.MOD_ID, "blast_processor_screen_data_sync_s2c");

    public static void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(BLAST_PROCESSOR_SCREEN_DATA_SYNC_S2C, (client, handler, buf, responseSender) -> {
            buf.retain();
            client.execute(() -> {
                int syncId = buf.readInt();
                double explosionPower = buf.readDouble();
                double explosionPowerMin = buf.readDouble();
                double explosionPowerMax = buf.readDouble();
                boolean producesFire = buf.readBoolean();
                boolean requiresFire = buf.readBoolean();
                BlastProcessorOutputState outputState = buf.readEnumConstant(BlastProcessorOutputState.class);

                if (client.player != null && client.player.currentScreenHandler instanceof BlastProcessorScreenHandler screenHandler) {
                    if (client.player.currentScreenHandler.syncId == syncId) {
                        screenHandler.setRecipeData(explosionPower, explosionPowerMin, explosionPowerMax, producesFire, requiresFire, outputState);
                    }
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(FAST_INPUT_SYNC_S2C, (client, handler, buf, responseSender) -> {
            buf.retain();
            client.execute(() -> {
                World clientWorld = client.world;
                BlockPos pos = buf.readBlockPos();
                client.player.sendMessage(Text.literal("blockPos: " + pos));

                if (clientWorld != null && clientWorld.getBlockEntity(pos) instanceof BlastProcessorBlockEntity blastProcessor) {
                    for (int i = 0; i < blastProcessor.getItems().size(); i++) {
                        ItemStack activeStack = buf.readItemStack();
                        client.player.sendMessage(Text.literal("slot: " + i + ", itemStack: " + activeStack.toString()));
                        blastProcessor.setStack(i, activeStack);
                    }
                }
            });
        });
    }

    public static void registerC2SPackets() {
    }
}
