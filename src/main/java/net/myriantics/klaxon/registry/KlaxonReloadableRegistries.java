package net.myriantics.klaxon.registry;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.infra.reloadable_registry.KlaxonResourceReloadListener;
import net.myriantics.klaxon.infra.reloadable_registry.KlaxonSimpleResourceReloadListener;
import net.myriantics.klaxon.infra.reloadable_registry.ReloadableRegistryManager;
import net.myriantics.klaxon.infra.reloadable_registry.ReloadableRegistryTemplate;
import net.myriantics.klaxon.mechanics.machinery.part.DynamicMachinePartDefinition;
import net.myriantics.klaxon.mechanics.machinery.part.KlaxonDynamicMachinePartLoader;
import net.myriantics.klaxon.networking.clientbound.ClientboundSimpleReloadableRegistrySyncPacket;
import net.myriantics.klaxon.recipe.tool_usage.ToolUsageRecipeType;
import net.myriantics.klaxon.registry.misc.KlaxonPackets;

import java.util.Map;

public class KlaxonReloadableRegistries {

    public static final Holder<ReloadableRegistryTemplate<KlaxonDynamicMachinePartLoader, DynamicMachinePartDefinition>> DYNAMIC_MACHINE_PART_DEFINITION = registerSynced(
            KlaxonRegistries.DYNAMIC_MACHINE_PART_DEFINITION,
            KlaxonDynamicMachinePartLoader::new,
            DynamicMachinePartDefinition.CODEC,
            KlaxonPackets.CLIENTBOUND_DYNAMIC_MACHINE_PART_SYNC
    );

    public static final Holder<ReloadableRegistryTemplate<KlaxonSimpleResourceReloadListener<ToolUsageRecipeType>, ToolUsageRecipeType>> TOOL_USAGE_RECIPE_TYPE = registerSimpleSynced(
            KlaxonRegistries.TOOL_USAGE_RECIPE_TYPE,
            ToolUsageRecipeType.CODEC,
            ToolUsageRecipeType.STREAM_CODEC
    );

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Reloadable Registries!");
    }

    @SuppressWarnings("unchecked")
    private static <T extends KlaxonResourceReloadListener<U>, U> Holder<ReloadableRegistryTemplate<T, U>> registerSimpleSynced(ResourceKey<Registry<U>> registryResourceKey, Codec<U> elementCodec, StreamCodec<RegistryFriendlyByteBuf, U> elementStreamCodec) {

        CustomPacketPayload.Type<ClientboundSimpleReloadableRegistrySyncPacket<U>> type = new CustomPacketPayload.Type<>(registryResourceKey.location().withPrefix("clientbound/simple_reloadable_registry/"));
        KlaxonSimpleResourceReloadListener.ElementSyncPacketFactory<U> factory = (elements) -> new ClientboundSimpleReloadableRegistrySyncPacket<U>(elements) {
            @Override
            public Type<? extends CustomPacketPayload> type() {
                return type;
            }
        };

        Holder<ReloadableRegistryTemplate<T, U>> holder = (Holder<ReloadableRegistryTemplate<T, U>>) (Object) register(
                registryResourceKey,
                (instance, registries) -> new KlaxonSimpleResourceReloadListener<U>((ReloadableRegistryTemplate<? extends KlaxonResourceReloadListener<?>, U>) instance, registries, registryResourceKey, factory),
                elementCodec
        );

        StreamCodec<RegistryFriendlyByteBuf, ClientboundSimpleReloadableRegistrySyncPacket<U>> streamCodec = ByteBufCodecs.<RegistryFriendlyByteBuf, ResourceKey<U>, U, Map<ResourceKey<U>, U>>map(
                Object2ObjectOpenHashMap::new,
                ResourceKey.streamCodec(registryResourceKey),
                elementStreamCodec
        ).map(factory::create, ClientboundSimpleReloadableRegistrySyncPacket::getElements);
        PayloadTypeRegistry.playS2C().register(type, streamCodec);
        addSynced((Holder<ReloadableRegistryTemplate<?, ?>>) (Object) holder, type);

        return holder;
    }

    @SuppressWarnings("unchecked")
    private static <T extends KlaxonResourceReloadListener<U>, U> Holder<ReloadableRegistryTemplate<T, U>> registerSynced(ResourceKey<Registry<U>> key, ReloadableRegistryTemplate.ListenerFactory<T, U> factory, Codec<U> elementCodec, CustomPacketPayload.Type<?> payloadType) {
        Holder<ReloadableRegistryTemplate<T, U>> holder = register(key, factory, elementCodec);
        addSynced((Holder<ReloadableRegistryTemplate<?, ?>>) (Object) holder, payloadType);
        return holder;
    }

    @SuppressWarnings("unchecked")
    private static <T extends KlaxonResourceReloadListener<U>, U> Holder<ReloadableRegistryTemplate<T, U>> register(ResourceKey<Registry<U>> baseKey, ReloadableRegistryTemplate.ListenerFactory<T, U> factory, Codec<U> elementCodec) {
        ResourceKey<T> internalKey = (ResourceKey<T>) (Object) ResourceKey.create(KlaxonRegistries.RELOADABLE_REGISTRY, baseKey.location());

        return (Holder<ReloadableRegistryTemplate<T, U>>) (Object) Registry.registerForHolder(
                KlaxonBuiltInRegistries.RELOADABLE_REGISTRY,
                internalKey.location(),
                new ReloadableRegistryTemplate<>(
                        internalKey,
                        factory,
                        elementCodec
                )
        );
    }

    @SuppressWarnings("unchecked")
    private static void addSynced(Holder<ReloadableRegistryTemplate<? extends KlaxonResourceReloadListener<?>, ?>> holder, CustomPacketPayload.Type<?> payloadType) {
        ReloadableRegistryManager.SYNCED_REGISTRY_MAP.put(holder.unwrapKey().get(), payloadType);
    }
}
