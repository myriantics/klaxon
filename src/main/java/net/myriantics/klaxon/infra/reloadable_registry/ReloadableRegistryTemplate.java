package net.myriantics.klaxon.infra.reloadable_registry;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.myriantics.klaxon.registry.KlaxonBuiltInRegistries;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public final class ReloadableRegistryTemplate<T extends KlaxonResourceReloadListener> {

    private final Holder<ReloadableRegistryTemplate<T>> intrusiveHolder = (Holder<ReloadableRegistryTemplate<T>>) (Object) KlaxonBuiltInRegistries.RELOADABLE_REGISTRY.createIntrusiveHolder(this);
    private final ListenerFactory<T> factory;
    private final String path;
    final ResourceKey<T> registryKey;

    public ReloadableRegistryTemplate(ResourceKey<T> registryKey, ListenerFactory<T> factory) {
        this.factory = factory;
        this.registryKey = registryKey;
        this.path = registryKey.location().getNamespace() + "/" + registryKey.location().getPath();
    }

    public String getPath() {
        return this.path;
    }

    public T create(HolderLookup.Provider registries) {
        return this.factory.create(this, registries);
    }

    public interface ListenerFactory<T extends KlaxonResourceReloadListener> {
        T create(ReloadableRegistryTemplate<T> instance, HolderLookup.Provider registries);
    }

    public Holder<ReloadableRegistryTemplate<T>> asHolder() {
        return this.intrusiveHolder;
    }
}
