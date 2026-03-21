package net.myriantics.klaxon.networking.c2s;

import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.myriantics.klaxon.mechanics.grapple_winch.connection.ServerGrappleWinchConnection;
import net.myriantics.klaxon.mechanics.grapple_winch.manager.ServerGrappleWinchConnectionManager;
import net.myriantics.klaxon.registry.misc.KlaxonPackets;
import org.jetbrains.annotations.Nullable;

public record GrappleWinchCableLengthUpdateC2S(double cableLength) implements CustomPacketPayload {
    public static CustomPacketPayload.Type<GrappleWinchCableLengthUpdateC2S> ID = new Type<>(KlaxonPackets.GRAPPLE_WINCH_CABLE_LENGTH_UPDATE_C2S_ID);

    public static final StreamCodec<ByteBuf, GrappleWinchCableLengthUpdateC2S> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, GrappleWinchCableLengthUpdateC2S::cableLength,
            GrappleWinchCableLengthUpdateC2S::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public void execute(ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            ServerGrappleWinchConnectionManager manager = ServerGrappleWinchConnectionManager.get(context.player().serverLevel());
            @Nullable ServerGrappleWinchConnection connection = manager.fromPlayer(context.player());
            if (connection != null) {
                connection.setCableLength(this.cableLength);
            }
        });
    }
}
