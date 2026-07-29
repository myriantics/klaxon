package net.myriantics.klaxon.registry.worldgen;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.myriantics.klaxon.KlaxonCommon;

public abstract class KlaxonStructureTypes {

    private static Holder<StructureType<?>> register(String name, MapCodec<Structure> codec) {
        return Registry.registerForHolder(BuiltInRegistries.STRUCTURE_TYPE, KlaxonCommon.locate(name), () -> codec);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Initialized KLAXON's Structure Types!");
    }
}
