package net.myriantics.klaxon.networking.c2s;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.myriantics.klaxon.component.ability.WalljumpAbilityComponent;
import net.myriantics.klaxon.registry.misc.KlaxonPackets;

public record HammerWalljumpTriggerPacket(BlockPos pos, Direction direction) implements CustomPayload {

    public static final CustomPayload.Id<HammerWalljumpTriggerPacket> ID = new CustomPayload.Id<>(KlaxonPackets.HAMMER_WALLJUMP_TRIGGER_PACKET_C2S_ID);

    public static final PacketCodec<RegistryByteBuf, HammerWalljumpTriggerPacket> PACKET_CODEC = PacketCodec.tuple(
            BlockPos.PACKET_CODEC, HammerWalljumpTriggerPacket::pos,
            Direction.PACKET_CODEC, HammerWalljumpTriggerPacket::direction,
            HammerWalljumpTriggerPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public void execute(ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            ServerPlayerEntity player = context.player();

            WalljumpAbilityComponent component = WalljumpAbilityComponent.get(player.getMainHandStack());

            if (component != null) {
                // run the walljump ability :D
                component.processHammerWalljump(player, player.getWorld(), pos, direction);
            }
        });
    }
}
