package net.myriantics.klaxon.networking.clientbound;

import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.myriantics.klaxon.infra.reloadable_registry.KlaxonSimpleResourceReloadListener;
import net.myriantics.klaxon.infra.reloadable_registry.ReloadableRegistryManager;
import net.myriantics.klaxon.infra.reloadable_registry.ReloadableRegistryTemplate;
import net.myriantics.klaxon.registry.KlaxonBuiltInRegistries;
import net.myriantics.klaxon.registry.KlaxonRegistries;
import net.myriantics.klaxon.registry.misc.KlaxonPackets;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class ClientboundSimpleReloadableRegistrySyncPacket<T> implements CustomPacketPayload {
    private final Map<ResourceKey<T>, T> elements;

    protected ClientboundSimpleReloadableRegistrySyncPacket(Map<ResourceKey<T>, T> elements) {
        this.elements = elements;
    }

    public final Map<ResourceKey<T>, T> getElements() {
        return this.elements;
    }
}
