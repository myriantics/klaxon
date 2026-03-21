package net.myriantics.klaxon.registry.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.myriantics.klaxon.KlaxonCommon;

public abstract class KlaxonArmorTrimMaterials {
    public static final ResourceKey<TrimMaterial> STEEL = of("steel");

    private static ResourceKey<TrimMaterial> of(String id) {
        return ResourceKey.create(Registries.TRIM_MATERIAL, KlaxonCommon.locate(id));
    }
}
