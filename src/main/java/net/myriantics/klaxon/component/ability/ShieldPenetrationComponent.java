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
import net.myriantics.klaxon.registry.minecraft.KlaxonDataComponentTypes;
import org.jetbrains.annotations.Nullable;

// When present on a weapon, allows it to penetrate through shields when performing a critical hit.
// Replaces default damage type with the one specified
public record ShieldPenetrationComponent(RegistryKey<DamageType> damageType, boolean requiresCritical, boolean requiresFullyCharged) {
    public static final Codec<ShieldPenetrationComponent> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                RegistryKey.createCodec(RegistryKeys.DAMAGE_TYPE).fieldOf("damage_type").forGetter(ShieldPenetrationComponent::damageType),
                Codec.BOOL.fieldOf("requires_critical").forGetter(ShieldPenetrationComponent::requiresCritical),
                Codec.BOOL.fieldOf("requires_fully_charged").forGetter(ShieldPenetrationComponent::requiresFullyCharged)
        ).apply(instance, ShieldPenetrationComponent::new);
    });

    public static final PacketCodec<RegistryByteBuf, ShieldPenetrationComponent> PACKET_CODEC = PacketCodec.tuple(
            RegistryKey.createPacketCodec(RegistryKeys.DAMAGE_TYPE), ShieldPenetrationComponent::damageType,
            PacketCodecs.BOOL, ShieldPenetrationComponent::requiresCritical,
            PacketCodecs.BOOL, ShieldPenetrationComponent::requiresFullyCharged,
            ShieldPenetrationComponent::new
    );

    public static @Nullable ShieldPenetrationComponent get(ItemStack stack) {
        return stack.getComponents().get(KlaxonDataComponentTypes.SHIELD_PENETRATION);
    }

    public static void set(ItemStack stack, ShieldPenetrationComponent component) {
        stack.applyComponentsFrom(ComponentMap.builder().add(KlaxonDataComponentTypes.SHIELD_PENETRATION, component).build());
    }

    public boolean shouldFire(boolean critical, boolean fullyCharged) {
        return (!requiresFullyCharged || fullyCharged) && (!requiresCritical || critical);
    }
}
