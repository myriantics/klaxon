package net.myriantics.klaxon.block.blockentities.blast_processor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.networking.KlaxonS2CPacketSender;
import net.myriantics.klaxon.recipes.blast_processing.BlastProcessingInator;
import net.myriantics.klaxon.recipes.blast_processing.BlastProcessorOutputState;

public class BlastProcessorScreenHandler extends ScreenHandler {
    private final Inventory ingredientInventory;
    private final SimpleInventory outputInventory;

    public BlastProcessingInator getInator() {
        return inator;
    }

    private BlastProcessingInator inator;

    public ScreenHandlerContext context;

    public PlayerEntity player;

    private PacketByteBuf clientRecipeDataBuf = null;

    public double explosionPower;
    public double explosionPowerMin;
    public double explosionPowerMax;
    public boolean producesFire;
    public BlastProcessorOutputState outputState;

    // client constructor
    public BlastProcessorScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, new SimpleInventory(2), ScreenHandlerContext.EMPTY);
        setRecipeData(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readBoolean(), buf.readEnumConstant(BlastProcessorOutputState.class));
    }

    // server constructor
    public BlastProcessorScreenHandler(int syncId, PlayerInventory playerInventory, Inventory blockEntityInventory, ScreenHandlerContext context) {
        super(KlaxonCommon.BLAST_PROCESSOR_SCREEN_HANDLER, syncId);
        checkSize(blockEntityInventory, 2);
        this.ingredientInventory = blockEntityInventory;
        this.context = context;
        this.player = playerInventory.player;
        this.outputInventory = new SimpleInventory(9);
        blockEntityInventory.onOpen(playerInventory.player);

        this.inator = new BlastProcessingInator(player.getWorld(), ingredientInventory);

        // machine slots
        for (int i = 0; i < 2; i++) {
            this.addSlot(new Slot(ingredientInventory, i, 35, 53 - i * 36) {
                @Override
                public int getMaxItemCount() {
                    return 1;
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
    }



    @Override
    public void onContentChanged(Inventory inventory) {
        this.context.run((world, pos) -> {
            updateResult(world, player, outputInventory);
        });
    }

    public void updateResult(World world, PlayerEntity player, SimpleInventory resultInventory) {

        this.inator = new BlastProcessingInator(world, ingredientInventory);

        if (!world.isClient && player instanceof ServerPlayerEntity playerEntity) {
            KlaxonS2CPacketSender.sendBlastProcessorScreenSyncData(inator, playerEntity, syncId);
        }

        resultInventory.setStack(0, inator.getResult());
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.ingredientInventory.canPlayerUse(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int sourceSlotIndex) {
        Slot slot = this.slots.get(sourceSlotIndex);

        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            if (sourceSlotIndex < this.ingredientInventory.size()) {
                // machine inventory to player inventory
                if (!this.insertItem(originalStack, this.ingredientInventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
                // player inventory to machine inventory
            } else {
                // yonked stacking protection logic from EnchantmentScreenHandler - unexpected enchant table carry
                for (int i = 0; i < this.ingredientInventory.size(); i++) {
                    if (this.slots.get(i).hasStack() || !this.slots.get(i).canInsert(originalStack)) {
                        continue;
                    }
                    ItemStack filteredStack = originalStack.split(BlastProcessorBlockEntity.MaxItemStackCount);
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

    @Environment(EnvType.CLIENT)
    public void setRecipeData(double explosionPower, double explosionPowerMin, double explosionPowerMax, boolean producesFire, BlastProcessorOutputState outputState) {
        this.explosionPower = explosionPower;
        this.explosionPowerMin = explosionPowerMin;
        this.explosionPowerMax = explosionPowerMax;
        this.producesFire = producesFire;
        this.outputState = outputState;
    }

    public Inventory getIngredientInventory() {
        return ingredientInventory;
    }
}
