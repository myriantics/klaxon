package net.myriantics.klaxon.infra.reloadable_registry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.serialization.Codec;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;

public abstract class KlaxonResourceReloadListener extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    protected final ReloadableRegistryTemplate<?> instance;
    protected final HolderLookup.Provider registries;

    protected KlaxonResourceReloadListener(ReloadableRegistryTemplate<?> instance, HolderLookup.Provider registries) {
        super(GSON, instance.getPath());
        this.instance = instance;
        this.registries = registries;
    }
}
