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
import net.myriantics.klaxon.registry.misc.KlaxonPackets;

import java.util.function.UnaryOperator;

public class KlaxonReloadableRegistries {

    public static final Holder<ReloadableRegistryInstance<DynamicMachinePartDefinition>> DYNAMIC_MACHINE_PART_DEFINITION = register(
            KlaxonRegistries.DYNAMIC_MACHINE_PART_DEFINITION,
            KlaxonDynamicMachinePartLoader::new,
            DynamicMachinePartDefinition.CODEC,
            builder -> builder.synced(KlaxonPackets.CLIENTBOUND_DYNAMIC_MACHINE_PART_SYNC)
    );

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Reloadable Registries!");
    }

    @SuppressWarnings("unchecked")
    private static <T> Holder<ReloadableRegistryInstance<T>> register(ResourceKey<Registry<T>> key, ReloadableRegistryInstance.ListenerFactory<T> factory, Codec<T> codec, UnaryOperator<ReloadableRegistryInstance.Builder<T>> operator) {
        return (Holder<ReloadableRegistryInstance<T>>) (Object) Registry.registerForHolder(
                KlaxonBuiltInRegistries.RELOADABLE_REGISTRY,
                key.location(),
                operator.apply(ReloadableRegistryInstance.builder(key, factory, codec)).build()
        );
    }

    public interface Access {
        @SuppressWarnings("unchecked")
        default <T> KlaxonResourceReloadListener<T> klaxon$getReloadableRegistry(Holder<ReloadableRegistryInstance<T>> holder) {
            return (KlaxonResourceReloadListener<T>) this.klaxon$getReloadableRegistry(holder, KlaxonResourceReloadListener.class);
        }

        <V extends KlaxonResourceReloadListener<T>, T> V klaxon$getReloadableRegistry(Holder<ReloadableRegistryInstance<T>> holder, Class<V> clazz);
    }
}
