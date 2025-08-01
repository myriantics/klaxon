package net.myriantics.klaxon.component.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.ComponentMap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.sound.SoundEvent;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import org.jetbrains.annotations.Nullable;

public record ToolUseRecipeConfigComponent(SoundEvent usageSound, boolean canCosmeticUse) {
    public ToolUseRecipeConfigComponent(SoundEvent usageSound) {
        this(usageSound, false);
    }

    public static final Codec<ToolUseRecipeConfigComponent> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                SoundEvent.CODEC.fieldOf("usage_sound").forGetter(ToolUseRecipeConfigComponent::usageSound),
                Codec.BOOL.fieldOf("action_requires_recipe").forGetter(ToolUseRecipeConfigComponent::canCosmeticUse)
        ).apply(instance, ToolUseRecipeConfigComponent::new);
    });

    public static final PacketCodec<RegistryByteBuf, ToolUseRecipeConfigComponent> PACKET_CODEC = PacketCodec.tuple(
            SoundEvent.PACKET_CODEC, ToolUseRecipeConfigComponent::usageSound,
            PacketCodecs.BOOL, ToolUseRecipeConfigComponent::canCosmeticUse,
            ToolUseRecipeConfigComponent::new
    );

    public static @Nullable ToolUseRecipeConfigComponent get(ItemStack stack) {
        return stack.getComponents().get(KlaxonDataComponentTypes.TOOL_USE_RECIPE_CONFIG);
    }

    public void set(ItemStack stack) {
        stack.applyComponentsFrom(ComponentMap.builder().add(KlaxonDataComponentTypes.TOOL_USE_RECIPE_CONFIG, this).build());
    }
}
