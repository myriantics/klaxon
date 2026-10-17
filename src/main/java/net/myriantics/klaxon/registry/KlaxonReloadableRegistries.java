package net.myriantics.klaxon.registry;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.infra.reloadable_registry.KlaxonResourceReloadListener;
import net.myriantics.klaxon.infra.reloadable_registry.ReloadableRegistryManager;
import net.myriantics.klaxon.infra.reloadable_registry.ReloadableRegistryTemplate;
import net.myriantics.klaxon.mechanics.machinery.part.DynamicMachinePartDefinition;
import net.myriantics.klaxon.mechanics.machinery.part.KlaxonDynamicMachinePartLoader;
import net.myriantics.klaxon.registry.misc.KlaxonPackets;

import java.util.function.UnaryOperator;

public class KlaxonReloadableRegistries {

    public static final Holder<ReloadableRegistryTemplate<KlaxonDynamicMachinePartLoader>> DYNAMIC_MACHINE_PART_DEFINITION = registerSynced(
            "dynamic_machine_part_definition",
            KlaxonDynamicMachinePartLoader::new,
            KlaxonPackets.CLIENTBOUND_DYNAMIC_MACHINE_PART_SYNC
    );

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Reloadable Registries!");
    }

    @SuppressWarnings("unchecked")
    private static <T extends KlaxonResourceReloadListener> Holder<ReloadableRegistryTemplate<T>> registerSynced(String name, ReloadableRegistryTemplate.ListenerFactory<T> factory, CustomPacketPayload.Type<?> payloadType) {
        Holder<ReloadableRegistryTemplate<T>> holder = register(name, factory);
        ReloadableRegistryManager.SYNCED_REGISTRY_MAP.put((ResourceKey<ReloadableRegistryTemplate<?>>) (Object) holder.unwrapKey().get(), payloadType);
        return holder;
    }

    @SuppressWarnings("unchecked")
    private static <T extends KlaxonResourceReloadListener> Holder<ReloadableRegistryTemplate<T>> register(String name, ReloadableRegistryTemplate.ListenerFactory<T> factory) {
        ResourceKey<T> key = (ResourceKey<T>) (Object) ResourceKey.create(KlaxonRegistries.RELOADABLE_REGISTRY, KlaxonCommon.locate(name));

        return (Holder<ReloadableRegistryTemplate<T>>) (Object) Registry.registerForHolder(
                KlaxonBuiltInRegistries.RELOADABLE_REGISTRY,
                key.location(),
                new ReloadableRegistryTemplate<>(
                        key,
                        factory
                )
        );
    }

}
