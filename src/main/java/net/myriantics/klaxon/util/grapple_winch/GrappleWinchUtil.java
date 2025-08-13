package net.myriantics.klaxon.util.grapple_winch;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.myriantics.klaxon.entity.GrappleClawEntity;
import net.myriantics.klaxon.networking.s2c.GrappleWinchSyncPacket;

import java.util.Optional;

public abstract class GrappleWinchUtil {
    public static void onWorldLoadedServerside(MinecraftServer minecraftServer, ServerWorld serverWorld) {
        for (ServerPlayerEntity serverPlayer : serverWorld.getPlayers()) {
            if (serverPlayer instanceof PlayerEntityGrappleAccess access) {
                GrappleClawEntity grappleClaw = access.klaxon$getGrappleClaw();

                // ping all players in a world when it loads about their grapple winch data
                if (grappleClaw != null) {
                    ServerPlayNetworking.send(serverPlayer, new GrappleWinchSyncPacket(Optional.of(
                            new GrappleWinchClientFallbackData(
                                    grappleClaw.getPos(),
                                    grappleClaw.isAnchored()
                            )
                    ), grappleClaw.getId()));
                }
            }
        }
    }

    public static void onPlayerJoinServer(ServerPlayNetworkHandler serverPlayNetworkHandler, PacketSender packetSender, MinecraftServer minecraftServer) {
        ServerPlayerEntity serverPlayer = serverPlayNetworkHandler.getPlayer();
        if (serverPlayer instanceof PlayerEntityGrappleAccess access) {
            GrappleClawEntity grappleClaw = access.klaxon$getGrappleClaw();

            // better to update when player logs in than to update when grapple claw is loaded - more stable and accounts for cases when a grapple claw is loaded, then the player logs in afterwards and doesnt get the packet
            // this solves that and is more baller
            if (grappleClaw != null) {
                ServerPlayNetworking.send(serverPlayer, new GrappleWinchSyncPacket(Optional.of(
                        new GrappleWinchClientFallbackData(
                                grappleClaw.getPos(),
                                grappleClaw.isAnchored()
                        )
                ), grappleClaw.getId()));
            }
        }
    }
}
