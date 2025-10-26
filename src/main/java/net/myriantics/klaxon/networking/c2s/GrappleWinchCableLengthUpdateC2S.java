package net.myriantics.klaxon.networking.c2s;

import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.PlayerEntityGrappleAccess;
import net.myriantics.klaxon.registry.misc.KlaxonPackets;

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
            PlayerEntityGrappleAccess access = (PlayerEntityGrappleAccess) context.player();
            if (access.klaxon$hasActiveConnection()) {
                access.klaxon$setCurrentWinchCableLength(cableLength);
            }
        });
    }
}
