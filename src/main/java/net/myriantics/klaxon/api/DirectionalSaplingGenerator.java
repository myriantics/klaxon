package net.myriantics.klaxon.api;

import net.minecraft.block.BlockState;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;

import java.util.Optional;

public class DirectionalSaplingGenerator {
    private final String id;
    private final Optional<RegistryKey<ConfiguredFeature<?, ?>>> up;
    private final Optional<RegistryKey<ConfiguredFeature<?, ?>>> down;
    private final Optional<RegistryKey<ConfiguredFeature<?, ?>>> north;
    private final Optional<RegistryKey<ConfiguredFeature<?, ?>>> east;
    private final Optional<RegistryKey<ConfiguredFeature<?, ?>>> south;
    private final Optional<RegistryKey<ConfiguredFeature<?, ?>>> west;

    public DirectionalSaplingGenerator(String id, Optional<RegistryKey<ConfiguredFeature<?, ?>>> up, Optional<RegistryKey<ConfiguredFeature<?, ?>>> down, Optional<RegistryKey<ConfiguredFeature<?, ?>>> north, Optional<RegistryKey<ConfiguredFeature<?, ?>>> east, Optional<RegistryKey<ConfiguredFeature<?, ?>>> south, Optional<RegistryKey<ConfiguredFeature<?, ?>>> west) {
        this.id = id;
        this.up = up;
        this.down = down;
        this.north = north;
        this.east = east;
        this.south = south;
        this.west = west;
    }

    public boolean generate(Direction direction, ServerWorld world, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state, Random random) {
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

    private boolean executeIfPresent(RegistryKey<ConfiguredFeature<?, ?>> selectedFeatureKey, ServerWorld serverWorld, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state, Random random) {
        RegistryEntry<ConfiguredFeature<?, ?>> selectedFeatureEntry = serverWorld
                .getRegistryManager()
                .get(RegistryKeys.CONFIGURED_FEATURE)
                .getEntry(selectedFeatureKey)
                .orElse(null);

        if (selectedFeatureEntry != null) {
            return selectedFeatureEntry.value().generate(serverWorld, chunkGenerator, random, pos);
        }

        return false;
    }
}
