package net.myriantics.klaxon.datagen.custom.reloadable;

import com.google.common.collect.Sets;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.myriantics.klaxon.infra.reloadable_registry.KlaxonResourceReloadListener;
import net.myriantics.klaxon.infra.reloadable_registry.ReloadableRegistryTemplate;
import net.myriantics.klaxon.registry.KlaxonBuiltInRegistries;
import net.myriantics.klaxon.registry.KlaxonRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public abstract class KlaxonReloadableRegistryProvider<T> implements DataProvider {
    final PackOutput.PathProvider entryProvider;
    private final CompletableFuture<HolderLookup.Provider> registries;
    protected final Holder<ReloadableRegistryTemplate<KlaxonResourceReloadListener<T>, T>> templateHolder;

    @SuppressWarnings("unchecked")
    public KlaxonReloadableRegistryProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ResourceKey<Registry<T>> registryResourceKey) {
        this.templateHolder = (Holder<ReloadableRegistryTemplate<KlaxonResourceReloadListener<T>,T>>) (Object) KlaxonBuiltInRegistries.RELOADABLE_REGISTRY.getHolderOrThrow(ResourceKey.create(KlaxonRegistries.RELOADABLE_REGISTRY, registryResourceKey.location()));
        this.entryProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, this.templateHolder.value().getPath());
        this.registries = registries;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return registries.thenCompose(provider -> this.run(output, provider));
    }

    private CompletableFuture<?> run(CachedOutput output, HolderLookup.Provider provider) {
        final Set<ResourceLocation> locations = Sets.newHashSet();
        final List<CompletableFuture<?>> futures = new ArrayList<>();
        this.buildDefinitions((key, definition) -> {
            if (!locations.add(key.location())) {
                throw new IllegalStateException("Duplicate value " + key);
            } else {
                futures.add(DataProvider.saveStable(output, provider, this.templateHolder.value().getElementCodec(), definition, KlaxonReloadableRegistryProvider.this.entryProvider.json(key.location())));
            }
        });
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    protected abstract void buildDefinitions(Output<T> output);

    protected interface Output<T> {
        void accept(ResourceKey<T> key, T value);
    }

    @Override
    public String getName() {
        return this.templateHolder.unwrapKey().get().location().toString();
    }
}
