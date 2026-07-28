package net.myriantics.klaxon.util.storage.item;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;

public abstract class KlaxonAdvancedContainerMenu extends KlaxonBaseContainerMenu {

    protected final ContainerData data;

    // client ctor
    protected <T extends KlaxonAdvancedContainerMenu> KlaxonAdvancedContainerMenu(MenuType<?> menuType, int containerId, Inventory playerInventory, KlaxonClientMenuInitializer<T> initializer) {
        this(
                menuType,
                containerId,
                playerInventory,
                initializer.createClientContainer(),
                initializer.createClientContainerData(),
                ContainerLevelAccess.NULL
        );

        //noinspection unchecked
        initializer.initialize((T) this);
    }

    // server ctor
    protected KlaxonAdvancedContainerMenu(MenuType<?> menuType, int containerId, Inventory playerInventory, Container container, ContainerData data, ContainerLevelAccess access) {
        super(menuType, containerId, playerInventory, container, access);
        // init data slots
        this.data = this.initContainerData(container);
        if (this.data != null) {
            this.addDataSlots(this.data);
        }
    }
}
