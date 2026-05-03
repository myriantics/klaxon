package net.myriantics.klaxon.block.machines.blast_processor;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractBlastProcessorMenu extends AbstractContainerMenu {
    protected AbstractBlastProcessorMenu(@Nullable MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }
}
