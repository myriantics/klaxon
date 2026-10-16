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
public final class ReloadableRegistryInstance<T> {
    private final Holder<ReloadableRegistryInstance<T>> intrusiveHolder = (Holder<ReloadableRegistryInstance<T>>) (Object) KlaxonBuiltInRegistries.RELOADABLE_REGISTRY.createIntrusiveHolder(this);
    private final ListenerFactory<T> factory;
    private final String path;
    final ResourceKey<Registry<T>> registryKey;
    final @Nullable CustomPacketPayload.Type<? extends CustomPacketPayload> payloadType;
    final Codec<T> codec;

    private ReloadableRegistryInstance(ResourceKey<Registry<T>> registryKey, ListenerFactory<T> factory, @Nullable CustomPacketPayload.Type<? extends CustomPacketPayload> payloadType, Codec<T> codec) {
        this.factory = factory;
        this.registryKey = registryKey;
        this.path = registryKey.location().getNamespace() + "/" + registryKey.location().getPath();
        this.payloadType = payloadType;
        this.codec = codec;
    }

    public String getPath() {
        return this.path;
    }

    public KlaxonResourceReloadListener<T> create(HolderLookup.Provider registries) {
        return this.factory.create(this, registries);
    }

    public interface ListenerFactory<T> {
        KlaxonResourceReloadListener<T> create(ReloadableRegistryInstance<T> instance, HolderLookup.Provider registries);
    }

    public Holder<ReloadableRegistryInstance<T>> asHolder() {
        return this.intrusiveHolder;
    }

    public @Nullable CustomPacketPayload.Type<?> getPacketType() {
        return this.payloadType;
    }

    public static <T> Builder<T> builder(ResourceKey<Registry<T>> key, ListenerFactory<T> factory, Codec<T> codec) {
        return new Builder<>(key, factory, codec);
    }

    public static class Builder<T> {

        private final ResourceKey<Registry<T>> key;
        private final ListenerFactory<T> factory;
        private final Codec<T> codec;
        private CustomPacketPayload.Type<? extends CustomPacketPayload> payloadType;

        private Builder(ResourceKey<Registry<T>> key, ListenerFactory<T> factory, Codec<T> codec) {
            this.key = key;
            this.factory = factory;
            this.codec = codec;
        }

        public Builder<T> synced(CustomPacketPayload.Type<? extends CustomPacketPayload> payloadType) {
            this.payloadType = payloadType;
            return this;
        }

        public ReloadableRegistryInstance<T> build() {
            return new ReloadableRegistryInstance<>(
                    this.key,
                    this.factory,
                    this.payloadType,
                    this.codec
            );
        }
    }
}
