package net.myriantics.klaxon.networking.c2s;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.myriantics.klaxon.entity.GrappleClawEntity;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.PlayerEntityGrappleAccess;
import net.myriantics.klaxon.registry.misc.KlaxonPackets;
import org.jetbrains.annotations.Nullable;
import snownee.jade.network.ClientPayloadContext;
import snownee.jade.network.ServerPayloadContext;

public record GrappleWinchCableForceDisconnectC2S() implements CustomPayload {
    public static final CustomPayload.Id<GrappleWinchCableForceDisconnectC2S> ID = new Id<>(KlaxonPackets.GRAPPLE_WINCH_CABLE_FORCE_DISCONNECT_C2S_ID);

    private static final GrappleWinchCableForceDisconnectC2S INSTANCE = new GrappleWinchCableForceDisconnectC2S();

    public static final PacketCodec<RegistryByteBuf, GrappleWinchCableForceDisconnectC2S> PACKET_CODEC = PacketCodec.unit(INSTANCE);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public void execute(ServerPlayNetworking.Context context) {
        // detach the player's grapple cable if they wish it to be detached
        context.server().execute(() -> {
            @Nullable GrappleClawEntity grappleClaw = ((PlayerEntityGrappleAccess) context.player()).klaxon$getGrappleClaw();
            if (grappleClaw != null) {
                grappleClaw.detachCable(false);
            }
        });
    }
}
