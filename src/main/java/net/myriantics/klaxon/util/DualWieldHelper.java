package net.myriantics.klaxon.util;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.myriantics.klaxon.networking.packets.EntityDualWieldToggleC2SPacket;
import net.myriantics.klaxon.networking.packets.EntityDualWieldToggleS2CPacket;

/**
 * Deals with cosmetic hand swinging when dual-wielding certain items.
 */
public abstract class DualWieldHelper {
    public static void setDualWielding(LivingEntity entity, boolean dualWielding) {
        if (entity instanceof LivingEntityMixinAccess access) access.klaxon$setDualWielding(dualWielding);
    }

    public static void syncDualWielding(LivingEntity entity) {
        boolean isDualWielding = ((LivingEntityMixinAccess) entity).klaxon$isDualWielding();

        if (entity.getWorld() instanceof ClientWorld) {
            ClientPlayNetworking.send(new EntityDualWieldToggleC2SPacket(isDualWielding));
        } else if (entity.getWorld() instanceof ServerWorld) {
            for (ServerPlayerEntity player : PlayerLookup.tracking(entity)) {
                if (!entity.equals(player)) ServerPlayNetworking.send(player, new EntityDualWieldToggleS2CPacket(entity.getId(), isDualWielding));
            }
        }
    }

    public enum DualWieldType {
        USE,
        SUSTAINED_USE,
        ATTACK,
        INACTIVE
    }
}
