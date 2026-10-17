package net.myriantics.klaxon.infra.reloadable_registry;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.myriantics.klaxon.registry.KlaxonBuiltInRegistries;

@SuppressWarnings("unchecked")
public final class ReloadableRegistryTemplate<T extends KlaxonResourceReloadListener<?>, U> {

    private final Holder<ReloadableRegistryTemplate<T, U>> intrusiveHolder = (Holder<ReloadableRegistryTemplate<T, U>>) (Object) KlaxonBuiltInRegistries.RELOADABLE_REGISTRY.createIntrusiveHolder(this);
    private final ListenerFactory<T, U> factory;
    private final Codec<U> elementCodec;
    private final String path;
    final ResourceKey<T> registryKey;

    public ReloadableRegistryTemplate(ResourceKey<T> registryKey, ListenerFactory<T, U> factory, Codec<U> elementCodec) {
        this.factory = factory;
        this.registryKey = registryKey;
        this.elementCodec = elementCodec;
        this.path = registryKey.location().getNamespace() + "/" + registryKey.location().getPath();
    }

    public String getPath() {
        return this.path;
    }

    public Codec<U> getElementCodec() {
        return this.elementCodec;
    }

    public T create(HolderLookup.Provider registries) {
        return this.factory.create(this, registries);
    }

    public interface ListenerFactory<T extends KlaxonResourceReloadListener<?>, U> {
        T create(ReloadableRegistryTemplate<T, U> instance, HolderLookup.Provider registries);
    }

    public Holder<ReloadableRegistryTemplate<T, U>> asHolder() {
        return this.intrusiveHolder;
    }
}
