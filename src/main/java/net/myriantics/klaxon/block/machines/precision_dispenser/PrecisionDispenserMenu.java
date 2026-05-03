package net.myriantics.klaxon.block.machines.precision_dispenser;

import com.google.common.base.Supplier;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.myriantics.klaxon.mechanics.muffling.MufflerSlot;
import net.myriantics.klaxon.registry.misc.KlaxonMenuTypes;
import net.myriantics.klaxon.tag.klaxon.KlaxonItemTags;
import net.myriantics.klaxon.util.container.KlaxonBaseContainerMenu;

public class PrecisionDispenserMenu extends KlaxonBaseContainerMenu {

    private final int mufflerIndex = 9;

    protected PrecisionDispenserMenu(MenuType<?> menuType, int containerId, Inventory playerInventory, Supplier<Container> supplier) {
        super(menuType, containerId, playerInventory, supplier);
    }

    public PrecisionDispenserMenu(int containerId, Inventory playerInventory) {
        this(KlaxonMenuTypes.PRECISION_DISPENSER.value(), containerId, playerInventory, () -> new SimpleContainer(10));
    }

    protected PrecisionDispenserMenu(MenuType<?> menuType, int containerId, Inventory playerInventory, Container container, ContainerLevelAccess access) {
        super(menuType, containerId, playerInventory, container, access);
    }

    public PrecisionDispenserMenu(int containerId, Inventory playerInventory, Container container, ContainerLevelAccess access) {
        this(KlaxonMenuTypes.PRECISION_DISPENSER.value(), containerId, playerInventory, container, access);
    }

    @Override
    protected void initSlots(Inventory inventory, Container container) {

        // dispenser slots
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.addSlot(new Slot(container, j + i * 3, 62 + j * 18, 17 + i * 18));
            }
        }

        // muffler - use muffler storage on server and dummy on client
        this.addSlot(new MufflerSlot(
                inventory.player,
                this.access,
                container instanceof PrecisionDispenserBlockEntity dispenser ? dispenser.mufflerStorage : container,
                9,
                0,
                0
        ));

        // player inv
        super.initSlots(inventory, container);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack clickedStackCopy = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack clickedStack = slot.getItem();

            // muffler handling first
            if (index > 10 && clickedStack.is(KlaxonItemTags.MUFFLERS)) {
                Slot mufflerSlot = this.getSlot(this.mufflerIndex);
                if (mufflerSlot.getItem().isEmpty()) {
                    mufflerSlot.set(clickedStack.split(mufflerSlot.getMaxStackSize()));
                }
            }

            clickedStackCopy = clickedStack.copy();
            if (index < 10) {
                if (!this.moveItemStackTo(clickedStack, 10, 45, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(clickedStack, 0, 10, false)) {
                return ItemStack.EMPTY;
            }

            if (clickedStack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (clickedStack.getCount() == clickedStackCopy.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, clickedStack);
        }

        return clickedStackCopy;
    }
}
