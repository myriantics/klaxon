package net.myriantics.klaxon.mechanics.machinery.part.slot;

import net.minecraft.core.component.DataComponentPatch;

public interface MachinePartSlotDefinition {

    boolean validateUpgrades(DataComponentPatch patch);

    static MachinePartSlotDefinitionImpl.Builder lenientBuilder() {
        return new MachinePartSlotDefinitionImpl.Builder(MachinePartSlotDefinitionImpl.CompatibilityMode.LENIENT);
    }

    static MachinePartSlotDefinitionImpl.Builder strictBuilder() {
        return new MachinePartSlotDefinitionImpl.Builder(MachinePartSlotDefinitionImpl.CompatibilityMode.STRICT);
    }
}
