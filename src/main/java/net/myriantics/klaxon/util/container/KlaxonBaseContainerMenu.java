package net.myriantics.klaxon.util.container;

import com.google.common.base.Supplier;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import org.jetbrains.annotations.Nullable;

public abstract class KlaxonBaseContainerMenu extends AbstractContainerMenu {

    protected final ContainerLevelAccess access;
    protected final Container container;

    // client ctor
    protected KlaxonBaseContainerMenu(MenuType<?> menuType, int containerId, Inventory playerInventory, Supplier<Container> supplier) {
        this(
                menuType,
                containerId,
                playerInventory,
                supplier.get(),
                ContainerLevelAccess.NULL
        );
    }

    // server ctor
    protected KlaxonBaseContainerMenu(MenuType<?> menuType, int containerId, Inventory playerInventory, Container container, ContainerLevelAccess access) {
        super(menuType, containerId);
        this.access = access;
        this.container = container;
        this.initSlots(playerInventory, container);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    protected ContainerData initContainerData(Container container) {
        return null;
    }

    protected void initSlots(Inventory inventory, Container container) {

        // main inventory
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        // hotbar
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(inventory, i, 8 + i * 18, 142));
        }
    }
}
