package net.myriantics.klaxon.component.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.ComponentMap;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.myriantics.klaxon.registry.minecraft.KlaxonDataComponentTypes;
import org.jetbrains.annotations.Nullable;

public record KnockbackModifierComponent(float multiplier, RegistryKey<DamageType> damageType, boolean requiresKnockbackHit) {
    public KnockbackModifierComponent(float multiplier, boolean requiresKnockbackHit) {
        this(multiplier, null, requiresKnockbackHit);
    }

    public static final Codec<KnockbackModifierComponent> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                Codec.FLOAT.fieldOf("multiplier").forGetter(KnockbackModifierComponent::multiplier),
                RegistryKey.createCodec(RegistryKeys.DAMAGE_TYPE).fieldOf("damage_type").forGetter(KnockbackModifierComponent::damageType),
                Codec.BOOL.fieldOf("needs_knockback_hit").forGetter(KnockbackModifierComponent::requiresKnockbackHit)
        ).apply(instance, KnockbackModifierComponent::new);
    });

    public static final PacketCodec<RegistryByteBuf, KnockbackModifierComponent> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.FLOAT, KnockbackModifierComponent::multiplier,
            RegistryKey.createPacketCodec(RegistryKeys.DAMAGE_TYPE), KnockbackModifierComponent::damageType,
            PacketCodecs.BOOL, KnockbackModifierComponent::requiresKnockbackHit,
            KnockbackModifierComponent::new
    );

    public static @Nullable KnockbackModifierComponent get(ItemStack stack) {
        return stack.getComponents().get(KlaxonDataComponentTypes.KNOCKBACK_MODIFIER);
    }

    public void set(ItemStack stack) {
        stack.applyComponentsFrom(ComponentMap.builder().add(KlaxonDataComponentTypes.KNOCKBACK_MODIFIER, this).build());
    }

    public boolean shouldFire(boolean knockbackHit) {
        return knockbackHit || !requiresKnockbackHit();
    }
}
