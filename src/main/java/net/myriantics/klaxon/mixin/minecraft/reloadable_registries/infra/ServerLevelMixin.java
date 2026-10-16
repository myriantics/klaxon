package net.myriantics.klaxon.mixin.minecraft.reloadable_registries.infra;

import net.minecraft.core.Holder;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.myriantics.klaxon.infra.KlaxonResourceReloadListener;
import net.myriantics.klaxon.infra.ReloadableRegistryInstance;
import net.myriantics.klaxon.registry.KlaxonReloadableRegistries;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.NoSuchElementException;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin implements KlaxonReloadableRegistries.Access {

    @Shadow
    public abstract @NotNull MinecraftServer getServer();

    @Override
    public <V extends KlaxonResourceReloadListener<T>, T, U extends CustomPacketPayload> V klaxon$getRegistry(Holder<ReloadableRegistryInstance<V, T, U>> registry) throws NoSuchElementException {
        return ((KlaxonReloadableRegistries.Access) this.getServer()).klaxon$getRegistry(registry);
    }
}
