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

import java.util.Optional;

public record KnockbackHitModifierComponent(float multiplier, Optional<RegistryKey<DamageType>> damageType) {
    public KnockbackHitModifierComponent(float multiplier) {
        this(multiplier, Optional.empty());
    }

    public KnockbackHitModifierComponent(float multiplier, RegistryKey<DamageType> damageType) {
        this(multiplier, Optional.of(damageType));
    }

    public static final Codec<KnockbackHitModifierComponent> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                Codec.FLOAT.fieldOf("multiplier").forGetter(KnockbackHitModifierComponent::multiplier),
                Codecs.optional(RegistryKey.createCodec(RegistryKeys.DAMAGE_TYPE)).fieldOf("damage_type").forGetter(KnockbackHitModifierComponent::damageType)
                ).apply(instance, KnockbackHitModifierComponent::new);
    });

    public static final PacketCodec<RegistryByteBuf, KnockbackHitModifierComponent> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.FLOAT, KnockbackHitModifierComponent::multiplier,
            PacketCodecs.optional(RegistryKey.createPacketCodec(RegistryKeys.DAMAGE_TYPE)), KnockbackHitModifierComponent::damageType,
            KnockbackHitModifierComponent::new
    );

    public static @Nullable KnockbackHitModifierComponent get(ItemStack stack) {
        return stack.getComponents().get(KlaxonDataComponentTypes.KNOCKBACK_HIT_MODIFIER);
    }

    public void set(ItemStack stack) {
        stack.applyComponentsFrom(ComponentMap.builder().add(KlaxonDataComponentTypes.KNOCKBACK_HIT_MODIFIER, this).build());
    }

    public boolean shouldFire(boolean knockbackHit) {
        return knockbackHit;
    }
}
