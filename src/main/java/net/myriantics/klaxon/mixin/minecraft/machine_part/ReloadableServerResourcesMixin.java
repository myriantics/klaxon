package net.myriantics.klaxon.mixin.minecraft.machine_part;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.commands.Commands;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.flag.FeatureFlagSet;
import net.myriantics.klaxon.mechanics.machinery.part.KlaxonDynamicMachinePartLoader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ReloadableServerResources.class)
public abstract class ReloadableServerResourcesMixin implements KlaxonDynamicMachinePartLoader.Access {
    @Shadow
    @Final
    private ReloadableServerResources.ConfigurableRegistryLookup registryLookup;
    @Unique
    private KlaxonDynamicMachinePartLoader klaxon$dynamicMachinePartDataLoader;

    @Inject(
            method = "<init>",
            at = @At(value = "TAIL")
    )
    private void klaxon$initDynamicMachinePartDataLoader(RegistryAccess.Frozen registryAccess, FeatureFlagSet enabledFeatures, Commands.CommandSelection commandSelection, int functionCompilationLevel, CallbackInfo ci) {
        this.klaxon$dynamicMachinePartDataLoader = new KlaxonDynamicMachinePartLoader(this.registryLookup);
    }

    @ModifyReturnValue(
            method = "listeners",
            at = @At(value = "RETURN")
    )
    private List<PreparableReloadListener> klaxon$injectListeners(List<PreparableReloadListener> original) {
        return ImmutableList.<PreparableReloadListener>builder().addAll(original).add(this.klaxon$dynamicMachinePartDataLoader).build();
    }

    @Override
    public KlaxonDynamicMachinePartLoader klaxon$getDynamicMachinePartLoader() {
        return this.klaxon$dynamicMachinePartDataLoader;
    }
}
