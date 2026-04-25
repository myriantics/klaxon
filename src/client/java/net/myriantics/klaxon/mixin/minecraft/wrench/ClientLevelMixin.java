package net.myriantics.klaxon.mixin.minecraft.wrench;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import net.myriantics.klaxon.mechanics.wrench.WrenchInteractionOverlayManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin extends Level implements WrenchInteractionOverlayManager.Access {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Unique
    private final WrenchInteractionOverlayManager klaxon$wrenchInteractionOverlayManager = new WrenchInteractionOverlayManager();

    protected ClientLevelMixin(WritableLevelData levelData, ResourceKey<Level> dimension, RegistryAccess registryAccess, Holder<DimensionType> dimensionTypeRegistration, Supplier<ProfilerFiller> profiler, boolean isClientSide, boolean isDebug, long biomeZoomSeed, int maxChainedNeighborUpdates) {
        super(levelData, dimension, registryAccess, dimensionTypeRegistration, profiler, isClientSide, isDebug, biomeZoomSeed, maxChainedNeighborUpdates);
    }

    @Inject(
            method = "tick",
            at = @At(value = "TAIL")
    )
    private void klaxon$tickWrenchInteractionOverlayManager(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        this.getProfiler().push("klaxon:wrench_interaction_overlay_manager");
        this.klaxon$wrenchInteractionOverlayManager.tick();
        this.getProfiler().pop();
    }

    @Override
    public WrenchInteractionOverlayManager klaxon$get() {
        return this.klaxon$wrenchInteractionOverlayManager;
    }
}
