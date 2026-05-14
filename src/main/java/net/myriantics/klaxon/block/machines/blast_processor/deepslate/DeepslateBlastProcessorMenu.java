package net.myriantics.klaxon.block.machines.blast_processor.deepslate;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.myriantics.klaxon.block.machines.blast_processor.AbstractBlastProcessorBlockEntity;
import net.myriantics.klaxon.networking.s2c.BlastProcessorScreenSyncPacket;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeData;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeInput;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystData;
import net.myriantics.klaxon.registry.misc.KlaxonMenuTypes;
import net.myriantics.klaxon.util.PermissionsHelper;

import java.util.List;

public class DeepslateBlastProcessorMenu extends AbstractContainerMenu {
    private final Container ingredientInventory;
    private final SimpleContainer outputInventory;

    private ExplosiveCatalystData catalystData;

    private BlastProcessingRecipeData blastProcessingData;

    public ContainerLevelAccess context;

    public Player player;

    public double explosionPower;

    public double explosionPowerMin;
    public double explosionPowerMax;
    public boolean producesFire;

    // client constructor
    public DeepslateBlastProcessorMenu(int syncId, Inventory playerInventory, BlastProcessorScreenSyncPacket packetData) {
        this(syncId, playerInventory, new SimpleContainer(2), ContainerLevelAccess.NULL);

            setRecipeData(packetData.explosionPower(),
                    packetData.explosionPowerMin(),
                    packetData.explosionPowerMax(),
                    packetData.producesFire());
    }


    // server constructor
    public DeepslateBlastProcessorMenu(int syncId, Inventory playerInventory, Container blockEntityInventory, ContainerLevelAccess context) {
        super(KlaxonMenuTypes.DEEPSLATE_BLAST_PROCESSOR.value(), syncId);
        checkContainerSize(blockEntityInventory, 2);
        this.ingredientInventory = blockEntityInventory;
        this.context = context;
        this.player = playerInventory.player;
        this.outputInventory = new SimpleContainer(9);
        blockEntityInventory.startOpen(playerInventory.player);

        if (!player.level().isClientSide) {
            this.context.execute((level, pos) -> {
                if (level.getBlockEntity(pos) instanceof AbstractBlastProcessorBlockEntity blastProcessorBlockEntity) {
                    this.catalystData = blastProcessorBlockEntity.getEffectiveCatalystData();
                    this.blastProcessingData = blastProcessorBlockEntity.getDisplayStacks(new BlastProcessingRecipeInput(blastProcessorBlockEntity.getIngredientStack(), this.catalystData));
                }
            });
        }

        // ingredient slot
        this.addSlot(new Slot(ingredientInventory, 0, 35, 53 - 36) {
            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });

        // catalyst slot
        this.addSlot(new Slot(ingredientInventory, 1, 35, 53) {
            @Override
            public int getMaxStackSize() {
                return 1;
            }

            // don't allow players to modify catalyst slot - protection put in for blanketcon

            @Override
            public boolean mayPlace(ItemStack stack) {
                return super.mayPlace(stack) && PermissionsHelper.canModifyWorld(player);
            }

            @Override
            public boolean mayPickup(Player playerEntity) {
                return super.mayPickup(playerEntity) && PermissionsHelper.canModifyWorld(player);
            }
        });

        int m;
        int l;

        for (m = 0; m < 3; m++) {
            for (l = 0; l < 3; l++) {
                this.addSlot(new Slot(outputInventory, l + m * 3, 107 + l * 18, 17 + m * 18) {
                    // these are crafting output showcase slots
                    @Override
                    public boolean mayPickup(Player playerEntity) {
                        return false;
                    }

                    @Override
                    public boolean mayPlace(ItemStack stack) {
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

        slotsChanged(blockEntityInventory);
    }

    @Override
    public void slotsChanged(Container inventory) {
        this.context.execute((world, pos) -> {
            updateResult(world, pos, player, outputInventory);
        });
    }

    public void updateResult(Level world, BlockPos pos, Player player, SimpleContainer resultInventory) {

        if (!world.isClientSide && player instanceof ServerPlayer serverPlayer) {
            if (world.getBlockEntity(pos) instanceof DeepslateBlastProcessorBlockEntity blastProcessor) {
                ExplosiveCatalystData newPowerData = ExplosiveCatalystData.findEffective(blastProcessor.getContext(serverPlayer.serverLevel()), blastProcessor.getCatalystStack());
                BlastProcessingRecipeData newBlastProcessingData = blastProcessor.getDisplayStacks(new BlastProcessingRecipeInput(blastProcessor.getIngredientStack(), newPowerData));

                // Make sure we've changed something before sending an update packet
                if (newPowerData != null && !newPowerData.equals(catalystData) || !newBlastProcessingData.equals(this.blastProcessingData)) {
                    this.catalystData = newPowerData;
                    this.blastProcessingData = newBlastProcessingData;

                    ServerPlayNetworking.send(serverPlayer, new BlastProcessorScreenSyncPacket(
                                    blastProcessingData.explosionPowerMin(),
                                    blastProcessingData.explosionPowerMax(),
                                    blastProcessingData.outputStacks(),
                                    catalystData.explosionPower(),
                                    catalystData.producesFire()
                            )
                    );
                }
            }
        }

        // yonk the display stacks
        List<ItemStack> displayStacks = blastProcessingData.outputStacks();

        // update display inventory
        for (int i = 0; i < resultInventory.getContainerSize(); i++) {
            // set slot to display stack if possible, otherwise clear it
            resultInventory.setItem(i, i < displayStacks.size() ? displayStacks.get(i) : ItemStack.EMPTY);
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return this.ingredientInventory.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int sourceSlotIndex) {
        Slot slot = this.slots.get(sourceSlotIndex);

        if (slot.hasItem()) {
            ItemStack originalStack = slot.getItem();
            if (sourceSlotIndex < this.ingredientInventory.getContainerSize()) {
                // machine inventory to player inventory
                if (!this.moveItemStackTo(originalStack, this.ingredientInventory.getContainerSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
                // player inventory to machine inventory
            } else {
                // yonked stacking protection logic from EnchantmentScreenHandler - unexpected enchant table carry
                for (int i = 0; i < this.ingredientInventory.getContainerSize(); i++) {
                    Slot selected = this.slots.get(i);
                    if (selected.hasItem() || !selected.mayPlace(originalStack)) {
                        continue;
                    }
                    ItemStack filteredStack = originalStack.split(selected.getMaxStackSize());
                    selected.setByPlayer(filteredStack);
                    break;
                }
            }

            if (originalStack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }


        // If above fails, return original stack as new stack, so nothing changes.
        return ItemStack.EMPTY;
    }

    @Environment(EnvType.CLIENT)
    public void setRecipeData(double explosionPower, double explosionPowerMin, double explosionPowerMax, boolean producesFire) {
        this.explosionPower = explosionPower;
        this.explosionPowerMin = explosionPowerMin;
        this.explosionPowerMax = explosionPowerMax;
        this.producesFire = producesFire;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
    }

    @Override
    public void broadcastChanges() {
        if (this.ingredientInventory instanceof DeepslateBlastProcessorBlockEntity blastProcessor && this.player instanceof ServerPlayer serverPlayer && blastProcessor.isUnlooted()) {
            serverPlayer.closeContainer();
        } else {
            super.broadcastChanges();
        }
    }
}
