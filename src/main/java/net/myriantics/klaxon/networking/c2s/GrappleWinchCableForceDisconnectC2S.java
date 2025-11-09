package net.myriantics.klaxon.networking.c2s;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.myriantics.klaxon.mechanics.grapple_winch.CableDetachmentReason;
import net.myriantics.klaxon.mechanics.grapple_winch.connection.ServerGrappleWinchConnection;
import net.myriantics.klaxon.mechanics.grapple_winch.manager.ServerGrappleWinchConnectionManager;
import net.myriantics.klaxon.registry.misc.KlaxonPackets;

public record GrappleWinchCableForceDisconnectC2S(CableDetachmentReason reason) implements CustomPayload {
    public static final CustomPayload.Id<GrappleWinchCableForceDisconnectC2S> ID = new Id<>(KlaxonPackets.GRAPPLE_WINCH_CABLE_FORCE_DISCONNECT_C2S_ID);

    public static final PacketCodec<RegistryByteBuf, GrappleWinchCableForceDisconnectC2S> PACKET_CODEC = PacketCodec.tuple(
            CableDetachmentReason.PACKET_CODEC, GrappleWinchCableForceDisconnectC2S::reason,
            GrappleWinchCableForceDisconnectC2S::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public void execute(ServerPlayNetworking.Context context) {
        // detach the player's grapple cable if they wish it to be detached
        context.server().execute(() -> {
            ServerGrappleWinchConnectionManager manager = ((ServerGrappleWinchConnectionManager.Access) context.player().getServerWorld()).klaxon$get();
            ServerGrappleWinchConnection connection = manager.fromPlayer(context.player());
            if (connection != null) {
                manager.disconnect(connection.getId(), reason);
            }
        });
    }
}
