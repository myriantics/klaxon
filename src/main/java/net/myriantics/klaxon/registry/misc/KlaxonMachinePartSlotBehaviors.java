package net.myriantics.klaxon.registry.misc;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.mechanics.machinery.part.container.MachinePartSlotBehavior;
import net.myriantics.klaxon.registry.KlaxonBuiltInRegistries;
import net.myriantics.klaxon.registry.item.KlaxonDataComponentTypes;

public abstract class KlaxonMachinePartSlotBehaviors {

    public static final Holder<MachinePartSlotBehavior> MUFFLER = register("muffler", MachinePartSlotBehavior.strictBuilder()
            .require(KlaxonDataComponentTypes.MACHINE_MUFFLER_PART)
    );
    public static final Holder<MachinePartSlotBehavior> GENERATOR_TURBINE = register("generator_turbine", MachinePartSlotBehavior.strictBuilder()
            .require(KlaxonDataComponentTypes.GENERATOR_TURBINE_PART)
    );

    private static Holder<MachinePartSlotBehavior> register(String name, MachinePartSlotBehavior.Builder builder) {
        return register(name, builder.build());
    }

    private static Holder<MachinePartSlotBehavior> register(String name, MachinePartSlotBehavior behavior) {
        return Registry.registerForHolder(KlaxonBuiltInRegistries.MACHINE_PART_SLOT_BEHAVIOR, KlaxonCommon.locate(name), behavior);
    }

    public static void init() {
        KlaxonCommon.LOGGER.info("Registered KLAXON's Machine Part Slot Behaviors!");
    }
}
