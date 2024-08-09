package net.myriantics.klaxon.block.blockentities.blast_processor;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.myriantics.klaxon.KlaxonMain;

public class BlastProcessorScreenHandler extends ScreenHandler {
    private final Inventory inventory;

    // client constructor
    public BlastProcessorScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(2));
    }

    // server constructor
    public BlastProcessorScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(KlaxonMain.BLAST_PROCESSOR_SCREEN_HANDLER, syncId);
        checkSize(inventory, 2);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);

        int m;
        int l;
        // machine slots
        for (m = 0; m < 1; m++) {
            for (l = 0; l < 2; l++) {
                this.addSlot(new Slot(inventory, l, 62 + l * 18, 17 + m * 18) {
                    // Prevents visual stutter on client
                    @Override
                    public int getMaxItemCount() {
                        return BlastProcessorBlockEntity.MaxItemStackCount;
                    }
                });
            }
        }

        // player inventory
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 84 + m * 18));
            }
        }

        // hotbar
        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 142));
        }

    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int sourceSlotIndex) {
        //MinecraftClient.getInstance().player.sendMessage(Text.literal("client: " + player.getWorld().isClient));
        ItemStack newStack = ItemStack.EMPTY;
        //MinecraftClient.getInstance().player.sendMessage(Text.literal("index: " + sourceSlotIndex));
        MinecraftClient.getInstance().player.sendMessage(Text.literal("looped"));
        Slot slot = this.slots.get(sourceSlotIndex);

        //MinecraftClient.getInstance().player.sendMessage(Text.literal("start_index: " + 0));
        //MinecraftClient.getInstance().player.sendMessage(Text.literal("end_index: " + this.inventory.size()));
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (sourceSlotIndex < this.inventory.size()) {
                // machine inventory to player inventory
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
                // player inventory to machine inventory
            } else {
                // yonked stacking protection logic from EnchantmentScreenHandler - unexpected enchant table carry
                for (int i = 0; i < this.inventory.size(); i++) {
                    if (this.slots.get(i).hasStack() || !this.slots.get(i).canInsert(newStack)) {
                        continue;
                    }
                    ItemStack filteredStack = newStack.copyWithCount(BlastProcessorBlockEntity.MaxItemStackCount);
                    newStack.decrement(BlastProcessorBlockEntity.MaxItemStackCount);
                    this.slots.get(i).setStack(filteredStack);
                    break;
                }
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        // If above fails, return original stack as new stack, so nothing changes.
        return ItemStack.EMPTY;
    }
}
