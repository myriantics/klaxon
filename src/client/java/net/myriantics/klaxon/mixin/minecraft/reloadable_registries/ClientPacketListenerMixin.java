package net.myriantics.klaxon.mixin.minecraft.reloadable_registries;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.Connection;
import net.minecraft.resources.ResourceKey;
import net.myriantics.klaxon.infra.KlaxonResourceReloadListener;
import net.myriantics.klaxon.infra.ReloadableRegistryInstance;
import net.myriantics.klaxon.registry.KlaxonBuiltInRegistries;
import net.myriantics.klaxon.registry.KlaxonReloadableRegistries;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.NoSuchElementException;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin extends ClientCommonPacketListenerImpl implements KlaxonReloadableRegistries.Access {

    @Shadow
    public abstract RegistryAccess.Frozen registryAccess();

    @Unique
    private Map<ResourceKey<?>, KlaxonResourceReloadListener<?>> klaxon$reloadableRegistryListeners = Map.of();

    protected ClientPacketListenerMixin(Minecraft minecraft, Connection connection, CommonListenerCookie commonListenerCookie) {
        super(minecraft, connection, commonListenerCookie);
    }

    @Inject(
            method = "<init>",
            at = @At(value = "TAIL")
    )
    private void klaxon$initReloadableRegistries(Minecraft minecraft, Connection connection, CommonListenerCookie commonListenerCookie, CallbackInfo ci) {
        ImmutableMap.Builder<ResourceKey<?>, KlaxonResourceReloadListener<?>> builder = ImmutableMap.builder();

        KlaxonBuiltInRegistries.RELOADABLE_REGISTRY.holders().forEach(holder -> {
            if (holder.value().getPacketType() != null) {
                builder.put(holder.key(), holder.value().create(this.registryAccess()));
            }
        });

        this.klaxon$reloadableRegistryListeners = builder.build();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V extends KlaxonResourceReloadListener<T>, T> V klaxon$getReloadableRegistry(@UnknownNullability Holder<ReloadableRegistryInstance<T>> holder, Class<V> clazz) {
        @Nullable V manager = (V) this.klaxon$reloadableRegistryListeners.get(holder.unwrapKey().get());
        if (manager == null) {
            throw new NoSuchElementException();
        } else {
            return manager;
        }
    }
}
