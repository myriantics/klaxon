package net.myriantics.klaxon.mixin.minecraft.grapple_winch.connection;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WritableLevelData;
import net.myriantics.klaxon.mechanics.grapple_winch.manager.ServerGrappleWinchConnectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level implements ServerGrappleWinchConnectionManager.Access {

    @Shadow
    public abstract DimensionDataStorage getDataStorage();

    @Unique
    private ServerGrappleWinchConnectionManager klaxon$connectionManager;

    protected ServerLevelMixin(WritableLevelData properties, ResourceKey<Level> registryRef, RegistryAccess registryManager, Holder<DimensionType> dimensionEntry, Supplier<ProfilerFiller> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
    }

    @Inject(
            method = "<init>",
            at = @At(value = "TAIL")
    )
    private void klaxon$initGrappleWinchConnectionManager(MinecraftServer server, Executor workerExecutor, LevelStorageSource.LevelStorageAccess session, ServerLevelData properties, ResourceKey<Level> worldKey, LevelStem dimensionOptions, ChunkProgressListener worldGenerationProgressListener, boolean debugWorld, long seed, List<CustomSpawner> spawners, boolean shouldTickTime, RandomSequences randomSequencesState, CallbackInfo ci) {
        this.klaxon$connectionManager = this.getDataStorage().computeIfAbsent(
                ServerGrappleWinchConnectionManager.getPersistentStateType((ServerLevel) (Object) this),
                ServerGrappleWinchConnectionManager.nameFor(worldKey)
        );
    }

    @Inject(
            method = "tick",
            at = @At(value = "TAIL")
    )
    private void klaxon$tickGrappleWinchConnectionManager(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        this.getProfiler().push("server_grapple_winch_connection_manager");
        this.klaxon$connectionManager.tick();
        this.getProfiler().pop();
    }

    @Override
    public ServerGrappleWinchConnectionManager klaxon$getGrappleWinchConnectionManager() {
        return this.klaxon$connectionManager;
    }
}
