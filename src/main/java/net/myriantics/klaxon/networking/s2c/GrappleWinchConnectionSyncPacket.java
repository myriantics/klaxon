package net.myriantics.klaxon.networking.s2c;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.myriantics.klaxon.client.GrappleWinchConnectionManager;
import net.myriantics.klaxon.entity.GrappleClawEntity;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.PlayerEntityGrappleAccess;
import net.myriantics.klaxon.registry.misc.KlaxonPackets;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.GrappleWinchConnectionData;

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
            if (MinecraftClient.getInstance().world instanceof ClientWorld clientWorld) {
                Entity potentialPlayer = clientWorld.getEntityById(connectionData.playerId());
                Entity potentialClaw = clientWorld.getEntityById(connectionData.clawId());

                if (potentialPlayer instanceof AbstractClientPlayerEntity player) {
                    GrappleWinchConnectionManager.INSTANCE.addConnection(
                            connectionData.playerId(),
                            connectionData.clawId(),
                            player,
                            (GrappleClawEntity) potentialClaw,
                            connectionData.playerPos(),
                            connectionData.clawPos());

                    // update access shi
                    PlayerEntityGrappleAccess access = (PlayerEntityGrappleAccess) player;
                    access.klaxon$setWinchConnectionData(connectionData);
                    access.klaxon$resetWinchCableLength();
                } else if (potentialClaw instanceof GrappleClawEntity grappleClaw) {
                    GrappleWinchConnectionManager.INSTANCE.addConnection(
                            connectionData.playerId(),
                            connectionData.clawId(),
                            null,
                            grappleClaw,
                            connectionData.playerPos(),
                            connectionData.clawPos());
                } else {
                    // cancel other operations if client world doesn't have either entity loaded
                    return;
                }
            }
        });
    }
}
