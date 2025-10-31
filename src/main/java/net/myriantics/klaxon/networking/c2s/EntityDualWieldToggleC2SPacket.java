package net.myriantics.klaxon.networking.c2s;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.myriantics.klaxon.registry.misc.KlaxonPackets;
import net.myriantics.klaxon.mechanics.dual_wielding.LivingEntityMixinAccess;

public record EntityDualWieldToggleC2SPacket(boolean isDualWielding) implements CustomPayload {

    public static final Id<EntityDualWieldToggleC2SPacket> ID = new Id<>(KlaxonPackets.DUAL_WIELD_TOGGLE_C2S_PACKET);

    public static final PacketCodec<RegistryByteBuf, EntityDualWieldToggleC2SPacket> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.BOOL, EntityDualWieldToggleC2SPacket::isDualWielding,
            EntityDualWieldToggleC2SPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public void execute(ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            ServerPlayerEntity player = context.player();

            if (player instanceof LivingEntityMixinAccess access) {
                access.klaxon$setDualWielding(isDualWielding);
            }
        });
    }
}
