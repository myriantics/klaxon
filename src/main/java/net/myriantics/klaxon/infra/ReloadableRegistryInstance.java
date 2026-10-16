package net.myriantics.klaxon.infra;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.myriantics.klaxon.registry.KlaxonBuiltInRegistries;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unchecked")
public final class ReloadableRegistryInstance<R extends KlaxonResourceReloadListener<T>, T, U extends CustomPacketPayload> {
    private final Holder<ReloadableRegistryInstance<R, T, U>> intrusiveHolder = (Holder<ReloadableRegistryInstance<R, T, U>>) (Object) KlaxonBuiltInRegistries.RELOADABLE_REGISTRY.createIntrusiveHolder(this);
    private final ListenerFactory<R, T, U> factory;
    private final String path;
    final ResourceKey<Registry<T>> registryKey;
    final @Nullable CustomPacketPayload.Type<U> type;
    final Codec<T> codec;

    public ReloadableRegistryInstance(ResourceKey<Registry<T>> registryKey, ListenerFactory<R, T, U> factory, @Nullable CustomPacketPayload.Type<U> type, Codec<T> codec) {
        this.factory = factory;
        this.registryKey = registryKey;
        this.path = registryKey.location().getNamespace() + "/" + registryKey.location().getPath();
        this.type = type;
        this.codec = codec;
    }

    public String getPath() {
        return this.path;
    }

    public R create(HolderLookup.Provider registries) {
        return this.factory.create(this, registries);
    }

    public interface ListenerFactory<R extends KlaxonResourceReloadListener<T>, T, U extends CustomPacketPayload> {
        R create(ReloadableRegistryInstance<R, T, U> instance, HolderLookup.Provider registries);
    }

    public Holder<ReloadableRegistryInstance<R, T, U>> asHolder() {
        return this.intrusiveHolder;
    }

    public @Nullable CustomPacketPayload.Type<U> getPacketType() {
        return this.type;
    }
}
