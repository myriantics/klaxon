package net.myriantics.klaxon.networking.s2c;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.myriantics.klaxon.registry.misc.KlaxonPackets;
import org.joml.Vector3f;

public record SteelBlastProcessorExhaustLaunchPacket(Vector3f launchVelocity) implements CustomPacketPayload {

    public static final Type<SteelBlastProcessorExhaustLaunchPacket> ID = new Type<>(KlaxonPackets.STEEL_BLAST_PROCESSOR_EXHAUST_LAUNCH_S2C_ID);

    public static final StreamCodec<FriendlyByteBuf, SteelBlastProcessorExhaustLaunchPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VECTOR3F, SteelBlastProcessorExhaustLaunchPacket::launchVelocity,
            SteelBlastProcessorExhaustLaunchPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
