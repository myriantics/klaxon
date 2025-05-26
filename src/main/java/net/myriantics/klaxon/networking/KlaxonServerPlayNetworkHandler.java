package net.myriantics.klaxon.networking;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.myriantics.klaxon.networking.s2c.EntityDualWieldToggleS2CPacket;
import net.myriantics.klaxon.networking.s2c.KlaxonWorldEventPacket;

public abstract class KlaxonServerPlayNetworkHandler {
    public static void sendToTracking(ServerWorld serverWorld, BlockPos pos, CustomPayload customPayload) {
        for (ServerPlayerEntity player : PlayerLookup.tracking(serverWorld, pos)) {
            ServerPlayNetworking.send(player, customPayload);
        }
    }

    public static void sendToTracking(ServerWorld serverWorld, Entity tracking, CustomPayload customPayload) {
        for (ServerPlayerEntity player : PlayerLookup.tracking(tracking)) {
            ServerPlayNetworking.send(player, customPayload);
        }
    }

    public static void syncWorldEvent(ServerWorld serverWorld, BlockPos pos, int eventId) {
        syncWorldEvent(serverWorld, pos, eventId, 0);
    }
    public static void syncWorldEvent(ServerWorld serverWorld, BlockPos pos, int eventId, int data) {
        sendToTracking(serverWorld, pos, new KlaxonWorldEventPacket(eventId, pos, data, false));
    }

    public static void syncGlobalEvent(ServerWorld serverWorld, BlockPos pos, int eventId) {
        syncGlobalEvent(serverWorld, pos, eventId, 0);
    }
    public static void syncGlobalEvent(ServerWorld serverWorld, BlockPos pos, int eventId, int data) {
        sendToTracking(serverWorld, pos, new KlaxonWorldEventPacket(eventId, pos, data, true));
    }
}
