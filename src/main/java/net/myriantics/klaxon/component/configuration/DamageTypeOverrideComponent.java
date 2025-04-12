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
public record DamageTypeOverrideComponent(RegistryKey<DamageType> damageType) {
    public static final Codec<DamageTypeOverrideComponent> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                RegistryKey.createCodec(RegistryKeys.DAMAGE_TYPE).fieldOf("damage_type").forGetter(DamageTypeOverrideComponent::damageType)
        ).apply(instance, DamageTypeOverrideComponent::new);
    });

    public static final PacketCodec<RegistryByteBuf, DamageTypeOverrideComponent> PACKET_CODEC = PacketCodec.tuple(
            RegistryKey.createPacketCodec(RegistryKeys.DAMAGE_TYPE), DamageTypeOverrideComponent::damageType,
            DamageTypeOverrideComponent::new
    );

    public static @Nullable DamageTypeOverrideComponent get(ItemStack stack) {
        return stack.getComponents().get(KlaxonDataComponentTypes.DAMAGE_TYPE_OVERRIDE);
    }

    public static void set(ItemStack stack, DamageTypeOverrideComponent component) {
        stack.applyComponentsFrom(ComponentMap.builder().add(KlaxonDataComponentTypes.DAMAGE_TYPE_OVERRIDE, component).build());
    }
}
