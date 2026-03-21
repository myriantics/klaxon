package net.myriantics.klaxon.registry.entity;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.myriantics.klaxon.KlaxonCommon;

public abstract class KlaxonStatusEffects {

    
    private static Holder<MobEffect> register(String name, MobEffect statusEffect) {
        return Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, KlaxonCommon.locate(name), statusEffect);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Status Effects!");
    }
}
