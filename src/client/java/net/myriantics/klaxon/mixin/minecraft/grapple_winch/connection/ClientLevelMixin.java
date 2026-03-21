package net.myriantics.klaxon.mixin.minecraft.grapple_winch.connection;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import net.myriantics.klaxon.mechanics.grapple_winch.ClientGrappleWinchConnectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin extends Level implements ClientGrappleWinchConnectionManager.Access {

    @Unique
    private final ClientGrappleWinchConnectionManager klaxon$grappleWinchConnectionManager = new ClientGrappleWinchConnectionManager((ClientLevel) (Object) this);

    protected ClientLevelMixin(WritableLevelData properties, ResourceKey<Level> registryRef, RegistryAccess registryManager, Holder<DimensionType> dimensionEntry, Supplier<ProfilerFiller> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
    }

    @Inject(
            method = "tick",
            at = @At(value = "TAIL")
    )
    private void klaxon$tickGrappleWinchConnectionManager(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        this.getProfiler().push("client_grapple_winch_connection_manager");
        this.klaxon$grappleWinchConnectionManager.tick();
        this.getProfiler().pop();
    }

    @Override
    public ClientGrappleWinchConnectionManager klaxon$getGrappleWinchConnectionManager() {
        return this.klaxon$grappleWinchConnectionManager;
    }
}
