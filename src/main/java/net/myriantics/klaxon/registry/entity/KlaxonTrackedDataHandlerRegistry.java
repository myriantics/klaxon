package net.myriantics.klaxon.registry.entity;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;

import java.util.List;

public abstract class KlaxonTrackedDataHandlerRegistry {
    public static final EntityDataSerializer<List<Integer>> INT_LIST = register(EntityDataSerializer.forValueType(ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list())));

    private static <T> EntityDataSerializer<T> register(EntityDataSerializer<T> handler) {
        EntityDataSerializers.registerSerializer(handler);
        return handler;
    }
}
