package net.myriantics.klaxon.datagen.custom;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;

public abstract class KlaxonDynamicRegistrySubProvider<T> {
    protected final HolderLookup.Provider wrapperLookup;
    private final FabricDynamicRegistryProvider.Entries entries;

    public KlaxonDynamicRegistrySubProvider(HolderLookup.Provider wrapperLookup, FabricDynamicRegistryProvider.Entries entries) {
        this.wrapperLookup = wrapperLookup;
        this.entries = entries;
        this.build();
    }

    protected abstract void build();

    protected T add(ResourceKey<T> key, T value) {
        entries.add(key, value);
        return value;
    }
}
