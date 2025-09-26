package net.myriantics.klaxon.item.equipment.tools.grapple_winch;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

public final class GrappleWinchConnectionData {
    public static final PacketCodec<RegistryByteBuf, GrappleWinchConnectionData> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT, GrappleWinchConnectionData::playerId,
            PacketCodecs.VAR_INT, GrappleWinchConnectionData::grappleClawId,
            PacketCodecs.VECTOR3F.xmap(Vec3d::new, Vec3d::toVector3f), GrappleWinchConnectionData::playerPos,
            PacketCodecs.VECTOR3F.xmap(Vec3d::new, Vec3d::toVector3f), GrappleWinchConnectionData::grappleClawPos,
            PacketCodecs.BOOL, GrappleWinchConnectionData::isClawAnchored,
            GrappleWinchConnectionData::new
    );

    private final int playerId;
    private final int grappleClawId;
    private Vec3d playerPos;
    private Vec3d grappleClawPos;
    private boolean isClawAnchored;

    public GrappleWinchConnectionData(
            int playerId,
            int grappleClawId,
            Vec3d playerPos,
            Vec3d grappleClawPos,
            boolean isClawAnchored
    ) {
        this.playerId = playerId;
        this.grappleClawId = grappleClawId;
        this.playerPos = playerPos;
        this.grappleClawPos = grappleClawPos;
        this.isClawAnchored = isClawAnchored;
    }

    public int playerId() {
        return playerId;
    }

    public int grappleClawId() {
        return grappleClawId;
    }

    public void setPlayerPos(Vec3d playerPos) {
        this.playerPos = playerPos;
    }

    public void setGrappleClawPos(Vec3d grappleClawPos) {
        this.grappleClawPos = grappleClawPos;
    }

    public void setClawAnchored(boolean anchored) {
        this.isClawAnchored = anchored;
    }

    public Vec3d playerPos() {
        return playerPos;
    }

    public Vec3d grappleClawPos() {
        return grappleClawPos;
    }

    public boolean isClawAnchored() {
        return isClawAnchored;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (GrappleWinchConnectionData) obj;
        return this.playerId == that.playerId &&
                this.grappleClawId == that.grappleClawId &&
                Objects.equals(this.playerPos, that.playerPos) &&
                Objects.equals(this.grappleClawPos, that.grappleClawPos) &&
                this.isClawAnchored == that.isClawAnchored;
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId, grappleClawId, playerPos, grappleClawPos, isClawAnchored);
    }

    @Override
    public String toString() {
        return "GrappleWinchConnectionData[" +
                "playerId=" + playerId + ", " +
                "grappleClawId=" + grappleClawId + ", " +
                "playerPos=" + playerPos + ", " +
                "grappleClawPos=" + grappleClawPos + ", " +
                "isWinchAnchored=" + isClawAnchored + ']';
    }

}
