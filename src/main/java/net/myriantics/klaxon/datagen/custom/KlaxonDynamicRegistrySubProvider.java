package net.myriantics.klaxon.datagen.custom;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;

public abstract class KlaxonDynamicRegistrySubProvider<T> {
    protected final RegistryWrapper.WrapperLookup wrapperLookup;
    private final FabricDynamicRegistryProvider.Entries entries;

    public KlaxonDynamicRegistrySubProvider(RegistryWrapper.WrapperLookup wrapperLookup, FabricDynamicRegistryProvider.Entries entries) {
        this.wrapperLookup = wrapperLookup;
        this.entries = entries;
        this.build();
    }

    protected abstract void build();

    protected T add(RegistryKey<T> key, T value) {
        entries.add(key, value);
        return value;
    }
}
