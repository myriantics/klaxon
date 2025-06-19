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

// When present on a weapon, allows it to penetrate through shields when performing a critical hit.
// Replaces default damage type with the one specified
public record ShieldBreachingComponent(Optional<RegistryKey<DamageType>> damageType, boolean requiresCritical, boolean requiresFullyCharged) {
    public ShieldBreachingComponent(RegistryKey<DamageType> damageType, boolean requiresCritical, boolean requiresFullyCharged) {
        this(Optional.of(damageType), requiresCritical, requiresFullyCharged);
    }

    public static final Codec<ShieldBreachingComponent> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                Codecs.optional(RegistryKey.createCodec(RegistryKeys.DAMAGE_TYPE)).fieldOf("damage_type").forGetter(ShieldBreachingComponent::damageType),
                Codec.BOOL.fieldOf("requires_critical").forGetter(ShieldBreachingComponent::requiresCritical),
                Codec.BOOL.fieldOf("requires_fully_charged").forGetter(ShieldBreachingComponent::requiresFullyCharged)
        ).apply(instance, ShieldBreachingComponent::new);
    });

    public static final PacketCodec<RegistryByteBuf, ShieldBreachingComponent> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.optional(RegistryKey.createPacketCodec(RegistryKeys.DAMAGE_TYPE)), ShieldBreachingComponent::damageType,
            PacketCodecs.BOOL, ShieldBreachingComponent::requiresCritical,
            PacketCodecs.BOOL, ShieldBreachingComponent::requiresFullyCharged,
            ShieldBreachingComponent::new
    );

    public static @Nullable ShieldBreachingComponent get(ItemStack stack) {
        return stack.getComponents().get(KlaxonDataComponentTypes.SHIELD_BREACHING);
    }

    public void set(ItemStack stack) {
        stack.applyComponentsFrom(ComponentMap.builder().add(KlaxonDataComponentTypes.SHIELD_BREACHING, this).build());
    }

    public boolean shouldFire(boolean critical, boolean fullyCharged) {
        return (!requiresFullyCharged || fullyCharged) && (!requiresCritical || critical);
    }
}
