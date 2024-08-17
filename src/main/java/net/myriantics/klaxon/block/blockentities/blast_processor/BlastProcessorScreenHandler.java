package net.myriantics.klaxon.block.blockentities.blast_processor;

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.myriantics.klaxon.KlaxonMain;
import net.myriantics.klaxon.recipes.blast_processing.BlastProcessingInator;
import net.myriantics.klaxon.recipes.blast_processing.BlastProcessorOutputState;
import net.myriantics.klaxon.recipes.blast_processing.BlastProcessorRecipe;
import net.myriantics.klaxon.recipes.item_explosion_power.ItemExplosionPowerRecipe;
import net.myriantics.klaxon.util.ImplementedInventory;
import net.myriantics.klaxon.util.ItemExplosionPowerHelper;

import java.util.Optional;

import static net.myriantics.klaxon.block.blockentities.blast_processor.BlastProcessorBlockEntity.CATALYST_INDEX;
import static net.myriantics.klaxon.block.blockentities.blast_processor.BlastProcessorBlockEntity.PROCESS_ITEM_INDEX;

public class BlastProcessorScreenHandler extends ScreenHandler {
    private final Inventory ingredientInventory;
    private final SimpleInventory outputInventory;

    private BlastProcessingInator inator;

    public ScreenHandlerContext context;

    public PlayerEntity player;

    // client constructor
    public BlastProcessorScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(2), ScreenHandlerContext.EMPTY);
    }

    // server constructor
    public BlastProcessorScreenHandler(int syncId, PlayerInventory playerInventory, Inventory blockEntityInventory, ScreenHandlerContext context) {
        super(KlaxonMain.BLAST_PROCESSOR_SCREEN_HANDLER, syncId);
        checkSize(blockEntityInventory, 2);
        this.ingredientInventory = blockEntityInventory;
        this.context = context;
        this.player = playerInventory.player;
        this.outputInventory = new SimpleInventory(9);
        blockEntityInventory.onOpen(playerInventory.player);

        this.inator = new BlastProcessingInator(player.getWorld(), ingredientInventory);

        // machine slots
        for (int i = 0; i < 2; i++) {
            this.addSlot(new Slot(ingredientInventory, i, 35, 17 + i * 36) {
                @Override
                public void markDirty() {
                    onContentChanged(ingredientInventory);
                    super.markDirty();
                }

                @Override
                public void setStack(ItemStack stack) {
                    onContentChanged(ingredientInventory);
                    super.setStack(stack);
                }

                @Override
                public ItemStack takeStack(int amount) {
                    onContentChanged(ingredientInventory);
                    return super.takeStack(amount);
                }
            });
        }

        int m;
        int l;

        for (m = 0; m < 3; m++) {
            for (l = 0; l < 3; l++) {
                this.addSlot(new Slot(outputInventory, l + m * 3, 107 + l * 18, 17 + m * 18) {
                    // these are crafting output showcase slots
                    @Override
                    public boolean canTakeItems(PlayerEntity playerEntity) {
                        return false;
                    }

                    @Override
                    public boolean canInsert(ItemStack stack) {
                        return false;
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

        updateResult(this, player.getWorld(), player, ingredientInventory, outputInventory);
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        this.context.run((world, pos) -> {
            updateResult(this, world, player, ingredientInventory, outputInventory);
        });
    }

    public void updateResult(ScreenHandler handler, World world, PlayerEntity player, Inventory craftingInventory, SimpleInventory resultInventory) {
        player.sendMessage(Text.literal("POGGIES"));

        ItemStack processItem = craftingInventory.getStack(PROCESS_ITEM_INDEX);
        ItemStack catalystItem = craftingInventory.getStack(CATALYST_INDEX);

        this.inator = new BlastProcessingInator(world, ingredientInventory);

        resultInventory.setStack(0, inator.getResult());
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.ingredientInventory.canPlayerUse(player);
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
            if (sourceSlotIndex < this.ingredientInventory.size()) {
                // machine inventory to player inventory
                if (!this.insertItem(originalStack, this.ingredientInventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
                // player inventory to machine inventory
            } else {
                // yonked stacking protection logic from EnchantmentScreenHandler - unexpected enchant table carry
                for (int i = 0; i < this.ingredientInventory.size(); i++) {
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

    public BlastProcessingInator getInator() {
        return inator;
    }
}
