package net.myriantics.klaxon.component.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.ItemStack;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

// When present on a weapon, allows it to disable shields while dealing damage through them if the condition is met.
// Replaces default damage type with the one specified
public record ShieldBreachingComponent(Optional<ResourceKey<DamageType>> damageType, Condition condition) {
    public ShieldBreachingComponent(ResourceKey<DamageType> damageType, Condition condition) {
        this(Optional.of(damageType), condition);
    }

    public static final Codec<ShieldBreachingComponent> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                ExtraCodecs.optionalEmptyMap(ResourceKey.codec(Registries.DAMAGE_TYPE)).fieldOf("damage_type").forGetter(ShieldBreachingComponent::damageType),
                Condition.CODEC.fieldOf("condition").forGetter(ShieldBreachingComponent::condition)
        ).apply(instance, ShieldBreachingComponent::new);
    });

    public static final StreamCodec<RegistryFriendlyByteBuf, ShieldBreachingComponent> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ResourceKey.streamCodec(Registries.DAMAGE_TYPE)), ShieldBreachingComponent::damageType,
            Condition.PACKET_CODEC, ShieldBreachingComponent::condition,
            ShieldBreachingComponent::new
    );

    public static @Nullable ShieldBreachingComponent get(ItemStack stack) {
        return stack.getComponents().get(KlaxonDataComponentTypes.SHIELD_BREACHING.value());
    }

    public void set(ItemStack stack) {
        stack.applyComponents(DataComponentMap.builder().set(KlaxonDataComponentTypes.SHIELD_BREACHING.value(), this).build());
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

    public enum Condition implements StringRepresentable {
        ALWAYS,
        FULLY_CHARGED,
        CRITICAL,
        KNOCKBACK;

        @Override
        public String getSerializedName() {
            return toString().toLowerCase();
        }

        public static Codec<Condition> CODEC = StringRepresentable.fromEnum(Condition::values);
        public static StreamCodec<ByteBuf, Condition> PACKET_CODEC = ByteBufCodecs.fromCodec(CODEC);
    }
}
