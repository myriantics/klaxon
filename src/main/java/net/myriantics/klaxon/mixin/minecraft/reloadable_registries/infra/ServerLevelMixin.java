package net.myriantics.klaxon.mixin.minecraft.reloadable_registries.infra;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.myriantics.klaxon.infra.reloadable_registry.KlaxonResourceReloadListener;
import net.myriantics.klaxon.infra.reloadable_registry.ReloadableRegistryManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.NoSuchElementException;
import java.util.function.Consumer;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin implements ReloadableRegistryManager.Access {

    @Shadow
    public abstract @NotNull MinecraftServer getServer();

    @Override
    public @Nullable ReloadableRegistryManager klaxon$getReloadableRegistryManager() {
        return ReloadableRegistryManager.getAccess(this.getServer()).klaxon$getReloadableRegistryManager();
    }
}
