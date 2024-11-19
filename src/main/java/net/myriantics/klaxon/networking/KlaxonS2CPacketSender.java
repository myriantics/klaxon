package net.myriantics.klaxon.networking;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.myriantics.klaxon.recipes.blast_processing.BlastProcessingInator;

// netowrk :)))) (from spectrum github) (only inspiration no yoinkage here) (totally)
public class KlaxonS2CPacketSender {

    public static void sendBlastProcessorScreenSyncData(BlastProcessingInator inator, ServerPlayerEntity playerEntity, int syncId) {

        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeInt(syncId);
        buf.writeDouble(inator.getExplosionPower());
        buf.writeDouble(inator.getExplosionPowerMin());
        buf.writeDouble(inator.getExplosionPowerMax());
        buf.writeBoolean(inator.producesFire());
        buf.writeEnumConstant(inator.getOutputState());

        ServerPlayNetworking.send(playerEntity, KlaxonS2CPackets.BLAST_PROCESSOR_SCREEN_DATA_SYNC_S2C, buf);
    }
}
