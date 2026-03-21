package net.myriantics.klaxon.registry.item;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Unit;
import net.myriantics.klaxon.KlaxonCommon;

import java.util.function.UnaryOperator;

public abstract class KlaxonEnchantmentEffectComponentTypes {

    public static final DataComponentType<Unit> CANCEL_CERTAIN_VELOCITY_UPDATES = register(
            "velocity_update_immunity", builder -> builder.persistent(Unit.CODEC)
    );

    private static <T> DataComponentType<T> register(String name, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return Registry.register(BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, KlaxonCommon.locate(name), builderOperator.apply(DataComponentType.builder()).build());
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Enchantment Effect Component Types!");
    }
}
