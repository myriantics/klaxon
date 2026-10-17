package net.myriantics.klaxon.infra.reloadable_registry;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.networking.clientbound.ClientboundSimpleReloadableRegistrySyncPacket;

import java.util.Map;

public final class KlaxonSimpleResourceReloadListener<U> extends KlaxonResourceReloadListener<U> implements ClientSynced<ClientboundSimpleReloadableRegistrySyncPacket<U>> {

    private final ResourceKey<Registry<U>> registryResourceKey;
    private final ElementSyncPacketFactory<U> packetFactory;
    private Map<ResourceKey<U>, U> elements = Map.of();

    public KlaxonSimpleResourceReloadListener(ReloadableRegistryTemplate<? extends KlaxonResourceReloadListener<?>, U> instance, HolderLookup.Provider registries, ResourceKey<Registry<U>> key, ElementSyncPacketFactory<U> packetFactory) {
        super(instance, registries);
        this.registryResourceKey = key;
        this.packetFactory = packetFactory;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
        RegistryOps<JsonElement> ops = this.registries.createSerializationContext(JsonOps.INSTANCE);

        ImmutableMap.Builder<ResourceKey<U>, U> builder = ImmutableMap.builder();

        for (Map.Entry<ResourceLocation, JsonElement> entry : object.entrySet()) {
            try {
                builder.put(ResourceKey.create(this.registryResourceKey, entry.getKey()), this.elementCodec().parse(ops, entry.getValue()).getOrThrow(JsonParseException::new));
            } catch (IllegalArgumentException | JsonParseException e) {
                KlaxonCommon.LOGGER.error("Parsing error loading element " + entry.getKey() + " into " + this.instance.registryKey);
            }
        }

        this.elements = builder.build();
    }

    public Map<ResourceKey<U>, U> getElements() {
        return this.elements;
    }

    @Override
    public ClientboundSimpleReloadableRegistrySyncPacket<U> getSyncPacket() {
        return this.packetFactory.create(this.elements);
    }

    @Override
    public void loadFromPacket(ClientboundSimpleReloadableRegistrySyncPacket<U> packet) {
        this.elements = packet.getElements();
    }

    public interface ElementSyncPacketFactory<U> {
        ClientboundSimpleReloadableRegistrySyncPacket<U> create(Map<ResourceKey<U>, U> elements);
    }
}
