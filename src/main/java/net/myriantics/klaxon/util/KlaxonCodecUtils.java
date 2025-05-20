package net.myriantics.klaxon.util;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.dynamic.Codecs;

import java.util.List;
import java.util.Optional;

public abstract class KlaxonCodecUtils {

    public static final Codec<List<Ingredient>> INGREDIENT_LIST_CODEC = Codec.list(Ingredient.ALLOW_EMPTY_CODEC);
    public static final PacketCodec<ByteBuf, List<Ingredient>> INGREDIENT_LIST_PACKET_CODEC = PacketCodecs.codec(INGREDIENT_LIST_CODEC);
    public static final Codec<SoundEvent> OPTIONAL_SOUND_EVENT_CODEC = Codecs.optional(SoundEvent.CODEC)
            .xmap(
                    soundEvent -> soundEvent.orElse(SoundEvents.INTENTIONALLY_EMPTY),
                    soundEvent -> soundEvent == null || soundEvent.equals(SoundEvents.INTENTIONALLY_EMPTY) ? Optional.empty() : Optional.of(soundEvent)
            );
    public static final PacketCodec<ByteBuf, SoundEvent> OPTIONAL_SOUND_EVENT_PACKET_CODEC = PacketCodecs.optional(SoundEvent.PACKET_CODEC)
            .xmap(
                    soundEvent -> soundEvent.orElse(SoundEvents.INTENTIONALLY_EMPTY),
                    soundEvent -> soundEvent == null || soundEvent.equals(SoundEvents.INTENTIONALLY_EMPTY) ? Optional.empty() : Optional.of(soundEvent)
            );
}
