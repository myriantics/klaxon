package net.myriantics.klaxon.mechanics.grapple_winch;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public interface GrapplingHook {
    int klaxon$getId();

    void klaxon$onConnect(ServerPlayerEntity serverPlayer);

    void klaxon$onDisconnect();

    UUID klaxon$getUUID();

    boolean klaxon$isAnchored();

    boolean klaxon$deAnchor(Vec3d deAnchoringDirection);

    boolean klaxon$isRemoved();

    Vec3d klaxon$getPos();

    void klaxon$setVelocity(Vec3d velocity);

    Vec3d klaxon$getVelocity();
}
