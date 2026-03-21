package net.myriantics.klaxon.tag.klaxon;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.myriantics.klaxon.KlaxonCommon;

public abstract class KlaxonFluidTags {

    public static final TagKey<Fluid> COLD_FLUIDS =
            createTag("cold_fluids");

    private static TagKey<Fluid> createTag(String name) {
        return TagKey.create(Registries.FLUID, KlaxonCommon.locate(name));
    }
}
