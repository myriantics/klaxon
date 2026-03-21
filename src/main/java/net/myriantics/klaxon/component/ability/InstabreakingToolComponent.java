package net.myriantics.klaxon.component.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import org.jetbrains.annotations.Nullable;

public record InstabreakingToolComponent(TagKey<Block> instabreakableBlocks) {
    public static Codec<InstabreakingToolComponent> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                TagKey.hashedCodec(Registries.BLOCK).fieldOf("instabreakable_blocks").forGetter(InstabreakingToolComponent::instabreakableBlocks)
        ).apply(instance, InstabreakingToolComponent::new);
    });

    public static StreamCodec<RegistryFriendlyByteBuf, InstabreakingToolComponent> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodec(TagKey.hashedCodec(Registries.BLOCK)), InstabreakingToolComponent::instabreakableBlocks,
            InstabreakingToolComponent::new
    );

    public @Nullable static InstabreakingToolComponent get(ItemStack stack) {
        return stack.getComponents().get(KlaxonDataComponentTypes.INSTABREAK_TOOL_COMPONENT);
    }

    public void set(ItemStack stack) {
        stack.applyComponents(DataComponentMap.builder().set(KlaxonDataComponentTypes.INSTABREAK_TOOL_COMPONENT, this).build());
    }

    public boolean isCorrectForInstabreak(BlockState state)  {
        return state.is(instabreakableBlocks);
    };
}
