package net.myriantics.klaxon.block.functional.hallnox_pod;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import java.util.Optional;

public class DirectionalSaplingGenerator {
    private final String id;
    private final Optional<ResourceKey<ConfiguredFeature<?, ?>>> up;
    private final Optional<ResourceKey<ConfiguredFeature<?, ?>>> down;
    private final Optional<ResourceKey<ConfiguredFeature<?, ?>>> north;
    private final Optional<ResourceKey<ConfiguredFeature<?, ?>>> east;
    private final Optional<ResourceKey<ConfiguredFeature<?, ?>>> south;
    private final Optional<ResourceKey<ConfiguredFeature<?, ?>>> west;

    public DirectionalSaplingGenerator(String id, Optional<ResourceKey<ConfiguredFeature<?, ?>>> up, Optional<ResourceKey<ConfiguredFeature<?, ?>>> down, Optional<ResourceKey<ConfiguredFeature<?, ?>>> north, Optional<ResourceKey<ConfiguredFeature<?, ?>>> east, Optional<ResourceKey<ConfiguredFeature<?, ?>>> south, Optional<ResourceKey<ConfiguredFeature<?, ?>>> west) {
        this.id = id;
        this.up = up;
        this.down = down;
        this.north = north;
        this.east = east;
        this.south = south;
        this.west = west;
    }

    public boolean generate(Direction direction, ServerLevel world, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state, RandomSource random) {
        switch (direction) {
            case DOWN -> {
                if (down.isPresent()) return executeIfPresent(down.get(), world, chunkGenerator, pos, state, random);
            }
            case UP -> {
                if (up.isPresent()) return executeIfPresent(up.get(), world, chunkGenerator, pos, state, random);
            }
            case NORTH -> {
                if (north.isPresent()) return executeIfPresent(north.get(), world, chunkGenerator, pos, state, random);
            }
            case SOUTH -> {
                if (south.isPresent()) return executeIfPresent(south.get(), world, chunkGenerator, pos, state, random);
            }
            case WEST -> {
                if (west.isPresent()) return executeIfPresent(west.get(), world, chunkGenerator, pos, state, random);
            }
            case EAST -> {
                if (east.isPresent()) return executeIfPresent(east.get(), world, chunkGenerator, pos, state, random);
            }
        };

        return false;
    }

    private boolean executeIfPresent(ResourceKey<ConfiguredFeature<?, ?>> selectedFeatureKey, ServerLevel serverWorld, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state, RandomSource random) {
        Holder<ConfiguredFeature<?, ?>> selectedFeatureEntry = serverWorld
                .registryAccess()
                .registryOrThrow(Registries.CONFIGURED_FEATURE)
                .getHolder(selectedFeatureKey)
                .orElse(null);

        if (selectedFeatureEntry != null) {
            return selectedFeatureEntry.value().place(serverWorld, chunkGenerator, random, pos);
        }

        return false;
    }
}
