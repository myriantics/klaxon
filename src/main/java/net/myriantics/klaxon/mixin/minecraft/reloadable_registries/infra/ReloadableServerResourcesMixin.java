package net.myriantics.klaxon.mixin.minecraft.reloadable_registries.infra;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.commands.Commands;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.flag.FeatureFlagSet;
import net.myriantics.klaxon.infra.KlaxonResourceReloadListener;
import net.myriantics.klaxon.infra.ReloadableRegistryInstance;
import net.myriantics.klaxon.registry.KlaxonBuiltInRegistries;
import net.myriantics.klaxon.registry.KlaxonReloadableRegistries;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Mixin(ReloadableServerResources.class)
public abstract class ReloadableServerResourcesMixin implements KlaxonReloadableRegistries.Access {

    @Shadow
    @Final
    private ReloadableServerResources.ConfigurableRegistryLookup registryLookup;

    @Unique
    private Map<ResourceKey<?>, KlaxonResourceReloadListener<?>> klaxon$reloadableRegistryListeners = Map.of();

    @Inject(
            method = "<init>",
            at = @At(value = "TAIL")
    )
    private void klaxon$initDynamicMachinePartDataLoader(RegistryAccess.Frozen registryAccess, FeatureFlagSet enabledFeatures, Commands.CommandSelection commandSelection, int functionCompilationLevel, CallbackInfo ci) {
        ImmutableMap.Builder<ResourceKey<?>, KlaxonResourceReloadListener<?>> builder = ImmutableMap.builder();

        KlaxonBuiltInRegistries.RELOADABLE_REGISTRY.holders().forEach(holder -> {
            builder.put(holder.key(), holder.value().create(this.registryLookup));
        });

        this.klaxon$reloadableRegistryListeners = builder.build();
    }

    @ModifyReturnValue(
            method = "listeners",
            at = @At(value = "RETURN")
    )
    private List<PreparableReloadListener> klaxon$injectListeners(List<PreparableReloadListener> original) {
        return ImmutableList.<PreparableReloadListener>builder().addAll(original).addAll(this.klaxon$reloadableRegistryListeners.values()).build();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V extends KlaxonResourceReloadListener<T>, T> V klaxon$getReloadableRegistry(@UnknownNullability Holder<ReloadableRegistryInstance<T>> registry, Class<V> clazz) throws NoSuchElementException {
        @Nullable V manager = (V) this.klaxon$reloadableRegistryListeners.get(registry.unwrapKey().get());
        if (manager == null) {
            throw new NoSuchElementException();
        } else {
            return manager;
        }
    }
}
