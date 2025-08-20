package net.myriantics.klaxon.util.grapple_winch;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.myriantics.klaxon.entity.GrappleClawEntity;
import net.myriantics.klaxon.networking.KlaxonServerPlayNetworkHandler;
import net.myriantics.klaxon.networking.s2c.GrappleWinchConnectionDiscardPacket;
import net.myriantics.klaxon.networking.s2c.GrappleWinchConnectionSyncPacket;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class GrappleWinchUtil {

    public static void onEntityLoadedServerside(Entity entity, ServerWorld serverWorld) {
        if (entity instanceof ServerPlayerEntity serverPlayer && serverPlayer instanceof PlayerEntityGrappleAccess access) {
            GrappleClawEntity grappleClaw = access.klaxon$getGrappleClaw();

            if (grappleClaw != null) {
                updateClientFallbackData(serverPlayer, grappleClaw);
            }
        }
        if (entity instanceof GrappleClawEntity grappleClaw) {
            Entity owner = grappleClaw.getOwner();
            if (owner instanceof ServerPlayerEntity serverPlayer) {
                updateClientFallbackData(serverPlayer, grappleClaw);
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
                updateClientFallbackData(serverPlayer, grappleClaw);

            }
        }
    }

    public static void updateClientFallbackData(ServerPlayerEntity serverPlayer, GrappleClawEntity grappleClaw) {
        KlaxonServerPlayNetworkHandler.sendToTracking(serverPlayer.getServerWorld(), serverPlayer.getBlockPos(), new GrappleWinchConnectionSyncPacket(
                new GrappleWinchConnectionData(
                        serverPlayer.getId(),
                        grappleClaw.getId(),
                        serverPlayer.getPos(),
                        grappleClaw.getPos(),
                        grappleClaw.isAnchored()
                )
        ));
    }

    public static void clearClientFallbackData(ServerPlayerEntity serverPlayer, @Nullable GrappleClawEntity grappleClaw) {
        KlaxonServerPlayNetworkHandler.sendToTracking(serverPlayer.getServerWorld(), serverPlayer.getBlockPos(), new GrappleWinchConnectionDiscardPacket(
                serverPlayer.getId(),
                grappleClaw == null ? -1 : grappleClaw.getId()
        ));
    }


}
