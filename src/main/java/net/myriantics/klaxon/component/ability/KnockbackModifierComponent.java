package net.myriantics.klaxon.component.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.ComponentMap;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.dynamic.Codecs;
import net.myriantics.klaxon.registry.minecraft.KlaxonDataComponentTypes;
import org.jetbrains.annotations.Nullable;

import javax.swing.text.html.Option;
import java.util.Optional;

public record KnockbackModifierComponent(float multiplier, boolean requiresKnockbackHit, Optional<RegistryKey<DamageType>> damageType) {
    public KnockbackModifierComponent(float multiplier, boolean requiresKnockbackHit) {
        this(multiplier, requiresKnockbackHit, Optional.empty());
    }

    public KnockbackModifierComponent(float multiplier, boolean requiresKnockbackHit, RegistryKey<DamageType> damageType) {
        this(multiplier, requiresKnockbackHit, Optional.of(damageType));
    }

    public static final Codec<KnockbackModifierComponent> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                Codec.FLOAT.fieldOf("multiplier").forGetter(KnockbackModifierComponent::multiplier),
                Codec.BOOL.fieldOf("needs_knockback_hit").forGetter(KnockbackModifierComponent::requiresKnockbackHit),
                Codecs.optional(RegistryKey.createCodec(RegistryKeys.DAMAGE_TYPE)).fieldOf("damage_type").forGetter(KnockbackModifierComponent::damageType)
                ).apply(instance, KnockbackModifierComponent::new);
    });

    public static final PacketCodec<RegistryByteBuf, KnockbackModifierComponent> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.FLOAT, KnockbackModifierComponent::multiplier,
            PacketCodecs.BOOL, KnockbackModifierComponent::requiresKnockbackHit,
            PacketCodecs.optional(RegistryKey.createPacketCodec(RegistryKeys.DAMAGE_TYPE)), KnockbackModifierComponent::damageType,
            KnockbackModifierComponent::new
    );

    public static @Nullable KnockbackModifierComponent get(ItemStack stack) {
        return stack.getComponents().get(KlaxonDataComponentTypes.KNOCKBACK_HIT_MODIFIER);
    }

    public void set(ItemStack stack) {
        stack.applyComponentsFrom(ComponentMap.builder().add(KlaxonDataComponentTypes.KNOCKBACK_HIT_MODIFIER, this).build());
    }

    public boolean shouldFire(boolean knockbackHit) {
        return knockbackHit || !requiresKnockbackHit();
    }
}
