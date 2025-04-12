package net.myriantics.klaxon.registry.minecraft;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.component.ability.ShieldPenetrationComponent;
import net.myriantics.klaxon.component.configuration.DamageTypeOverrideComponent;
import net.myriantics.klaxon.component.ability.WalljumpAbilityComponent;

import java.util.function.UnaryOperator;

public class KlaxonDataComponentTypes {

    public static final ComponentType<WalljumpAbilityComponent> WALLJUMP_ABILITY = register("walljump_ability",
            builder -> {
                builder.codec(WalljumpAbilityComponent.CODEC);
                builder.packetCodec(WalljumpAbilityComponent.PACKET_CODEC);
                return builder;
            });

    public static final ComponentType<ShieldPenetrationComponent> SHIELD_PENETRATION = register("shield_penetration",
            builder -> {
                builder.codec(ShieldPenetrationComponent.CODEC);
                builder.packetCodec(ShieldPenetrationComponent.PACKET_CODEC);
                return builder;
            });

    public static final ComponentType<DamageTypeOverrideComponent> DAMAGE_TYPE_OVERRIDE = register("damage_type_override",
            builder -> {
                builder.codec(DamageTypeOverrideComponent.CODEC);
                builder.packetCodec(DamageTypeOverrideComponent.PACKET_CODEC);
                return builder;
            });

    public static final ComponentType<Boolean> UNENCHANTABLE = register("unenchantable",
            builder -> builder.codec(Codec.BOOL).packetCodec(PacketCodecs.BOOL));

    private static <T> ComponentType<T> register(String name, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, KlaxonCommon.locate(name), builderOperator.apply(ComponentType.builder()).build());
    }

    public static void registerKlaxonComponents() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Data Component Types!");
    }
}
