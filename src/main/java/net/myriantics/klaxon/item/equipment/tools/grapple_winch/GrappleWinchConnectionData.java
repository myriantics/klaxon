package net.myriantics.klaxon.item.equipment.tools.grapple_winch;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.math.Vec3d;

public record GrappleWinchConnectionData(
        int playerId,
        int clawId,
        Vec3d playerPos,
        Vec3d clawPos,
        boolean isWinchAnchored
) {
    public static final PacketCodec<RegistryByteBuf, GrappleWinchConnectionData> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT, GrappleWinchConnectionData::playerId,
            PacketCodecs.VAR_INT, GrappleWinchConnectionData::clawId,
            PacketCodecs.VECTOR3F.xmap(Vec3d::new, Vec3d::toVector3f), GrappleWinchConnectionData::playerPos,
            PacketCodecs.VECTOR3F.xmap(Vec3d::new, Vec3d::toVector3f), GrappleWinchConnectionData::clawPos,
            PacketCodecs.BOOL, GrappleWinchConnectionData::isWinchAnchored,
            GrappleWinchConnectionData::new
    );
}
