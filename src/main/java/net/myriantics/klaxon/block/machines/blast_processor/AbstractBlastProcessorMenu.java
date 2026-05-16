package net.myriantics.klaxon.block.machines.blast_processor;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystData;
import net.myriantics.klaxon.networking.s2c.BlastProcessorMenuPowerSyncPacket;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeData;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeInput;
import net.myriantics.klaxon.util.PermissionsHelper;
import net.myriantics.klaxon.util.container.KlaxonAdvancedContainerMenu;
import net.myriantics.klaxon.util.container.KlaxonClientMenuInitializer;

import java.util.Iterator;

public abstract class AbstractBlastProcessorMenu extends KlaxonAdvancedContainerMenu implements ContainerListener {

    protected SimpleContainer outputInventory;

    protected double explosionPower = 0.0;
    protected double explosionPowerMin = 0.0;
    protected double explosionPowerMax = 0.0;
    protected boolean producesFire = false;

    protected Slot catalystSlot;
    protected Slot ingredientSlot;
    protected Slot[] outputSlots;

    protected AbstractBlastProcessorMenu(MenuType<?> menuType, int containerId, Inventory playerInventory, KlaxonClientMenuInitializer<AbstractBlastProcessorMenu> initializer) {
        super(menuType, containerId, playerInventory, initializer);
    }

    protected AbstractBlastProcessorMenu(MenuType<?> menuType, int containerId, Inventory playerInventory, Container container, ContainerData data, ContainerLevelAccess access) {
        super(menuType, containerId, playerInventory, container, data, access);
        this.addSlotListener(this);
    }

    protected int getIngredientStackSize() {
        return 1;
    }

    @Override
    protected void initSlots(Inventory inventory, Container container) {
        super.initSlots(inventory, container);

        // ingredient slot
        this.ingredientSlot = this.addSlot(new Slot(container instanceof AbstractBlastProcessorBlockEntity blastProcessor ? blastProcessor.ingredientPartition : new SimpleContainer(1), 0, 17, 17) {
            @Override
            public int getMaxStackSize() {
                return AbstractBlastProcessorMenu.this.getIngredientStackSize();
            }

            @Override
            public void setChanged() {
                super.setChanged();
                AbstractBlastProcessorMenu.this.access.execute(AbstractBlastProcessorMenu.this::recomputeOutput);
                AbstractBlastProcessorMenu.this.slotsChanged(this.container);
            }
        });

        // catalyst slot
        this.catalystSlot = this.addSlot(new Slot(container instanceof AbstractBlastProcessorBlockEntity blastProcessor ? blastProcessor.catalystPartition : new SimpleContainer(1), 0, 17, 53) {
            @Override
            public int getMaxStackSize() {
                return 1;
            }

            // don't allow players to modify catalyst slot - protection put in for blanketcon

            @Override
            public boolean mayPlace(ItemStack stack) {
                return super.mayPlace(stack) && PermissionsHelper.canModifyWorld(inventory.player);
            }

            @Override
            public boolean mayPickup(Player playerEntity) {
                return super.mayPickup(playerEntity) && PermissionsHelper.canModifyWorld(inventory.player);
            }

            @Override
            public void setChanged() {
                super.setChanged();
                AbstractBlastProcessorMenu.this.access.execute(AbstractBlastProcessorMenu.this::recomputeOutput);
                AbstractBlastProcessorMenu.this.slotsChanged(this.container);
            }
        });

        // these are crafting output showcase slots
        this.outputSlots = new Slot[9];
        this.outputInventory = new SimpleContainer(9);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int selected = i + j * 3;
                this.outputSlots[selected] = this.addSlot(new Slot(this.outputInventory, selected, 107 + i * 18, 17 + j * 18) {
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
    }

    private void recomputeOutput(Level level, BlockPos pos) {
        if (level instanceof ServerLevel serverLevel && this.container instanceof AbstractBlastProcessorBlockEntity blastProcessor) {
            ExplosiveCatalystData newPowerData = ExplosiveCatalystData.findEffective(blastProcessor.getContext(serverLevel), this.catalystSlot.getItem());
            if (newPowerData == null) {
                newPowerData = ExplosiveCatalystData.ZERO;
            }

            this.explosionPower = newPowerData.explosionPower();
            this.producesFire = newPowerData.producesFire();

            BlastProcessingRecipeData recipeData = blastProcessor.getDisplayStacks(new BlastProcessingRecipeInput(this.ingredientSlot.getItem(), newPowerData, serverLevel.getRandom()));
            if (recipeData == null) {
                recipeData = BlastProcessingRecipeData.ZERO;
            }

            this.explosionPowerMin = recipeData.explosionPowerMin();
            this.explosionPowerMax = recipeData.explosionPowerMax();

            Iterator<ItemStack> iterator = recipeData.outputStacks().iterator();
            for (Slot slot : this.outputSlots) {
                slot.set(iterator.hasNext() ? iterator.next() : ItemStack.EMPTY);
            }
        }
    }

    @Override
    public void sendAllDataToRemote() {
        super.sendAllDataToRemote();
        this.syncExplosionPowerData();
    }

    private void syncExplosionPowerData() {
        if (this.playerInventory.player instanceof ServerPlayer serverPlayer) {
            ServerPlayNetworking.send(
                    serverPlayer,
                    new BlastProcessorMenuPowerSyncPacket(
                            this.explosionPowerMin,
                            this.explosionPowerMax,
                            this.explosionPower,
                            this.producesFire
                    )
            );
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int sourceSlotIndex) {
        Slot slot = this.slots.get(sourceSlotIndex);

        if (slot.hasItem()) {
            ItemStack originalStack = slot.getItem();
            if (sourceSlotIndex < this.container.getContainerSize()) {
                // machine inventory to player inventory
                if (!this.moveItemStackTo(originalStack, this.container.getContainerSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
                // player inventory to machine inventory
            } else {
                // yonked stacking protection logic from EnchantmentScreenHandler - unexpected enchant table carry
                for (int i = 0; i < this.container.getContainerSize(); i++) {
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

    @Override
    public void slotChanged(AbstractContainerMenu containerToSend, int dataSlotIndex, ItemStack stack) {
        this.access.execute(this::recomputeOutput);
        this.syncExplosionPowerData();
    }

    @Override
    public void dataChanged(AbstractContainerMenu containerMenu, int dataSlotIndex, int value) {

    }

    public void updatePowerData(BlastProcessorMenuPowerSyncPacket packet) {
        this.explosionPower = packet.explosionPower();
        this.explosionPowerMin = packet.explosionPowerMin();
        this.explosionPowerMax = packet.explosionPowerMax();
        this.producesFire = packet.producesFire();
    }

    public double getExplosionPowerMin() {
        return this.explosionPowerMin;
    }

    public double getExplosionPowerMax() {
        return this.explosionPowerMax;
    }

    public double getExplosionPower() {
        return this.explosionPower;
    }

    public boolean producesFire() {
        return this.producesFire;
    }

    public boolean hasCatalyst() {
        return this.catalystSlot.hasItem();
    }

    public boolean hasIngredient() {
        return this.ingredientSlot.hasItem();
    }
}
