package net.myriantics.klaxon.registry;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.infra.KlaxonResourceReloadListener;
import net.myriantics.klaxon.infra.ReloadableRegistryInstance;
import net.myriantics.klaxon.mechanics.machinery.part.DynamicMachinePartDefinition;
import net.myriantics.klaxon.mechanics.machinery.part.KlaxonDynamicMachinePartLoader;

import java.util.NoSuchElementException;

public class KlaxonReloadableRegistries {

    public static final Holder<ReloadableRegistryInstance<KlaxonDynamicMachinePartLoader, DynamicMachinePartDefinition>> DYNAMIC_MACHINE_PART_DEFINITION = register(
            KlaxonRegistries.DYNAMIC_MACHINE_PART_DEFINITION,
            KlaxonDynamicMachinePartLoader::new,
            DynamicMachinePartDefinition.CODEC,
            true
    );

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Reloadable Registries!");
    }

    @SuppressWarnings("unchecked")
    private static <R extends KlaxonResourceReloadListener<T>, T> Holder<ReloadableRegistryInstance<R, T>> register(ResourceKey<Registry<T>> key, ReloadableRegistryInstance.ListenerFactory<R, T> factory, Codec<T> codec, boolean syncToClient) {
        return (Holder<ReloadableRegistryInstance<R, T>>) Registry.registerForHolder(
                (Registry<? super ReloadableRegistryInstance<R, T>>) KlaxonBuiltInRegistries.RELOADABLE_REGISTRY,
                key.location(),
                new ReloadableRegistryInstance<>(
                        key,
                        factory,
                        codec,
                        syncToClient
                )
        );
    }

    public interface Access {
        <U extends KlaxonResourceReloadListener<T>, T> U klaxon$getRegistry(Holder<ReloadableRegistryInstance<U, T>> holder);
    }
}
