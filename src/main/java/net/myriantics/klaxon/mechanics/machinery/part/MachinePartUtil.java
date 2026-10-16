package net.myriantics.klaxon.mechanics.machinery.part;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.myriantics.klaxon.mechanics.machinery.part.data.StackingStrategy;
import net.myriantics.klaxon.mechanics.machinery.part.patch.MachinePartDataComponentPatchBuilder;
import net.myriantics.klaxon.registry.KlaxonRegistries;
import net.myriantics.klaxon.registry.KlaxonReloadableRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class MachinePartUtil {

    public static DataComponentPatch stackForCount(DataComponentPatch singleItemStackedPartComponents, int count, StackingStrategy strategy) {
        if (count < 1) {
            return DataComponentPatch.EMPTY;
        }

        if (strategy != StackingStrategy.ADD) {
            return singleItemStackedPartComponents;
        }


        MachinePartDataComponentPatchBuilder builder = MachinePartDataComponentPatchBuilder.create();
        for (int i = 0; i < count; i++) {
            builder.stack(singleItemStackedPartComponents, StackingStrategy.ADD);
        }

        return builder.build();
    }

    public static DataComponentPatch getStackedPartComponents(ItemStack stack, Level level, StackingStrategy strategy) {
        return stack(
                stack.getComponentsPatch(),
                Objects.requireNonNullElse(getDynamicPartComponents(stack, level), DataComponentPatch.EMPTY),
                strategy
        );
    }

    public static DataComponentPatch stack(@NotNull DataComponentPatch patchA, @NotNull DataComponentPatch patchB, StackingStrategy strategy) {
        Objects.requireNonNull(patchA);
        Objects.requireNonNull(patchB);

        if (patchA.isEmpty()) {
            return patchB;
        }
        if (patchB.isEmpty()) {
            return patchA;
        }

        return MachinePartDataComponentPatchBuilder.create()
                .stack(patchA, strategy)
                .stack(patchB, strategy)
                .build();
    }

    public static @Nullable DataComponentPatch getDynamicPartComponents(ItemStack stack, Level level) {
        return getDynamicPartComponents(stack.getItem(), level);
    }

    public static @Nullable DataComponentPatch getDynamicPartComponents(Item item, Level level) {
        if (level instanceof KlaxonReloadableRegistries.Access access) {
            return access.klaxon$getRegistry(KlaxonReloadableRegistries.DYNAMIC_MACHINE_PART_DEFINITION).getDynamicPartComponentsForItem(item);
        } else {
            return null;
        }
    }
}
