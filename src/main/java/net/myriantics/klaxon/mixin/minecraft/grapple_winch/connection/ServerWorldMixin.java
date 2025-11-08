package net.myriantics.klaxon.mixin.minecraft.grapple_winch.connection;

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.RandomSequencesState;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.spawner.SpecialSpawner;
import net.myriantics.klaxon.mechanics.grapple_winch.GrappleWinchConnectionManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World implements GrappleWinchConnectionManager.ServerAccess {

    @Shadow
    public abstract PersistentStateManager getPersistentStateManager();

    @Unique
    private GrappleWinchConnectionManager.Server klaxon$connectionManager;

    protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
    }

    @Inject(
            method = "<init>",
            at = @At(value = "TAIL")
    )
    private void klaxon$initGrappleWinchConnectionManager(MinecraftServer server, Executor workerExecutor, LevelStorage.Session session, ServerWorldProperties properties, RegistryKey worldKey, DimensionOptions dimensionOptions, WorldGenerationProgressListener worldGenerationProgressListener, boolean debugWorld, long seed, List spawners, boolean shouldTickTime, RandomSequencesState randomSequencesState, CallbackInfo ci) {
        this.klaxon$connectionManager = this.getPersistentStateManager().getOrCreate(GrappleWinchConnectionManager.Server.getPersistentStateType((ServerWorld) (Object) this), GrappleWinchConnectionManager.nameFor(this.getDimensionEntry()));
    }

    @Override
    public GrappleWinchConnectionManager.Server klaxon$get() {
        return this.klaxon$connectionManager;
    }
}
