package net.myriantics.klaxon.mixin.minecraft.reloadable_registries;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.myriantics.klaxon.infra.KlaxonResourceReloadListener;
import net.myriantics.klaxon.infra.ReloadableRegistryInstance;
import net.myriantics.klaxon.registry.KlaxonReloadableRegistries;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.NoSuchElementException;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin implements KlaxonReloadableRegistries.Access {

    @Shadow
    public abstract @Nullable ClientPacketListener getConnection();

    @Override
    public <V extends KlaxonResourceReloadListener<T>, T, U extends CustomPacketPayload> V klaxon$getRegistry(Holder<ReloadableRegistryInstance<V, T, U>> holder) {
        if (this.getConnection() == null) {
            throw new NoSuchElementException();
        } else {
            return ((KlaxonReloadableRegistries.Access) this.getConnection()).klaxon$getRegistry(holder);
        }
    }
}
