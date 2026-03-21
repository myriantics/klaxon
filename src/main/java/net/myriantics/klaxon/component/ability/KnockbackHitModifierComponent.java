package net.myriantics.klaxon.component.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.ItemStack;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record KnockbackHitModifierComponent(float multiplier, Optional<ResourceKey<DamageType>> damageType) {
    public KnockbackHitModifierComponent(float multiplier) {
        this(multiplier, Optional.empty());
    }

    public KnockbackHitModifierComponent(float multiplier, ResourceKey<DamageType> damageType) {
        this(multiplier, Optional.of(damageType));
    }

    public static final Codec<KnockbackHitModifierComponent> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                Codec.FLOAT.fieldOf("multiplier").forGetter(KnockbackHitModifierComponent::multiplier),
                ExtraCodecs.optionalEmptyMap(ResourceKey.codec(Registries.DAMAGE_TYPE)).fieldOf("damage_type").forGetter(KnockbackHitModifierComponent::damageType)
                ).apply(instance, KnockbackHitModifierComponent::new);
    });

    public static final StreamCodec<RegistryFriendlyByteBuf, KnockbackHitModifierComponent> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, KnockbackHitModifierComponent::multiplier,
            ByteBufCodecs.optional(ResourceKey.streamCodec(Registries.DAMAGE_TYPE)), KnockbackHitModifierComponent::damageType,
            KnockbackHitModifierComponent::new
    );

    public static @Nullable KnockbackHitModifierComponent get(ItemStack stack) {
        return stack.getComponents().get(KlaxonDataComponentTypes.KNOCKBACK_HIT_MODIFIER);
    }

    public void set(ItemStack stack) {
        stack.applyComponents(DataComponentMap.builder().set(KlaxonDataComponentTypes.KNOCKBACK_HIT_MODIFIER, this).build());
    }

    public boolean shouldFire(boolean knockbackHit) {
        return knockbackHit;
    }
}
