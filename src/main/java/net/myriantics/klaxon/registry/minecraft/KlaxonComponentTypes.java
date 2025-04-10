package net.myriantics.klaxon.registry.minecraft;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.myriantics.klaxon.KlaxonCommon;

import java.util.function.UnaryOperator;

public class KlaxonComponentTypes {

    public static final ComponentType<Boolean> UNENCHANTABLE = register("unenchantable",
            builder -> builder.codec(Codec.BOOL).packetCodec(PacketCodecs.BOOL));

    private static <T> ComponentType<T> register(String name, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, KlaxonCommon.locate(name), builderOperator.apply(ComponentType.builder()).build());
    }

    public static void registerKlaxonComponents() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Data Components!");
    }
}
