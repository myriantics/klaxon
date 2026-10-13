package net.myriantics.klaxon.mechanics.machinery.part.slot;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.ArrayList;
import java.util.Map;

public class MachinePartSlotDefinitionImpl implements MachinePartSlotDefinition {

    private final HolderSet<DataComponentType<?>> allowedComponents;
    private final CompatibilityMode compatibilityMode;

    public MachinePartSlotDefinitionImpl(HolderSet<DataComponentType<?>> allowedComponents, CompatibilityMode compatibilityMode) {
        this.allowedComponents = allowedComponents;
        this.compatibilityMode = compatibilityMode;
    }

    @Override
    public boolean validateUpgrades(DataComponentPatch patch) {
        return this.compatibilityMode.allowUpgradeInsertion(patch, this);
    }

    public static final class Builder {

        private ArrayList<Holder<DataComponentType<?>>> validUpgrades = new ArrayList<>();
        private final CompatibilityMode compatibilityMode;

        Builder(CompatibilityMode compatibilityMode) {
            this.compatibilityMode = compatibilityMode;
        }

        public Builder add(Holder<DataComponentType<?>> type) {
            this.validUpgrades.add(type);
            return this;
        }

        public Builder add(DataComponentType<?> type) {
            this.validUpgrades.add(BuiltInRegistries.DATA_COMPONENT_TYPE.wrapAsHolder(type));
            return this;
        }

        public MachinePartSlotDefinition build() {
            return new MachinePartSlotDefinitionImpl(
                    HolderSet.direct(this.validUpgrades),
                    this.compatibilityMode
            );
        }
    }

    enum CompatibilityMode {
        LENIENT {
            @Override
            protected boolean allowUpgradeInsertion(DataComponentPatch upgradeComponents, MachinePartSlotDefinitionImpl definition) {
                return true;
            }
        },
        STRICT {
            @Override
            protected boolean allowUpgradeInsertion(DataComponentPatch upgradeComponents, MachinePartSlotDefinitionImpl definition) {
                return definition.allowedComponents.stream().anyMatch(holder -> upgradeComponents.entrySet().stream().map(Map.Entry::getKey).anyMatch(type -> holder == type));
            }
        };

        protected abstract boolean allowUpgradeInsertion(DataComponentPatch upgradeComponents, MachinePartSlotDefinitionImpl definition);
    }
}
