package net.myriantics.klaxon.item.equipment.tools.grapple_winch;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.server.network.ServerPlayerEntity;
import net.myriantics.klaxon.entity.GrappleClawEntity;
import net.myriantics.klaxon.networking.KlaxonServerPlayNetworkHandler;
import net.myriantics.klaxon.networking.s2c.GrappleWinchConnectionDiscardPacket;
import net.myriantics.klaxon.networking.s2c.GrappleWinchConnectionSyncPacket;

import java.util.Collection;

public abstract class GrappleWinchNetworkUtil {
    public static void syncToClients(ServerPlayerEntity serverPlayer, GrappleClawEntity grappleClaw) {
        Collection<ServerPlayerEntity> playerTrackingPlayers = PlayerLookup.tracking(serverPlayer.getServerWorld(), serverPlayer.getBlockPos());
        Collection<ServerPlayerEntity> clawTrackingPlayers = PlayerLookup.tracking(serverPlayer.getServerWorld(), grappleClaw.getBlockPos());

        GrappleWinchConnectionSyncPacket syncPacket = new GrappleWinchConnectionSyncPacket(new GrappleWinchConnectionData(
                serverPlayer.getId(),
                grappleClaw.getId(),
                serverPlayer.getPos(),
                grappleClaw.getPos(),
                grappleClaw.isAnchored()
        ));

        // send the packet to all players tracking the connected player
        for (ServerPlayerEntity trackingPlayer : playerTrackingPlayers) {
            KlaxonServerPlayNetworkHandler.send(trackingPlayer, syncPacket);
        }

        // don't send the same packet to players twice - however do send to any tracking claw but not player
        for (ServerPlayerEntity trackingPlayer : clawTrackingPlayers) {
            if (!playerTrackingPlayers.contains(trackingPlayer)) {
                KlaxonServerPlayNetworkHandler.send(trackingPlayer, syncPacket);
            }
        }
    }

    public static void clearFromClients(ServerPlayerEntity serverPlayer, GrappleClawEntity grappleClaw) {
        Collection<ServerPlayerEntity> playerTrackingPlayers = PlayerLookup.tracking(serverPlayer.getServerWorld(), serverPlayer.getBlockPos());
        Collection<ServerPlayerEntity> clawTrackingPlayers = PlayerLookup.tracking(serverPlayer.getServerWorld(), grappleClaw.getBlockPos());

        GrappleWinchConnectionDiscardPacket discardPacket = new GrappleWinchConnectionDiscardPacket(
                serverPlayer.getId(),
                grappleClaw.getId()
        );

        // send the packet to all players tracking the connected player
        for (ServerPlayerEntity trackingPlayer : playerTrackingPlayers) {
            KlaxonServerPlayNetworkHandler.send(trackingPlayer, discardPacket);
        }

        // don't send the same packet to players twice - however do send to any tracking claw but not player
        for (ServerPlayerEntity trackingPlayer : clawTrackingPlayers) {
            if (!playerTrackingPlayers.contains(trackingPlayer)) {
                KlaxonServerPlayNetworkHandler.send(trackingPlayer, discardPacket);
            }
        }
    }
}
