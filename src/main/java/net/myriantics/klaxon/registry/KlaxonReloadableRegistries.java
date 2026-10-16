package net.myriantics.klaxon.registry;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.infra.KlaxonResourceReloadListener;
import net.myriantics.klaxon.infra.ReloadableRegistryInstance;
import net.myriantics.klaxon.mechanics.machinery.part.DynamicMachinePartDefinition;
import net.myriantics.klaxon.mechanics.machinery.part.KlaxonDynamicMachinePartLoader;
import net.myriantics.klaxon.networking.clientbound.ClientboundDynamicMachinePartSyncPayload;
import net.myriantics.klaxon.registry.misc.KlaxonPackets;
import org.jetbrains.annotations.Nullable;

public class KlaxonReloadableRegistries {

    public static final Holder<ReloadableRegistryInstance<KlaxonDynamicMachinePartLoader, DynamicMachinePartDefinition, ClientboundDynamicMachinePartSyncPayload>> DYNAMIC_MACHINE_PART_DEFINITION = register(
            KlaxonRegistries.DYNAMIC_MACHINE_PART_DEFINITION,
            KlaxonDynamicMachinePartLoader::new,
            KlaxonPackets.CLIENTBOUND_DYNAMIC_MACHINE_PART_SYNC,
            DynamicMachinePartDefinition.CODEC
    );

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Reloadable Registries!");
    }

    @SuppressWarnings("unchecked")
    private static <R extends KlaxonResourceReloadListener<T>, T, U extends CustomPacketPayload> Holder<ReloadableRegistryInstance<R, T, U>> register(ResourceKey<Registry<T>> key, ReloadableRegistryInstance.ListenerFactory<R, T, U> factory, @Nullable CustomPacketPayload.Type<U> type, Codec<T> codec) {
        return (Holder<ReloadableRegistryInstance<R, T, U>>) (Object) Registry.registerForHolder(
                KlaxonBuiltInRegistries.RELOADABLE_REGISTRY,
                key.location(),
                new ReloadableRegistryInstance<>(
                        key,
                        factory,
                        type,
                        codec
                )
        );
    }

    public interface Access {
        <V extends KlaxonResourceReloadListener<T>, T, U extends CustomPacketPayload> V klaxon$getRegistry(Holder<ReloadableRegistryInstance<V, T, U>> holder);
    }
}
