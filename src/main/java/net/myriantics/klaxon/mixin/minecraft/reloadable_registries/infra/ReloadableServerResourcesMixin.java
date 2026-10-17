package net.myriantics.klaxon.mixin.minecraft.reloadable_registries.infra;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.flag.FeatureFlagSet;
import net.myriantics.klaxon.infra.reloadable_registry.KlaxonResourceReloadListener;
import net.myriantics.klaxon.infra.reloadable_registry.ReloadableRegistryTemplate;
import net.myriantics.klaxon.infra.reloadable_registry.ReloadableRegistryManager;
import net.myriantics.klaxon.registry.KlaxonBuiltInRegistries;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(ReloadableServerResources.class)
public abstract class ReloadableServerResourcesMixin implements ReloadableRegistryManager.Access {

    @Shadow
    @Final
    private ReloadableServerResources.ConfigurableRegistryLookup registryLookup;

    @Unique
    private ReloadableRegistryManager klaxon$reloadableRegistryManager = ReloadableRegistryManager.EMPTY;

    @Inject(
            method = "<init>",
            at = @At(value = "TAIL")
    )
    private void klaxon$initDynamicMachinePartDataLoader(RegistryAccess.Frozen registryAccess, FeatureFlagSet enabledFeatures, Commands.CommandSelection commandSelection, int functionCompilationLevel, CallbackInfo ci) {

        ImmutableMap.Builder<ResourceKey<ReloadableRegistryTemplate<?>>, KlaxonResourceReloadListener> builder = ImmutableMap.builder();

        KlaxonBuiltInRegistries.RELOADABLE_REGISTRY.holders().forEach(holder -> {
            builder.put(holder.key(), holder.value().create(this.registryLookup));
        });

        this.klaxon$reloadableRegistryManager = new ReloadableRegistryManager(builder.build());
    }

    @ModifyReturnValue(
            method = "listeners",
            at = @At(value = "RETURN")
    )
    private List<PreparableReloadListener> klaxon$injectListeners(List<PreparableReloadListener> original) {
        return ImmutableList.<PreparableReloadListener>builder().addAll(original).addAll(this.klaxon$reloadableRegistryManager.listenerIterator()).build();
    }

    public @Nullable ReloadableRegistryManager klaxon$getReloadableRegistryManager() {
        return this.klaxon$reloadableRegistryManager;
    }
}
