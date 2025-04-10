package net.myriantics.klaxon.registry.minecraft;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.entity.effects.HeavyStatusEffect;

public class KlaxonStatusEffects {
    public static final RegistryEntry<StatusEffect> HEAVY = register("heavy",
            new HeavyStatusEffect(StatusEffectCategory.NEUTRAL, 7300466));

    private static RegistryEntry<StatusEffect> register(String name, StatusEffect statusEffect) {
        return Registry.registerReference(Registries.STATUS_EFFECT, KlaxonCommon.locate(name), statusEffect);
    }

    public static void registerStatusEffects() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Status Effects!");
    }
}
