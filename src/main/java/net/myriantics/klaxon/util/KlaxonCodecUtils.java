package net.myriantics.klaxon.util;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Ingredient;

import java.util.List;

public class KlaxonCodecUtils {

    public static final Codec<List<Ingredient>> INGREDIENT_LIST_CODEC = Codec.list(Ingredient.ALLOW_EMPTY_CODEC);
    public static final PacketCodec<ByteBuf, List<Ingredient>> INGREDIENT_LIST_PACKET_CODEC = PacketCodecs.codec(INGREDIENT_LIST_CODEC);
}
