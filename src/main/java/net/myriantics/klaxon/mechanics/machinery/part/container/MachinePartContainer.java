package net.myriantics.klaxon.mechanics.machinery.part.container;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.myriantics.klaxon.mechanics.machinery.part.data.StackingStrategy;
import org.jetbrains.annotations.Nullable;

public abstract class MachinePartContainer {

    private final MachinePartContainerTemplate template;
    private final MachinePartSlotInstance[] slots;

    private Level level;
    private int setupIndex = 0;

    MachinePartContainer(MachinePartContainerTemplate template, @Nullable Level level) {
        this.template = template;
        this.slots = new MachinePartSlotInstance[template.behaviors.length];
        this.level = level;
    }

    public void populateLevel(Level level) {
        this.level = level;
        for (MachinePartSlotInstance slot : this.slots) {
            slot.onLevelPopulated(level);
        }
    }

    public boolean isEmpty() {
        for (MachinePartSlotInstance slot : this.slots) {
            if (!slot.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public Tag save(HolderLookup.Provider registries) {
        ListTag listTag = new ListTag();

        for (MachinePartSlotInstance machinePartSlotInstance : this.slots) {
            if (!machinePartSlotInstance.isEmpty()) {
                listTag.add(machinePartSlotInstance.save(new CompoundTag(), registries));
            }
        }

        return listTag;
    }

    public void load(ListTag listTag, HolderLookup.Provider provider, @Nullable Level level) {
        for (int i = 0; i < this.slots.length; i++) {
            CompoundTag tag = listTag.getCompound(i);
            this.slots[i].load(tag, provider, level);
        }
    }

    public MachinePartSlotInstance add(Holder<MachinePartSlotBehavior> holder, StackingStrategy strategy) {
        return this.add(holder.value(), strategy);
    }

    public MachinePartSlotInstance add(MachinePartSlotBehavior behavior, StackingStrategy strategy) {
        if (this.setupIndex > this.slots.length) {
            throw new ArrayIndexOutOfBoundsException("Attempted to overfill machine part container");
        }

        MachinePartSlotBehavior expectedBehavior = this.template.behaviors[this.setupIndex];
        if (expectedBehavior != behavior) {
            throw new IllegalArgumentException("Slot Behavior registered improperly or in the wrong order - expected " + expectedBehavior + ", got " + behavior);
        }

        return this.slots[this.setupIndex++] = new MachinePartSlotInstance(behavior, this, strategy);
    }

    public void validate() {
        if (this.setupIndex != this.slots.length) {
            throw new IllegalStateException("Machine Part Container was initialized with " + (this.setupIndex + 1) + ", expected " + (this.slots.length + 1));
        }
    }

    public Level level() {
        return this.level;
    }

    public abstract void setChanged();
}
