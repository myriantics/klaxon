package net.myriantics.klaxon.mixin.minecraft.grapple_winch.connection;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.myriantics.klaxon.mechanics.grapple_winch.manager.ClientGrappleWinchConnectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin extends World implements ClientGrappleWinchConnectionManager.Access {

    @Unique
    private final ClientGrappleWinchConnectionManager klaxon$grappleWinchConnectionManager = new ClientGrappleWinchConnectionManager((ClientWorld) (Object) this);

    protected ClientWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {
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
    public ClientGrappleWinchConnectionManager klaxon$get() {
        return this.klaxon$grappleWinchConnectionManager;
    }
}
