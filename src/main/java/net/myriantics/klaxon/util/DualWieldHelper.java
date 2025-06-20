package net.myriantics.klaxon.util;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.StringIdentifiable;
import net.myriantics.klaxon.networking.KlaxonServerPlayNetworkHandler;
import net.myriantics.klaxon.networking.c2s.EntityDualWieldToggleC2SPacket;
import net.myriantics.klaxon.networking.s2c.EntityDualWieldToggleS2CPacket;

/**
 * Deals with cosmetic hand swinging when dual-wielding certain items.
 */
public abstract class DualWieldHelper {
    public static void setDualWielding(LivingEntity entity, boolean dualWielding) {
        if (entity instanceof LivingEntityMixinAccess access) access.klaxon$setDualWielding(dualWielding);
    }

    public static void syncDualWielding(LivingEntity entity) {
        boolean isDualWielding = ((LivingEntityMixinAccess) entity).klaxon$isDualWielding();

        // Entities should disable dualwielding on their own, whether it be clientside or serverside. This is here to prevent useless packet spam.
        if (!isDualWielding) return;

        if (entity.getWorld().isClient()) {
            ClientPlayNetworking.send(new EntityDualWieldToggleC2SPacket(isDualWielding));
        } else if (entity.getWorld() instanceof ServerWorld serverWorld) {
            KlaxonServerPlayNetworkHandler.sendToTracking(serverWorld, entity, new EntityDualWieldToggleS2CPacket(entity.getId(), isDualWielding));
        }
    }

    public enum DualWieldType implements StringIdentifiable {
        USE,
        SUSTAINED_USE,
        ATTACK,
        INACTIVE;

        private static final Codec<DualWieldType> CODEC = StringIdentifiable.createCodec(DualWieldType::values);

        @Override
        public String asString() {
            return this.toString().toLowerCase();
        }
    }
}
