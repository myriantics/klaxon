package net.myriantics.klaxon.mechanics.machinery.part;

import net.minecraft.world.item.ItemStack;
import net.myriantics.klaxon.mechanics.machinery.part.slot.MachinePartSlotDefinition;
import net.myriantics.klaxon.mechanics.machinery.part.slot.MachinePartSlotDefinitionImpl;

import java.util.ArrayList;
import java.util.function.UnaryOperator;

public final class MachinePartContainer {

    private final MachinePartSlotDefinition[] definitions;
    private ItemStack[] stacks;

    private MachinePartContainer(MachinePartSlotDefinition[] definitions) {
        this.definitions = definitions;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        ArrayList<MachinePartSlotDefinition> definitions = new ArrayList<>();

        private Builder() {

        }

        public MachinePartSlotDefinition lenientSlot(UnaryOperator<MachinePartSlotDefinitionImpl.Builder> operator) {
            MachinePartSlotDefinition slotDefinition = operator.apply(MachinePartSlotDefinition.lenientBuilder()).build();
            this.definitions.add(slotDefinition);
            return slotDefinition;
        }

        public MachinePartSlotDefinition strictSlot(UnaryOperator<MachinePartSlotDefinitionImpl.Builder> operator) {
            MachinePartSlotDefinition slotDefinition = operator.apply(MachinePartSlotDefinition.strictBuilder()).build();
            this.definitions.add(slotDefinition);
            return slotDefinition;
        }

        public MachinePartContainer build() {
            return new MachinePartContainer(this.definitions.toArray(new MachinePartSlotDefinition[this.definitions.size()]));
        }
    }
}
