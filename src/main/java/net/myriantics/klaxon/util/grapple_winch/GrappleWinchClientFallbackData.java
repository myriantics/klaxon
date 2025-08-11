package net.myriantics.klaxon.util.grapple_winch;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.math.Vec3d;

public record GrappleWinchClientFallbackData(Vec3d winchConnectedPos, boolean isWinchAnchored) {

    public static final PacketCodec<RegistryByteBuf, GrappleWinchClientFallbackData> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.VECTOR3F.xmap(Vec3d::new, Vec3d::toVector3f), GrappleWinchClientFallbackData::winchConnectedPos,
            PacketCodecs.BOOL, GrappleWinchClientFallbackData::isWinchAnchored,
            GrappleWinchClientFallbackData::new
    );
}
