package net.myriantics.klaxon.mechanics.machinery.part.patch;

import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.util.Unit;
import net.myriantics.klaxon.mechanics.machinery.part.data.MachinePartData;
import net.myriantics.klaxon.mechanics.machinery.part.data.StackingStrategy;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public final class MachinePartDataComponentPatchBuilder {
    private final Reference2ObjectMap<DataComponentType<?>, Object> map = new Reference2ObjectArrayMap<>();

    private MachinePartDataComponentPatchBuilder() {
    }

    public static MachinePartDataComponentPatchBuilder create() {
        return new MachinePartDataComponentPatchBuilder();
    }

    public MachinePartDataComponentPatchBuilder addUnit(DataComponentType<Unit> type) {
        return this.add(type, Unit.INSTANCE, StackingStrategy.USE_FIRST);
    }

    public <T> MachinePartDataComponentPatchBuilder add(DataComponentType<T> type, T value) {
        return this.add(type, value, StackingStrategy.ADD);
    }

    @SuppressWarnings("unchecked")
    public <T> MachinePartDataComponentPatchBuilder add(DataComponentType<T> type, T value, StackingStrategy strategy) {
        @Nullable T original = (T) this.map.get(type);
        if (original == null) {
            this.map.put(type, value);
        } else {
            switch (strategy) {
                case ADD -> {
                    if (value instanceof MachinePartData data) {
                        this.map.put(type, data.add((MachinePartData) original));
                    }
                }
                case USE_STRONGEST -> {
                    if (value instanceof MachinePartData data) {
                        this.map.put(type, data.compareTo((MachinePartData) (original)) > 0 ? data : original);
                    }
                }
                case USE_FIRST -> {
                    // no-op because we alr have a value
                }
            }
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    public DataComponentPatch build() {
        DataComponentPatch.Builder builder = DataComponentPatch.builder();
        for (Map.Entry<DataComponentType<?>, Object> entry : this.map.entrySet()) {
            builder.set((DataComponentType<? super Object>) entry.getKey(), entry.getValue());
        }
        return builder.build();
    }
}
