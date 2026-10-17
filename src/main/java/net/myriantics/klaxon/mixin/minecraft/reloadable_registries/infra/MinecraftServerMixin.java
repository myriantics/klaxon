package net.myriantics.klaxon.mixin.minecraft.reloadable_registries.infra;

import net.minecraft.server.MinecraftServer;
import net.myriantics.klaxon.infra.reloadable_registry.ReloadableRegistryManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements ReloadableRegistryManager.Access {

    @Shadow
    private MinecraftServer.ReloadableResources resources;

    @Override
    public @Nullable ReloadableRegistryManager klaxon$getReloadableRegistryManager() {
        return ((ReloadableRegistryManager.Access) this.resources.managers()).klaxon$getReloadableRegistryManager();
    }
}
