package net.myriantics.klaxon.mechanics.grapple_winch.connection;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.myriantics.klaxon.mechanics.grapple_winch.GrapplingHook;
import net.myriantics.klaxon.networking.s2c.GrappleWinchConnectionSyncPacket;

import java.util.Objects;

public abstract sealed class GrappleWinchConnection permits ClientGrappleWinchConnection, ServerGrappleWinchConnection {
    protected final int connectionId;

    protected boolean retracting = false;
    protected boolean hookAnchored = false;
    protected double cableLength;

    public GrappleWinchConnection(int connectionId) {
        this.connectionId = connectionId;
    }

    public void tick() {
    }

    public int getId() {
        return this.connectionId;
    }

    public abstract int getPlayerId();

    public abstract int getHookId();

    public abstract PlayerEntity getPlayer();

    public abstract GrapplingHook getHook();

    public abstract Vec3d getPlayerPos();

    public abstract Vec3d getHookPos();

    public boolean isHookAnchored() {
        return this.hookAnchored;
    }

    public double getCableLength() {
        return this.cableLength;
    }

    public abstract void setCableLength(double cableLength);

    public abstract void resetCableLength();

    public boolean isRetracting() {
        return this.retracting;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof GrappleWinchConnection connection && connection.connectionId == this.connectionId;
    }
}
