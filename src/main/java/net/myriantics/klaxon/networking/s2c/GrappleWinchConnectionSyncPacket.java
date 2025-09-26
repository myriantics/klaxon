package net.myriantics.klaxon.networking.s2c;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.Vec3d;
import net.myriantics.klaxon.client.GrappleWinchClientConnectionManager;
import net.myriantics.klaxon.entity.GrappleClawEntity;
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
                GrappleWinchClientConnectionManager.INSTANCE.addOrUpdateConnection(connectionData);

                if (client.player instanceof PlayerEntityGrappleAccess access) {
                    access.klaxon$resetWinchCableLength();
                }
            }
        });
    }
}
