package net.myriantics.klaxon.networking.s2c;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.myriantics.klaxon.client.GrappleWinchClientConnectionManager;
import net.myriantics.klaxon.entity.entities.grapple_claw.GrappleClawCableAttachmentHandler;
import net.myriantics.klaxon.entity.entities.grapple_claw.GrappleClawEntity;
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
            GrappleWinchClientConnectionManager.INSTANCE.discardConnection(playerId);

            if (client.world != null) {
                Entity player = client.world.getEntityById(playerId());
                Entity grappleClaw = client.world.getEntityById(clawId);
                if (player instanceof AbstractClientPlayerEntity) {
                    ((PlayerEntityGrappleAccess) player).klaxon$setConnectionData(null);
                    ((PlayerEntityGrappleAccess) player).klaxon$setGrappleClaw(grappleClaw instanceof GrappleClawEntity ? (GrappleClawEntity) grappleClaw : null);
                    ((PlayerEntityGrappleAccess) player).klaxon$resetWinchCableLength();
                }
                if (grappleClaw instanceof GrappleClawEntity) {
                    ((GrappleClawEntity) grappleClaw).cableAttachmentHandler.setAttachmentState(GrappleClawCableAttachmentHandler.AttachmentState.DETACHED);
                }
            }
        });
    }
}
