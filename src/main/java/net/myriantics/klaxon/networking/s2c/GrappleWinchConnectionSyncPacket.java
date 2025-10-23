package net.myriantics.klaxon.networking.s2c;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.myriantics.klaxon.client.GrappleWinchClientConnectionManager;
import net.myriantics.klaxon.entity.entities.grapple_claw.GrappleClawCableAttachmentHandler;
import net.myriantics.klaxon.entity.entities.grapple_claw.GrappleClawEntity;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.GrappleWinchConnectionData;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.PlayerEntityGrappleAccess;
import net.myriantics.klaxon.registry.misc.KlaxonPackets;

public record GrappleWinchConnectionSyncPacket(GrappleWinchConnectionData connectionData) implements CustomPayload {

    public static final CustomPayload.Id<GrappleWinchConnectionSyncPacket> ID = new CustomPayload.Id<>(KlaxonPackets.GRAPPLE_WINCH_CONNECTION_SYNC_S2C_ID);

    public static final PacketCodec<RegistryByteBuf, GrappleWinchConnectionSyncPacket> PACKET_CODEC = PacketCodec.tuple(
            GrappleWinchConnectionData.PACKET_CODEC, GrappleWinchConnectionSyncPacket::connectionData,
            GrappleWinchConnectionSyncPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public void execute(ClientPlayNetworking.Context context) {
        MinecraftClient client = context.client();

        client.execute(() -> {
            if (client.world != null) {
                // if the player is tracked by the client, update its connection data
                Entity player = client.world.getEntityById(connectionData.playerId());
                Entity grappleClaw = client.world.getEntityById(connectionData.grappleClawId());
                if (player instanceof AbstractClientPlayerEntity) {
                    ((PlayerEntityGrappleAccess) player).klaxon$setConnectionData(connectionData);
                    ((PlayerEntityGrappleAccess) player).klaxon$setGrappleClaw(grappleClaw instanceof GrappleClawEntity ? (GrappleClawEntity) grappleClaw : null);
                    ((PlayerEntityGrappleAccess) player).klaxon$resetWinchCableLength();
                }

                // update owner so grapple claw knows who to actually play sounds to
                if (grappleClaw instanceof GrappleClawEntity) {
                    ((GrappleClawEntity) grappleClaw).setOwner(player);
                    ((GrappleClawEntity) grappleClaw).cableAttachmentHandler.setAttachmentState(GrappleClawCableAttachmentHandler.AttachmentState.ATTACHED);
                }

                GrappleWinchClientConnectionManager.INSTANCE.addOrUpdateConnection(connectionData);
            }
        });
    }
}
