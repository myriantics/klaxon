package net.myriantics.klaxon.infra;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.serialization.Codec;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;

public abstract class KlaxonResourceReloadListener<T> extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    protected final ReloadableRegistryInstance<? extends KlaxonResourceReloadListener<T>, T, ? extends CustomPacketPayload> instance;
    protected final HolderLookup.Provider registries;

    protected KlaxonResourceReloadListener(ReloadableRegistryInstance<? extends KlaxonResourceReloadListener<T>, T, ? extends CustomPacketPayload> instance, HolderLookup.Provider registries) {
        super(GSON, instance.getPath());
        this.instance = instance;
        this.registries = registries;
    }

    protected final Codec<T> elementCodec() {
        return this.instance.codec;
    }
}
