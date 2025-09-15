package net.myriantics.klaxon.networking.s2c;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.myriantics.klaxon.client.GrappleWinchConnectionManager;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.PlayerEntityGrappleAccess;
import net.myriantics.klaxon.registry.misc.KlaxonPackets;

public record GrappleWinchConnectionDiscardPacket(int playerId, int clawId) implements CustomPayload{
    public static final CustomPayload.Id<GrappleWinchConnectionDiscardPacket> ID = new CustomPayload.Id<>(KlaxonPackets.GRAPPLE_WINCH_CONNECTION_DISCARD_S2C_ID);

    public static PacketCodec<RegistryByteBuf, GrappleWinchConnectionDiscardPacket> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT, GrappleWinchConnectionDiscardPacket::playerId,
            PacketCodecs.VAR_INT, GrappleWinchConnectionDiscardPacket::clawId,
            GrappleWinchConnectionDiscardPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public void execute(ClientPlayNetworking.Context context) {
        MinecraftClient client = context.client();

        client.execute(() -> {
            GrappleWinchConnectionManager.INSTANCE.discardConnection(playerId);
            if (client.world instanceof ClientWorld world && world.getEntityById(playerId) instanceof PlayerEntityGrappleAccess access) {
                access.klaxon$setWinchConnectionData(null);
                access.klaxon$resetWinchCableLength();
            }
        });
    }
}
