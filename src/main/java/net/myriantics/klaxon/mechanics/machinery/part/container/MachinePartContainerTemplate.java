package net.myriantics.klaxon.mechanics.machinery.part.container;

import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class MachinePartContainerTemplate {

    public final MachinePartSlotBehavior[] behaviors;

    private MachinePartContainerTemplate(MachinePartSlotBehavior... behaviors) {
        this.behaviors = behaviors;
    }

    public static MachinePartContainerTemplate of(MachinePartSlotBehavior... behaviors) {
        return new MachinePartContainerTemplate(behaviors);
    }

    public MachinePartContainer create(Runnable changedCallback) {
        return this.create(null, changedCallback);
    }

    public MachinePartContainer create(@Nullable Level level, Runnable changedCallback) {
        return new MachinePartContainer(this, level) {
            @Override
            public void setChanged() {
                changedCallback.run();
            }
        };
    }
}
