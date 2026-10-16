package net.myriantics.klaxon.infra;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.myriantics.klaxon.registry.KlaxonBuiltInRegistries;

import java.util.function.Function;

@SuppressWarnings("unchecked")
public final class ReloadableRegistryInstance<R extends KlaxonResourceReloadListener<T>, T> {
    private final Holder<ReloadableRegistryInstance<R, T>> intrusiveHolder = (Holder<ReloadableRegistryInstance<R, T>>) (Object) KlaxonBuiltInRegistries.RELOADABLE_REGISTRY.createIntrusiveHolder(this);
    private final ListenerFactory<R, T> factory;
    private final String path;
    final ResourceKey<Registry<T>> registryKey;
    final Codec<T> codec;
    final boolean syncToClient;

    public ReloadableRegistryInstance(ResourceKey<Registry<T>> registryKey, ListenerFactory<R, T> factory, Codec<T> codec) {
        this(registryKey, factory, codec, true);
    }

    public ReloadableRegistryInstance(ResourceKey<Registry<T>> registryKey, ListenerFactory<R, T> factory, Codec<T> codec, boolean syncToClient) {
        this.factory = factory;
        this.registryKey = registryKey;
        this.path = registryKey.location().getNamespace() + "/" + registryKey.location().getPath();
        this.syncToClient = syncToClient;
        this.codec = codec;
    }

    public String getPath() {
        return this.path;
    }

    public R create(HolderLookup.Provider registries) {
        return this.factory.create(this, registries);
    }

    public interface ListenerFactory<R extends KlaxonResourceReloadListener<T>, T> {
        R create(ReloadableRegistryInstance<R, T> instance, HolderLookup.Provider registries);
    }
}
