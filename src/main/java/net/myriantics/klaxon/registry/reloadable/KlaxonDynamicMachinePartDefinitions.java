package net.myriantics.klaxon.registry.reloadable;

import net.minecraft.resources.ResourceKey;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.mechanics.machinery.part.DynamicMachinePartDefinition;
import net.myriantics.klaxon.registry.KlaxonRegistries;

public abstract class KlaxonDynamicMachinePartDefinitions {
    public static final ResourceKey<DynamicMachinePartDefinition> MUFFLER = create("muffler");

    private static ResourceKey<DynamicMachinePartDefinition> create(String name) {
        return ResourceKey.create(KlaxonRegistries.DYNAMIC_MACHINE_PART_DEFINITION, KlaxonCommon.locate(name));
    }
}
