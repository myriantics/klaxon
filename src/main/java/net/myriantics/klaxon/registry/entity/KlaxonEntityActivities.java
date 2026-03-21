package net.myriantics.klaxon.registry.entity;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.schedule.Activity;
import net.myriantics.klaxon.KlaxonCommon;

public abstract class KlaxonEntityActivities {

    public static final Activity GERALD_SEARCHING = register("gerald_searching");

    private static Activity register(String name) {
        return Registry.register(BuiltInRegistries.ACTIVITY, KlaxonCommon.locate(name), new Activity(name));
    }
}
