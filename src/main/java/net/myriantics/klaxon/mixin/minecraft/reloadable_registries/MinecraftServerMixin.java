package net.myriantics.klaxon.mixin.minecraft.reloadable_registries;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.myriantics.klaxon.infra.KlaxonResourceReloadListener;
import net.myriantics.klaxon.infra.ReloadableRegistryInstance;
import net.myriantics.klaxon.registry.KlaxonReloadableRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.NoSuchElementException;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements KlaxonReloadableRegistries.Access {

    @Shadow
    private MinecraftServer.ReloadableResources resources;

    @Override
    public <U extends KlaxonResourceReloadListener<T>, T> U klaxon$getRegistry(Holder<ReloadableRegistryInstance<U, T>> registry) throws NoSuchElementException {
        return ((KlaxonReloadableRegistries.Access) this.resources.managers()).klaxon$getRegistry(registry);
    }
}
