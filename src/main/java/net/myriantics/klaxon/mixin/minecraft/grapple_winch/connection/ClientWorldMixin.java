package net.myriantics.klaxon.mixin.minecraft.grapple_winch.connection;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.myriantics.klaxon.mechanics.grapple_winch.GrappleWinchConnectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.Supplier;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin extends World implements GrappleWinchConnectionManager.ClientAccess {

    @Unique
    private final GrappleWinchConnectionManager.Client klaxon$grappleWinchConnectionManager = new GrappleWinchConnectionManager.Client((ClientWorld) (Object) this);

    protected ClientWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
    }

    @Override
    public GrappleWinchConnectionManager.Client klaxon$get() {
        return this.klaxon$grappleWinchConnectionManager;
    }
}
