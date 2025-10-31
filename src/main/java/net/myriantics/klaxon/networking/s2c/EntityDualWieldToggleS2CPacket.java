package net.myriantics.klaxon.networking.s2c;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.myriantics.klaxon.registry.misc.KlaxonPackets;
import net.myriantics.klaxon.mechanics.dual_wielding.LivingEntityMixinAccess;

public record EntityDualWieldToggleS2CPacket(int entityId, boolean isDualWielding) implements CustomPayload {

    public static final CustomPayload.Id<EntityDualWieldToggleS2CPacket> ID = new CustomPayload.Id<>(KlaxonPackets.DUAL_WIELD_TOGGLE_S2C_PACKET);

    public static final PacketCodec<RegistryByteBuf, EntityDualWieldToggleS2CPacket> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT, EntityDualWieldToggleS2CPacket::entityId,
            PacketCodecs.BOOL, EntityDualWieldToggleS2CPacket::isDualWielding,
            EntityDualWieldToggleS2CPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public void execute(ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            MinecraftClient client = context.client();

            if (client.world != null && client.world.getEntityById(entityId) instanceof LivingEntityMixinAccess access) {
                access.klaxon$setDualWielding(isDualWielding);
            }
        });
    }
}
