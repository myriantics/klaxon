package net.myriantics.klaxon.util;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
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
    public static final Codec<TagKey<Block>> BLOCK_TAG_CODEC = tagCodec(RegistryKeys.BLOCK);
    public static final PacketCodec<ByteBuf, TagKey<Block>> BLOCK_TAG_PACKET_CODEC = tagPacketCodec(RegistryKeys.BLOCK);
    public static final Codec<Block> BLOCK_CODEC = Registries.BLOCK.getCodec();
    public static final PacketCodec<ByteBuf, Block> BLOCK_PACKET_CODEC = PacketCodecs.codec(BLOCK_CODEC);

    public static <T> Codec<TagKey<T>> tagCodec(RegistryKey<? extends Registry<T>> registryKey) {
        return TagKey.codec(registryKey);
    }
    public static <T> PacketCodec<ByteBuf, TagKey<T>> tagPacketCodec(RegistryKey<? extends Registry<T>> registryKey) {
        return PacketCodecs.codec(tagCodec(registryKey));
    }
}
