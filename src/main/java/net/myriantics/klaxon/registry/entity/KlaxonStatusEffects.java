package net.myriantics.klaxon.registry.entity;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.entity.effects.HeavyStatusEffect;

public abstract class KlaxonStatusEffects {
    public static final RegistryEntry<StatusEffect> HEAVY = register("heavy",
            new HeavyStatusEffect(StatusEffectCategory.NEUTRAL, 7300466)
                    .addAttributeModifier(KlaxonEntityAttributes.GENERIC_WEIGHT, KlaxonCommon.locate("effect.heavy"), 0.5f, EntityAttributeModifier.Operation.ADD_VALUE));

    private static RegistryEntry<StatusEffect> register(String name, StatusEffect statusEffect) {
        return Registry.registerReference(Registries.STATUS_EFFECT, KlaxonCommon.locate(name), statusEffect);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Status Effects!");
    }
}
