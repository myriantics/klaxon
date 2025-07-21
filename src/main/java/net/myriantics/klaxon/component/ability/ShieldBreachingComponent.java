package net.myriantics.klaxon.component.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.component.ComponentMap;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.dynamic.Codecs;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

// When present on a weapon, allows it to disable shields while dealing damage through them if the condition is met.
// Replaces default damage type with the one specified
public record ShieldBreachingComponent(Optional<RegistryKey<DamageType>> damageType, Condition condition) {
    public ShieldBreachingComponent(RegistryKey<DamageType> damageType, Condition condition) {
        this(Optional.of(damageType), condition);
    }

    public static final Codec<ShieldBreachingComponent> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                Codecs.optional(RegistryKey.createCodec(RegistryKeys.DAMAGE_TYPE)).fieldOf("damage_type").forGetter(ShieldBreachingComponent::damageType),
                Condition.CODEC.fieldOf("condition").forGetter(ShieldBreachingComponent::condition)
        ).apply(instance, ShieldBreachingComponent::new);
    });

    public static final PacketCodec<RegistryByteBuf, ShieldBreachingComponent> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.optional(RegistryKey.createPacketCodec(RegistryKeys.DAMAGE_TYPE)), ShieldBreachingComponent::damageType,
            Condition.PACKET_CODEC, ShieldBreachingComponent::condition,
            ShieldBreachingComponent::new
    );

    public static @Nullable ShieldBreachingComponent get(ItemStack stack) {
        return stack.getComponents().get(KlaxonDataComponentTypes.SHIELD_BREACHING);
    }

    public void set(ItemStack stack) {
        stack.applyComponentsFrom(ComponentMap.builder().add(KlaxonDataComponentTypes.SHIELD_BREACHING, this).build());
    }

    public boolean shouldFire(boolean critical, boolean fullyCharged, boolean knockbackHit) {
        switch (condition) {
            case ALWAYS -> {
                return true;
            }
            case FULLY_CHARGED -> {
                return fullyCharged;
            }
            case CRITICAL -> {
                return critical;
            }
            case KNOCKBACK -> {
                return knockbackHit;
            }
        }

        return false;
    }

    public enum Condition implements StringIdentifiable {
        ALWAYS,
        FULLY_CHARGED,
        CRITICAL,
        KNOCKBACK;

        @Override
        public String asString() {
            return toString().toLowerCase();
        }

        public static Codec<Condition> CODEC = StringIdentifiable.createCodec(Condition::values);
        public static PacketCodec<ByteBuf, Condition> PACKET_CODEC = PacketCodecs.codec(CODEC);
    }
}
