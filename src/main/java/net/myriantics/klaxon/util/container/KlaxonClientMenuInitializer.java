package net.myriantics.klaxon.util.container;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import org.jetbrains.annotations.Nullable;

public interface KlaxonClientMenuInitializer<T extends KlaxonBaseContainerMenu> {
    int getContainerSize();

    default int getContainerDataSize() {
        return 0;
    }

    default Container createClientContainer() {
        return new SimpleContainer(this.getContainerSize());
    }

    @Nullable default ContainerData createClientContainerData() {
        int size = this.getContainerDataSize();
        return size > 0 ? new SimpleContainerData(size) : null;
    }

    void initialize(T menu);
}
