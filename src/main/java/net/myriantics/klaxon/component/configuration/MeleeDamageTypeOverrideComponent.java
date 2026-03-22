package net.myriantics.klaxon.component.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.ItemStack;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import org.jetbrains.annotations.Nullable;

// Overrides the default damage type for the weapon with a new one.
public record MeleeDamageTypeOverrideComponent(ResourceKey<DamageType> damageType) {
    public static final Codec<MeleeDamageTypeOverrideComponent> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                ResourceKey.codec(Registries.DAMAGE_TYPE).fieldOf("damage_type").forGetter(MeleeDamageTypeOverrideComponent::damageType)
        ).apply(instance, MeleeDamageTypeOverrideComponent::new);
    });

    public static final StreamCodec<RegistryFriendlyByteBuf, MeleeDamageTypeOverrideComponent> PACKET_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(Registries.DAMAGE_TYPE), MeleeDamageTypeOverrideComponent::damageType,
            MeleeDamageTypeOverrideComponent::new
    );

    public static @Nullable MeleeDamageTypeOverrideComponent get(ItemStack stack) {
        return stack.getComponents().get(KlaxonDataComponentTypes.MELEE_DAMAGE_TYPE_OVERRIDE.value());
    }

    public void set(ItemStack stack) {
        stack.applyComponents(DataComponentMap.builder().set(KlaxonDataComponentTypes.MELEE_DAMAGE_TYPE_OVERRIDE.value(), this).build());
    }
}
