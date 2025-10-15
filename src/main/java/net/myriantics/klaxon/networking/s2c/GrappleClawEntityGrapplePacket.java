package net.myriantics.klaxon.networking.s2c;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.myriantics.klaxon.client.GrappleWinchClientConnectionManager;
import net.myriantics.klaxon.entity.GrappleClawEntity;
import net.myriantics.klaxon.item.equipment.tools.grapple_winch.PlayerEntityGrappleAccess;
import net.myriantics.klaxon.registry.misc.KlaxonPackets;

public record GrappleClawEntityGrapplePacket(int clawId, int grappledEntityId) implements CustomPayload {
    public static final CustomPayload.Id<GrappleClawEntityGrapplePacket> ID = new CustomPayload.Id<>(KlaxonPackets.GRAPPLE_CLAW_ENTITY_GRAPPLE_S2C_ID);

    public static PacketCodec<RegistryByteBuf, GrappleClawEntityGrapplePacket> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT, GrappleClawEntityGrapplePacket::clawId,
            PacketCodecs.VAR_INT, GrappleClawEntityGrapplePacket::grappledEntityId,
            GrappleClawEntityGrapplePacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public void execute(ClientPlayNetworking.Context context) {
        MinecraftClient client = context.client();

        client.execute(() -> {
            if (client.world != null) {
                Entity maybeGrappleClaw = client.world.getEntityById(clawId);
                Entity grappledEntity = client.world.getEntityById(grappledEntityId);
                if (maybeGrappleClaw instanceof GrappleClawEntity grappleClaw) {
                    grappleClaw.setGrappledEntity(grappledEntity);
                }
            }
        });
    }
}
