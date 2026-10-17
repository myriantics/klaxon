package net.myriantics.klaxon.mixin.minecraft.reloadable_registries;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.Connection;
import net.minecraft.resources.ResourceKey;
import net.myriantics.klaxon.infra.reloadable_registry.KlaxonResourceReloadListener;
import net.myriantics.klaxon.infra.reloadable_registry.ReloadableRegistryManager;
import net.myriantics.klaxon.infra.reloadable_registry.ReloadableRegistryTemplate;
import net.myriantics.klaxon.registry.KlaxonBuiltInRegistries;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Objects;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin extends ClientCommonPacketListenerImpl implements ReloadableRegistryManager.Access {

    @Shadow
    public abstract RegistryAccess.Frozen registryAccess();

    @Unique
    private ReloadableRegistryManager klaxon$reloadableRegistryManager = ReloadableRegistryManager.EMPTY;

    protected ClientPacketListenerMixin(Minecraft minecraft, Connection connection, CommonListenerCookie commonListenerCookie) {
        super(minecraft, connection, commonListenerCookie);
    }

    @SuppressWarnings("unchecked")
    @Inject(
            method = "<init>",
            at = @At(value = "TAIL")
    )
    private void klaxon$initReloadableRegistries(Minecraft minecraft, Connection connection, CommonListenerCookie commonListenerCookie, CallbackInfo ci) {
        ImmutableMap.Builder<ResourceKey<ReloadableRegistryTemplate<? extends KlaxonResourceReloadListener<?>, ?>>, KlaxonResourceReloadListener<?>> builder = ImmutableMap.builder();

        ReloadableRegistryManager.SYNCED_REGISTRY_MAP.forEach((key, type) -> {
            builder.put(key, Objects.requireNonNull(KlaxonBuiltInRegistries.RELOADABLE_REGISTRY.get((ResourceKey<ReloadableRegistryTemplate<?, ?>>) (Object) key)).create(this.registryAccess()));
        });

        this.klaxon$reloadableRegistryManager = new ReloadableRegistryManager((Map<ResourceKey<ReloadableRegistryTemplate<?,?>>, KlaxonResourceReloadListener<?>>) (Object) builder.build());
    }

    @Override
    public @Nullable ReloadableRegistryManager klaxon$getReloadableRegistryManager() {
        return this.klaxon$reloadableRegistryManager;
    }
}
