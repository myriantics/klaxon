package net.myriantics.klaxon.infra.reloadable_registry;

import com.google.common.collect.Maps;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public final class ReloadableRegistryManager {

    public static final HashMap<ResourceKey<ReloadableRegistryTemplate<? extends KlaxonResourceReloadListener<?>, ?>>, CustomPacketPayload.Type<?>> SYNCED_REGISTRY_MAP = new HashMap<>();

    public static final ReloadableRegistryManager EMPTY = new ReloadableRegistryManager(Map.of());

    private final Map<ResourceKey<ReloadableRegistryTemplate<?, ?>>, Entry<?>> key2EntryMap;

    @SuppressWarnings("unchecked")
    public ReloadableRegistryManager(Map<ResourceKey<ReloadableRegistryTemplate<?, ?>>, KlaxonResourceReloadListener<?>> key2ListenerMap) {
        this.key2EntryMap = Maps.transformEntries(
                key2ListenerMap,
                (key, value) -> {
                    if (value instanceof ClientSynced<?> synced) {
                        return new Entry<>(value, new SyncInstance<>(
                                (CustomPacketPayload.Type<CustomPacketPayload>) Objects.requireNonNull(SYNCED_REGISTRY_MAP.get(key), "Attempted to register a synced reloadable registry without associating a payload type!"),
                                (ClientSynced<CustomPacketPayload>) synced
                        ));
                    } else {
                        return new Entry<>(value, null);
                    }
                }
        );
    }

    public Iterator<? extends KlaxonResourceReloadListener<?>> listenerIterator() {
        return this.key2EntryMap.values().stream().map(Entry::listener).iterator();
    }

    public Iterator<? extends SyncInstance<? extends CustomPacketPayload>> syncIterator() {
        return this.key2EntryMap.values().stream().map(Entry::syncInstance).iterator();
    }

    public void forEachListener(Consumer<KlaxonResourceReloadListener<?>> consumer) {
        for (Entry<?> entry : this.key2EntryMap.values()) {
            consumer.accept(entry.listener);
        }
    }

    public <T extends KlaxonResourceReloadListener<U>, U> T getListener(Holder<ReloadableRegistryTemplate<T, U>> holder) {
        return this.getListener(holder.unwrapKey().get());
    }

    @SuppressWarnings("unchecked")
    public <T extends KlaxonResourceReloadListener<U>, U> T getListener(ResourceKey<ReloadableRegistryTemplate<T, U>> key) {
        return (T) this.key2EntryMap.get(key).listener;
    }

    @SuppressWarnings("unchecked")
    public @Nullable SyncInstance<CustomPacketPayload> getSyncInstance(ResourceKey<ReloadableRegistryTemplate<? extends KlaxonResourceReloadListener<?>, ?>> key) {
        return (SyncInstance<CustomPacketPayload>) this.key2EntryMap.get(key).syncInstance;
    }

    public static @Nullable ReloadableRegistryManager get(Object object) {
        if (object instanceof Access access) {
            return access.klaxon$getReloadableRegistryManager();
        } else {
            return null;
        }
    }

    public interface Access {
        @Nullable ReloadableRegistryManager klaxon$getReloadableRegistryManager();
    }

    private record Entry<T extends KlaxonResourceReloadListener<?>>(T listener, SyncInstance<?> syncInstance) {
    }

    public record SyncInstance<U extends CustomPacketPayload>(CustomPacketPayload.Type<U> payloadType, ClientSynced<U> synchronizer) {
    }
}
