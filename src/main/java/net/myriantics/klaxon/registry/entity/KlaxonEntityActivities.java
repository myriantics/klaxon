package net.myriantics.klaxon.registry.entity;

import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.myriantics.klaxon.KlaxonCommon;

public abstract class KlaxonEntityActivities {

    public static final Activity GERALD_SEARCHING = register("gerald_searching");

    private static Activity register(String name) {
        return Registry.register(Registries.ACTIVITY, KlaxonCommon.locate(name), new Activity(name));
    }
}
