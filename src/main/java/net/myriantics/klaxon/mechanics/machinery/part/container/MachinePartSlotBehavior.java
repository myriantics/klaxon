package net.myriantics.klaxon.mechanics.machinery.part.container;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.myriantics.klaxon.registry.KlaxonBuiltInRegistries;

import java.util.ArrayList;
import java.util.Map;

public final class MachinePartSlotBehavior {

    private final Holder<MachinePartSlotBehavior> holder = KlaxonBuiltInRegistries.MACHINE_PART_SLOT_BEHAVIOR.createIntrusiveHolder(this);
    private final HolderSet<DataComponentType<?>> allowedComponents;
    private final CompatibilityMode compatibilityMode;
    private final int maxCount;

    private MachinePartSlotBehavior(HolderSet<DataComponentType<?>> allowedComponents, CompatibilityMode compatibilityMode, int maxCount) {
        this.allowedComponents = allowedComponents;
        this.compatibilityMode = compatibilityMode;
        this.maxCount = maxCount;
    }

    public Holder<MachinePartSlotBehavior> asHolder() {
        return this.holder;
    }

    public boolean validateUpgrades(DataComponentPatch patch) {
        return this.compatibilityMode.allowUpgradeInsertion(patch, this);
    }

    public int getMaxCount() {
        return this.maxCount;
    }

    public static MachinePartSlotBehavior.Builder lenientBuilder() {
        return new MachinePartSlotBehavior.Builder(MachinePartSlotBehavior.CompatibilityMode.LENIENT);
    }

    public static MachinePartSlotBehavior.Builder strictBuilder() {
        return new MachinePartSlotBehavior.Builder(MachinePartSlotBehavior.CompatibilityMode.STRICT);
    }

    public static final class Builder {

        private ArrayList<Holder<DataComponentType<?>>> validUpgrades = new ArrayList<>();
        private final CompatibilityMode compatibilityMode;
        private int maxStackSize = 1;

        Builder(CompatibilityMode compatibilityMode) {
            this.compatibilityMode = compatibilityMode;
        }

        @SuppressWarnings("unchecked")
        public <T> Builder require(Holder<DataComponentType<T>> type) {
            this.validUpgrades.add((Holder<DataComponentType<?>>) (Object) type);
            return this;
        }

        public Builder require(DataComponentType<?> type) {
            this.validUpgrades.add(BuiltInRegistries.DATA_COMPONENT_TYPE.wrapAsHolder(type));
            return this;
        }

        public Builder maxStackSize(int maxStackSize) {
            this.maxStackSize = maxStackSize;
            return this;
        }

        public MachinePartSlotBehavior build() {
            return new MachinePartSlotBehavior(
                    HolderSet.direct(this.validUpgrades),
                    this.compatibilityMode,
                    this.maxStackSize
            );
        }
    }

    enum CompatibilityMode {
        LENIENT {
            @Override
            protected boolean allowUpgradeInsertion(DataComponentPatch upgradeComponents, MachinePartSlotBehavior definition) {
                return true;
            }
        },
        STRICT {
            @Override
            protected boolean allowUpgradeInsertion(DataComponentPatch upgradeComponents, MachinePartSlotBehavior definition) {
                return definition.allowedComponents.stream().anyMatch(holder -> upgradeComponents.entrySet().stream().map(Map.Entry::getKey).anyMatch(type -> holder == type));
            }
        };

        protected abstract boolean allowUpgradeInsertion(DataComponentPatch upgradeComponents, MachinePartSlotBehavior definition);
    }
}
