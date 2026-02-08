package net.myriantics.klaxon.networking.c2s;

import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.myriantics.klaxon.mechanics.grapple_winch.connection.ServerGrappleWinchConnection;
import net.myriantics.klaxon.mechanics.grapple_winch.manager.ServerGrappleWinchConnectionManager;
import net.myriantics.klaxon.registry.misc.KlaxonPackets;
import org.jetbrains.annotations.Nullable;

public record GrappleWinchCableLengthUpdateC2S(double cableLength) implements CustomPayload {
    public static CustomPayload.Id<GrappleWinchCableLengthUpdateC2S> ID = new Id<>(KlaxonPackets.GRAPPLE_WINCH_CABLE_LENGTH_UPDATE_C2S_ID);

    public static final PacketCodec<ByteBuf, GrappleWinchCableLengthUpdateC2S> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.DOUBLE, GrappleWinchCableLengthUpdateC2S::cableLength,
            GrappleWinchCableLengthUpdateC2S::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public void execute(ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            ServerGrappleWinchConnectionManager manager = ServerGrappleWinchConnectionManager.get(context.player().getServerWorld());
            @Nullable ServerGrappleWinchConnection connection = manager.fromPlayer(context.player());
            if (connection != null) {
                connection.setCableLength(this.cableLength);
            }
        });
    }
}
