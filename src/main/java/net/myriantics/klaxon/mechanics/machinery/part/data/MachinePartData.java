package net.myriantics.klaxon.mechanics.machinery.part.data;

import org.jetbrains.annotations.NotNull;

public interface MachinePartData extends Comparable<MachinePartData> {
    MachinePartData add(MachinePartData otherData) throws IllegalArgumentException;

    @Override
    int compareTo(@NotNull MachinePartData other);
}
