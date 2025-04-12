package net.myriantics.klaxon.tag.klaxon;

import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.myriantics.klaxon.KlaxonCommon;

public class KlaxonFluidTags {

    public static final TagKey<Fluid> COLD_FLUIDS =
            createTag("cold_fluids");

    private static TagKey<Fluid> createTag(String name) {
        return TagKey.of(RegistryKeys.FLUID, KlaxonCommon.locate(name));
    }
}
