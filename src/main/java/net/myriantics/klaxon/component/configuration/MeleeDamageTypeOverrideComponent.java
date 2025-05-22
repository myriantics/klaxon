package net.myriantics.klaxon.component.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.ComponentMap;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.myriantics.klaxon.registry.minecraft.KlaxonDataComponentTypes;
import org.jetbrains.annotations.Nullable;

// Overrides the default damage type for the weapon with a new one.
public record MeleeDamageTypeOverrideComponent(RegistryKey<DamageType> damageType) {
    public static final Codec<MeleeDamageTypeOverrideComponent> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                RegistryKey.createCodec(RegistryKeys.DAMAGE_TYPE).fieldOf("damage_type").forGetter(MeleeDamageTypeOverrideComponent::damageType)
        ).apply(instance, MeleeDamageTypeOverrideComponent::new);
    });

    public static final PacketCodec<RegistryByteBuf, MeleeDamageTypeOverrideComponent> PACKET_CODEC = PacketCodec.tuple(
            RegistryKey.createPacketCodec(RegistryKeys.DAMAGE_TYPE), MeleeDamageTypeOverrideComponent::damageType,
            MeleeDamageTypeOverrideComponent::new
    );

    public static @Nullable MeleeDamageTypeOverrideComponent get(ItemStack stack) {
        return stack.getComponents().get(KlaxonDataComponentTypes.DAMAGE_TYPE_OVERRIDE);
    }

    public void set(ItemStack stack) {
        stack.applyComponentsFrom(ComponentMap.builder().add(KlaxonDataComponentTypes.DAMAGE_TYPE_OVERRIDE, this).build());
    }
}
