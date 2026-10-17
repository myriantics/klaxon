package net.myriantics.klaxon.mixin.minecraft.reloadable_registries;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.myriantics.klaxon.infra.reloadable_registry.ReloadableRegistryManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin implements ReloadableRegistryManager.Access {

    @Shadow
    public abstract @Nullable ClientPacketListener getConnection();

    @Override
    public @Nullable ReloadableRegistryManager klaxon$getReloadableRegistryManager() {
        if (this.getConnection() == null) {
            return null;
        } else {
            return ((ReloadableRegistryManager.Access) this.getConnection()).klaxon$getReloadableRegistryManager();
        }
    }
}
