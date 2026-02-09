package net.myriantics.klaxon.mechanics.grapple_winch;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;

public enum CableDetachmentReason implements StringIdentifiable {
    INVALID_HELD_ITEMS(0, true),
    HOOK_REMOVED(1, false),
    PLAYER_DIED(2, false),
    PLAYER_SPECTATOR(3, false),
    PLAYER_TELEPORTED(4, true),
    WORLD_MISMATCH(5, false),
    MANUAL_DISCONNECT(6, true),
    CABLE_TOO_LONG(7, true),
    FAST_RELOADED(8, false),
    PICKUP_RELOADED(9, false),
    PICKUP(10, false),
    GENERIC_DISCONNECT(11, false);

    public static final Codec<CableDetachmentReason> CODEC = StringIdentifiable.createCodec(CableDetachmentReason::values);
    public static final PacketCodec<ByteBuf, CableDetachmentReason> PACKET_CODEC = PacketCodecs.indexed(
            (index) -> CableDetachmentReason.values()[index],
            CableDetachmentReason::getIndex
    );

    private final int index;
    public final boolean playsDetachmentSound;

    CableDetachmentReason(int index, boolean playsDetachmentSound) {
        this.index = index;
        this.playsDetachmentSound = playsDetachmentSound;
    }

    public int getIndex() {
        return this.index;
    }

    @Override
    public String asString() {
        return this.name().toLowerCase();
    }
}
