package net.myriantics.klaxon.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.myriantics.klaxon.KlaxonMain;
import net.myriantics.klaxon.block.blockentities.blast_processor.BlastProcessorBlockEntity;

public class KlaxonMessages {
    public static final Identifier FAST_INPUT_SYNC_S2C = new Identifier(KlaxonMain.MOD_ID, "fast_input_sync_s2c");

    public static void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(FAST_INPUT_SYNC_S2C, ((client, handler, buf, responseSender) -> {
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
        }));
    }

    public static void registerC2SPackets() {
    }
}
