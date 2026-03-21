package net.myriantics.klaxon.networking.c2s;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.myriantics.klaxon.mechanics.grapple_winch.CableDetachmentReason;
import net.myriantics.klaxon.mechanics.grapple_winch.connection.ServerGrappleWinchConnection;
import net.myriantics.klaxon.mechanics.grapple_winch.manager.ServerGrappleWinchConnectionManager;
import net.myriantics.klaxon.registry.misc.KlaxonPackets;

public record GrappleWinchCableForceDisconnectC2S(CableDetachmentReason reason) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<GrappleWinchCableForceDisconnectC2S> ID = new Type<>(KlaxonPackets.GRAPPLE_WINCH_CABLE_FORCE_DISCONNECT_C2S_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, GrappleWinchCableForceDisconnectC2S> PACKET_CODEC = StreamCodec.composite(
            CableDetachmentReason.PACKET_CODEC, GrappleWinchCableForceDisconnectC2S::reason,
            GrappleWinchCableForceDisconnectC2S::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public void execute(ServerPlayNetworking.Context context) {
        // detach the player's grapple cable if they wish it to be detached
        context.server().execute(() -> {
            ServerGrappleWinchConnectionManager manager = ServerGrappleWinchConnectionManager.get(context.player().serverLevel());
            ServerGrappleWinchConnection connection = manager.fromPlayer(context.player());
            if (connection != null) {
                manager.disconnect(connection.getId(), reason);
            }
        });
    }
}
