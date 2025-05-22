package net.myriantics.klaxon.component.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.component.ComponentMap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.myriantics.klaxon.registry.minecraft.KlaxonDataComponentTypes;
import org.jetbrains.annotations.Nullable;

public record InstabreakToolComponent(TagKey<Block> instabreakableBlocks) {
    public static Codec<InstabreakToolComponent> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                TagKey.codec(RegistryKeys.BLOCK).fieldOf("instabreakable_blocks").forGetter(InstabreakToolComponent::instabreakableBlocks)
        ).apply(instance, InstabreakToolComponent::new);
    });

    public static PacketCodec<RegistryByteBuf, InstabreakToolComponent> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.codec(TagKey.codec(RegistryKeys.BLOCK)), InstabreakToolComponent::instabreakableBlocks,
            InstabreakToolComponent::new
    );

    public @Nullable static InstabreakToolComponent get(ItemStack stack) {
        return stack.getComponents().get(KlaxonDataComponentTypes.INSTABREAK_TOOL_COMPONENT);
    }

    public void set(ItemStack stack) {
        stack.applyComponentsFrom(ComponentMap.builder().add(KlaxonDataComponentTypes.INSTABREAK_TOOL_COMPONENT, this).build());
    }

    public boolean isCorrectForInstabreak(BlockState state)  {
        return state.isIn(instabreakableBlocks);
    };
}
