package net.myriantics.klaxon.networking.packets;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeData;
import net.myriantics.klaxon.recipe.item_explosion_power.ItemExplosionPowerData;
import net.myriantics.klaxon.registry.KlaxonPackets;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingOutputState;

public record BlastProcessorScreenSyncPacket(int syncId, double explosionPowerMin, double explosionPowerMax, ItemStack result, BlastProcessingOutputState outputState, double explosionPower, boolean producesFire) {

    public static void send(ServerPlayerEntity serverPlayer, BlastProcessingRecipeData blastProcessingRecipeData, ItemExplosionPowerData explosionPowerData, int syncId) {
        PacketByteBuf buf = PacketByteBufs.create();

        ServerPlayNetworking.send(serverPlayer, KlaxonPackets.BLAST_PROCESSOR_SCREEN_SYNC_PACKET_S2C_ID,
                encode(buf, blastProcessingRecipeData, explosionPowerData, syncId));
    }

    public static PacketByteBuf encode(PacketByteBuf buf, BlastProcessingRecipeData blastProcessingRecipeData, ItemExplosionPowerData explosionPowerData, int syncId) {
        buf.writeInt(syncId);
        buf.writeDouble(blastProcessingRecipeData.explosionPowerMin());
        buf.writeDouble(blastProcessingRecipeData.explosionPowerMax());
        buf.writeItemStack(blastProcessingRecipeData.result());
        buf.writeEnumConstant(blastProcessingRecipeData.outputState());
        buf.writeDouble(explosionPowerData.explosionPower());
        buf.writeBoolean(explosionPowerData.producesFire());

        return buf;
    }

    public static BlastProcessorScreenSyncPacket decode(PacketByteBuf buf) {
        return new BlastProcessorScreenSyncPacket(
                buf.readInt(),
                buf.readDouble(),
                buf.readDouble(),
                buf.readItemStack(),
                buf.readEnumConstant(BlastProcessingOutputState.class),
                buf.readDouble(),
                buf.readBoolean()
        );
    }
}
