package net.myriantics.klaxon.mechanics.grapple_winch;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

public enum CableDetachmentReason implements StringRepresentable {
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

    public static final Codec<CableDetachmentReason> CODEC = StringRepresentable.fromEnum(CableDetachmentReason::values);
    public static final StreamCodec<ByteBuf, CableDetachmentReason> PACKET_CODEC = ByteBufCodecs.idMapper(
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
    public String getSerializedName() {
        return this.name().toLowerCase();
    }
}
