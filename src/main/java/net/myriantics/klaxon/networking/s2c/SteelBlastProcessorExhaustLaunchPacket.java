package net.myriantics.klaxon.networking.s2c;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.joml.Vector3f;

public record SteelBlastProcessorExhaustLaunchPacket(Vector3f launchVelocity) implements CustomPacketPayload {
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return null;
    }
}
