package net.myriantics.klaxon.registry.entity;

import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.network.codec.PacketCodecs;

import java.util.List;

public abstract class KlaxonTrackedDataHandlerRegistry {
    public static final TrackedDataHandler<List<Integer>> INT_LIST = register(TrackedDataHandler.create(PacketCodecs.VAR_INT.collect(PacketCodecs.toList())));

    private static <T> TrackedDataHandler<T> register(TrackedDataHandler<T> handler) {
        TrackedDataHandlerRegistry.register(handler);
        return handler;
    }
}
