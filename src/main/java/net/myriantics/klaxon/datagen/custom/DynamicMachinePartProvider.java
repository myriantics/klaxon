package net.myriantics.klaxon.datagen.custom;

import com.google.common.collect.Sets;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.mechanics.machinery.part.KlaxonDynamicMachinePartLoader;
import net.myriantics.klaxon.mechanics.machinery.part.patch.MachinePartDataComponentPatchBuilder;
import net.myriantics.klaxon.registry.KlaxonRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;

public abstract class DynamicMachinePartProvider implements DataProvider {
    final PackOutput.PathProvider entryProvider;
    private final CompletableFuture<HolderLookup.Provider> registries;

    public DynamicMachinePartProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        this.entryProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, KlaxonDynamicMachinePartLoader.PATH);
        this.registries = registries;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return registries.thenCompose(provider -> this.run(output, provider));
    }

    private CompletableFuture<?> run(CachedOutput output, HolderLookup.Provider provider) {
        final Set<ResourceLocation> locations = Sets.newHashSet();
        final List<CompletableFuture<?>> futures = new ArrayList<>();
        this.buildDefinitions((location, definition) -> {
            if (!locations.add(location)) {
                throw new IllegalStateException("Duplicate definition " + location);
            } else {
                futures.add(DataProvider.saveStable(output, provider, KlaxonDynamicMachinePartLoader.Definition.CODEC, definition, DynamicMachinePartProvider.this.entryProvider.json(location)));
            }
        });
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    protected abstract void buildDefinitions(Output output);

    protected void add(ResourceLocation location, UnaryOperator<MachinePartDataComponentPatchBuilder> operator) {

    }

    protected interface Output {
        void accept(ResourceLocation location, KlaxonDynamicMachinePartLoader.Definition definition);
    }
}
