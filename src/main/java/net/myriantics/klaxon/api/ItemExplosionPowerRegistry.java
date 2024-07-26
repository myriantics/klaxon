package net.myriantics.klaxon.api;

import net.fabricmc.fabric.api.util.Item2ObjectMap;

public interface ItemExplosionPowerRegistry extends Item2ObjectMap<Double> {
    ItemExplosionPowerRegistry INSTANCE = new ItemExplosionPowerRegistryImpl();
}
