package net.myriantics.klaxon.util.container;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;

public abstract class KlaxonAdvancedContainerMenu extends KlaxonBaseContainerMenu {

    protected final ContainerData data;

    // client ctor
    protected KlaxonAdvancedContainerMenu(MenuType<?> menuType, int containerId, Inventory playerInventory, KlaxonClientMenuInitializer<KlaxonBaseContainerMenu> initializer) {
        this(
                menuType,
                containerId,
                playerInventory,
                initializer.createClientContainer(),
                initializer.createClientContainerData(),
                ContainerLevelAccess.NULL
        );
        initializer.initialize(this);
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
