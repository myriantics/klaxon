package net.myriantics.klaxon.networking;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.myriantics.klaxon.networking.s2c.ItemUsageLockoutTrigger;
import net.myriantics.klaxon.networking.s2c.KlaxonWorldEventPacket;

public abstract class KlaxonServerPlayNetworkHandler {

    /**
     * Clears the player's active item and triggers item lockout on the client.
     * This makes it so that the player must release their use key and press it again in order to use the item again.
     * @param serverPlayer the player to trigger it on
     */
    public static void triggerItemLockout(ServerPlayer serverPlayer) {
        serverPlayer.stopUsingItem();
        send(serverPlayer, new ItemUsageLockoutTrigger());
    }

    public static void send(ServerPlayer serverPlayer, CustomPacketPayload customPayload) {
        ServerPlayNetworking.send(serverPlayer, customPayload);
    }

    public static void sendToTracking(ServerLevel serverWorld, BlockPos pos, CustomPacketPayload customPayload) {
        for (ServerPlayer player : PlayerLookup.tracking(serverWorld, pos)) {
            ServerPlayNetworking.send(player, customPayload);
        }
    }

    public static void sendToTracking(ServerLevel serverWorld, Entity tracking, CustomPacketPayload customPayload) {
        for (ServerPlayer player : PlayerLookup.tracking(tracking)) {
            ServerPlayNetworking.send(player, customPayload);
        }
    }

    public static void syncWorldEvent(ServerLevel serverWorld, BlockPos pos, int eventId) {
        syncWorldEvent(serverWorld, pos, eventId, 0);
    }
    public static void syncWorldEvent(ServerLevel serverWorld, BlockPos pos, int eventId, int data) {
        sendToTracking(serverWorld, pos, new KlaxonWorldEventPacket(eventId, pos, data, false));
    }

    public static void syncGlobalEvent(ServerLevel serverWorld, BlockPos pos, int eventId) {
        syncGlobalEvent(serverWorld, pos, eventId, 0);
    }
    public static void syncGlobalEvent(ServerLevel serverWorld, BlockPos pos, int eventId, int data) {
        sendToTracking(serverWorld, pos, new KlaxonWorldEventPacket(eventId, pos, data, true));
    }
}
