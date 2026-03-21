package net.myriantics.klaxon.component.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import org.jetbrains.annotations.Nullable;

public record ToolUseRecipeConfigComponent(SoundEvent usageSound, boolean canCosmeticUse) {
    public ToolUseRecipeConfigComponent(SoundEvent usageSound) {
        this(usageSound, false);
    }

    public static final ToolUseRecipeConfigComponent DEFAULT = new ToolUseRecipeConfigComponent(SoundEvents.STONE_BREAK);

    public static final Codec<ToolUseRecipeConfigComponent> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                SoundEvent.DIRECT_CODEC.fieldOf("usage_sound").forGetter(ToolUseRecipeConfigComponent::usageSound),
                Codec.BOOL.fieldOf("action_requires_recipe").forGetter(ToolUseRecipeConfigComponent::canCosmeticUse)
        ).apply(instance, ToolUseRecipeConfigComponent::new);
    });

    public static final StreamCodec<RegistryFriendlyByteBuf, ToolUseRecipeConfigComponent> PACKET_CODEC = StreamCodec.composite(
            SoundEvent.DIRECT_STREAM_CODEC, ToolUseRecipeConfigComponent::usageSound,
            ByteBufCodecs.BOOL, ToolUseRecipeConfigComponent::canCosmeticUse,
            ToolUseRecipeConfigComponent::new
    );

    public static @Nullable ToolUseRecipeConfigComponent get(ItemStack stack) {
        return stack.getComponents().get(KlaxonDataComponentTypes.TOOL_USE_RECIPE_CONFIG);
    }

    public void set(ItemStack stack) {
        stack.applyComponents(DataComponentMap.builder().set(KlaxonDataComponentTypes.TOOL_USE_RECIPE_CONFIG, this).build());
    }
}
