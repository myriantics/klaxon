package net.myriantics.klaxon.networking.s2c;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.myriantics.klaxon.block.customblocks.machines.blast_processor.deepslate.DeepslateBlastProcessorScreenHandler;
import net.myriantics.klaxon.registry.misc.KlaxonPackets;

import java.util.List;

public record BlastProcessorScreenSyncPacket(double explosionPowerMin, double explosionPowerMax, List<ItemStack> displayStacks, double explosionPower, boolean producesFire) implements CustomPayload {

    public static final CustomPayload.Id<BlastProcessorScreenSyncPacket> ID = new CustomPayload.Id<>(KlaxonPackets.BLAST_PROCESSOR_SCREEN_SYNC_PACKET_S2C_ID);

    // beeg packet
    public static final PacketCodec<RegistryByteBuf, BlastProcessorScreenSyncPacket> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.DOUBLE, BlastProcessorScreenSyncPacket::explosionPowerMin,
            PacketCodecs.DOUBLE, BlastProcessorScreenSyncPacket::explosionPowerMax,
            ItemStack.LIST_PACKET_CODEC, BlastProcessorScreenSyncPacket::displayStacks,
            PacketCodecs.DOUBLE, BlastProcessorScreenSyncPacket::explosionPower,
            PacketCodecs.BOOL, BlastProcessorScreenSyncPacket::producesFire,
            BlastProcessorScreenSyncPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public void execute(ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            MinecraftClient client = context.client();

            if (client.player != null && client.player.currentScreenHandler instanceof DeepslateBlastProcessorScreenHandler screenHandler) {
                screenHandler.setRecipeData(
                        explosionPower,
                        explosionPowerMin,
                        explosionPowerMax,
                        producesFire
                );
            }
        });
    }
}
