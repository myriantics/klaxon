package net.myriantics.klaxon.block.machines.energy.appliances.industrial_shredder;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.myriantics.klaxon.registry.misc.KlaxonMenuTypes;
import net.myriantics.klaxon.util.storage.item.KlaxonAdvancedContainerMenu;
import net.myriantics.klaxon.util.storage.item.KlaxonClientMenuInitializer;

public class IndustrialShredderMenu extends KlaxonAdvancedContainerMenu implements ContainerListener {

    protected Slot inputSlot;
    protected Slot[] outputSlots;

    protected Container outputInventory;

    private static final KlaxonClientMenuInitializer<IndustrialShredderMenu> CLIENT_INITIALIZER = new KlaxonClientMenuInitializer<>() {
        @Override
        public int getContainerSize() {
            return IndustrialShredderTopBlockEntity.SHREDDING_INPUT_PARTITION_SIZE + IndustrialShredderBottomBlockEntity.OUTPUT_STORAGE_CONTAINER_SIZE;
        }

        @Override
        public int getContainerDataSize() {
            return IndustrialShredderTopBlockEntity.CONTAINER_DATA_ACCESS_SIZE;
        }

        @Override
        public void initialize(IndustrialShredderMenu menu) {
        }
    };

    public IndustrialShredderMenu(int containerId, Inventory playerInventory) {
        super(KlaxonMenuTypes.INDUSTRIAL_SHREDDER.value(), containerId, playerInventory, CLIENT_INITIALIZER);
    }

    public IndustrialShredderMenu(int containerId, Inventory playerInventory, Container container, ContainerData dataAccess, ContainerLevelAccess access) {
        super(KlaxonMenuTypes.INDUSTRIAL_SHREDDER.value(), containerId, playerInventory, container, dataAccess, access);
        this.addSlotListener(this);
    }

    @Override
    protected void initSlots(Inventory inventory, Container container) {
        // input
        this.inputSlot = this.addSlot(new Slot(this.container, 0, 11, 17));

        // output storage slots
        this.outputSlots = new Slot[9];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int selected = i + j * 3;
                this.outputSlots[selected] = this.addSlot(new Slot(this.container, selected + 1, 107 + i * 18, 17 + j * 18));
            }
        }

        super.initSlots(inventory, container);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack movedStack = ItemStack.EMPTY;
        Slot selectedSlot = this.slots.get(index);
        if (selectedSlot.hasItem()) {
            movedStack = selectedSlot.getItem();
            ItemStack movedStackCopy = movedStack.copy();
            if (index < 11) {
                if (!this.moveItemStackTo(movedStackCopy, 11, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(movedStackCopy, 0, 1, false)) {
                return ItemStack.EMPTY;
            }

            if (movedStackCopy.isEmpty()) {
                selectedSlot.setByPlayer(ItemStack.EMPTY);
            } else {
                selectedSlot.setChanged();
            }
        }


        return movedStack;
    }

    @Override
    public void slotChanged(AbstractContainerMenu containerToSend, int dataSlotIndex, ItemStack stack) {

    }

    @Override
    public void dataChanged(AbstractContainerMenu containerMenu, int dataSlotIndex, int value) {

    }
}
