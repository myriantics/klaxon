package net.myriantics.klaxon.networking.c2s;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.myriantics.klaxon.component.ability.WalljumpAbilityComponent;
import net.myriantics.klaxon.registry.misc.KlaxonPackets;

public record HammerWalljumpTriggerPacket(BlockPos pos, Direction direction) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<HammerWalljumpTriggerPacket> ID = new CustomPacketPayload.Type<>(KlaxonPackets.HAMMER_WALLJUMP_TRIGGER_PACKET_C2S_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, HammerWalljumpTriggerPacket> PACKET_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, HammerWalljumpTriggerPacket::pos,
            Direction.STREAM_CODEC, HammerWalljumpTriggerPacket::direction,
            HammerWalljumpTriggerPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public void execute(ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            ServerPlayer player = context.player();

            WalljumpAbilityComponent component = WalljumpAbilityComponent.get(player.getMainHandItem());

            if (component != null) {
                // run the walljump ability :D
                component.processHammerWalljump(player, player.level(), pos, direction);
            }
        });
    }
}
