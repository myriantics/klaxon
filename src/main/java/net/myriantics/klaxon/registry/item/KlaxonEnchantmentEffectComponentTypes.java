package net.myriantics.klaxon.registry.item;

import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Unit;
import net.myriantics.klaxon.KlaxonCommon;

import java.util.function.UnaryOperator;

public abstract class KlaxonEnchantmentEffectComponentTypes {

    public static final ComponentType<Unit> CANCEL_CERTAIN_VELOCITY_UPDATES = register(
            "velocity_update_immunity", builder -> builder.codec(Unit.CODEC)
    );

    private static <T> ComponentType<T> register(String name, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, KlaxonCommon.locate(name), builderOperator.apply(ComponentType.builder()).build());
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Enchantment Effect Component Types!");
    }
}
