package net.myriantics.klaxon.networking.packets;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingOutputState;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeData;
import net.myriantics.klaxon.recipe.item_explosion_power.ItemExplosionPowerData;
import net.myriantics.klaxon.registry.KlaxonPackets;

public record HammerWalljumpTriggerPacket(BlockPos pos, Direction direction) {

    public static void send(BlockPos pos, Direction direction) {
        PacketByteBuf buf = PacketByteBufs.create();

        ClientPlayNetworking.send(KlaxonPackets.HAMMER_WALLJUMP_TRIGGER_PACKET_C2S_ID,
                encode(buf, pos, direction));
    }

    public static PacketByteBuf encode(PacketByteBuf buf, BlockPos pos, Direction direction) {
        buf.writeBlockPos(pos);
        buf.writeEnumConstant(direction);

        return buf;
    }

    public static HammerWalljumpTriggerPacket decode(PacketByteBuf buf) {
        return new HammerWalljumpTriggerPacket(
                buf.readBlockPos(),
                buf.readEnumConstant(Direction.class)
        );
    }
}
