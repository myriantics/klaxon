package net.myriantics.klaxon.util;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import it.unimi.dsi.fastutil.objects.Object2IntFunction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.myriantics.klaxon.mechanics.grapple_winch.CableDetachmentReason;

import java.util.List;
import java.util.Optional;
import java.util.function.ToIntFunction;

public abstract class KlaxonCodecUtils {

    public static final Codec<List<Ingredient>> INGREDIENT_LIST_CODEC = Codec.list(Ingredient.CODEC);
    public static final StreamCodec<RegistryFriendlyByteBuf, List<Ingredient>> INGREDIENT_LIST_PACKET_CODEC = Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list());
    public static final Codec<SoundEvent> OPTIONAL_SOUND_EVENT_CODEC = ExtraCodecs.optionalEmptyMap(SoundEvent.DIRECT_CODEC)
            .xmap(
                    soundEvent -> soundEvent.orElse(SoundEvents.EMPTY),
                    soundEvent -> soundEvent == null || soundEvent.equals(SoundEvents.EMPTY) ? Optional.empty() : Optional.of(soundEvent)
            );
    public static final StreamCodec<ByteBuf, SoundEvent> OPTIONAL_SOUND_EVENT_PACKET_CODEC = ByteBufCodecs.optional(SoundEvent.DIRECT_STREAM_CODEC)
            .map(
                    soundEvent -> soundEvent.orElse(SoundEvents.EMPTY),
                    soundEvent -> soundEvent == null || soundEvent.equals(SoundEvents.EMPTY) ? Optional.empty() : Optional.of(soundEvent)
            );
    public static final Codec<TagKey<Block>> BLOCK_TAG_CODEC = tagCodec(Registries.BLOCK);
    public static final StreamCodec<ByteBuf, TagKey<Block>> BLOCK_TAG_PACKET_CODEC = tagPacketCodec(Registries.BLOCK);
    public static final Codec<Holder<Block>> BLOCK_HOLDER_CODEC = BuiltInRegistries.BLOCK.holderByNameCodec();
    public static final Codec<Block> BLOCK_CODEC = BuiltInRegistries.BLOCK.byNameCodec();
    public static final StreamCodec<ByteBuf, Block> BLOCK_PACKET_CODEC = ByteBufCodecs.fromCodec(BLOCK_CODEC);

    public static <T> Codec<TagKey<T>> tagCodec(ResourceKey<? extends Registry<T>> registryKey) {
        return TagKey.hashedCodec(registryKey);
    }
    public static <T> StreamCodec<ByteBuf, TagKey<T>> tagPacketCodec(ResourceKey<? extends Registry<T>> registryKey) {
        return ByteBufCodecs.fromCodec(tagCodec(registryKey));
    }
}
