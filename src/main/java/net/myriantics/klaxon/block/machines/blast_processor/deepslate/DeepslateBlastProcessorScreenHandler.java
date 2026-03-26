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
import net.myriantics.klaxon.networking.s2c.BlastProcessorScreenSyncPacket;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeData;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeInput;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystData;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystDefinitionRecipeInput;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystDefinitionRecipeLogic;
import net.myriantics.klaxon.registry.misc.KlaxonScreenHandlers;
import net.myriantics.klaxon.util.PermissionsHelper;

public class DeepslateBlastProcessorScreenHandler extends AbstractContainerMenu {
    private final Container ingredientInventory;
    private final SimpleContainer outputInventory;

    private ExplosiveCatalystData powerData;

    private BlastProcessingRecipeData blastProcessingData;

    public ContainerLevelAccess context;

    public Player player;

    public double explosionPower;

    public double explosionPowerMin;
    public double explosionPowerMax;
    public boolean producesFire;

    // client constructor
        public DeepslateBlastProcessorScreenHandler(int syncId, Inventory playerInventory, BlastProcessorScreenSyncPacket packetData) {
        this(syncId, playerInventory, new SimpleContainer(2), ContainerLevelAccess.NULL);

            setRecipeData(packetData.explosionPower(),
                    packetData.explosionPowerMin(),
                    packetData.explosionPowerMax(),
                    packetData.producesFire());
    }


    // server constructor
    public DeepslateBlastProcessorScreenHandler(int syncId, Inventory playerInventory, Container blockEntityInventory, ContainerLevelAccess context) {
        super(KlaxonScreenHandlers.BLAST_PROCESSOR_SCREEN_HANDLER.value(), syncId);
        checkContainerSize(blockEntityInventory, 2);
        this.ingredientInventory = blockEntityInventory;
        this.context = context;
        this.player = playerInventory.player;
        this.outputInventory = new SimpleContainer(9);
        blockEntityInventory.startOpen(playerInventory.player);

        if (!player.level().isClientSide) {
            this.context.execute((world, pos) -> {
                ExplosiveCatalystDefinitionRecipeInput catalystInput = new ExplosiveCatalystDefinitionRecipeInput(blockEntityInventory.getItem(DeepslateBlastProcessorBlockEntity.CATALYST_INDEX));

                this.powerData = ExplosiveCatalystDefinitionRecipeLogic.computeExplosiveCatalystData(world, pos, (DeepslateBlastProcessorBlockEntity) world.getBlockEntity(pos), catalystInput);

                BlastProcessingRecipeInput recipeInput = new BlastProcessingRecipeInput(ingredientInventory.getItem(DeepslateBlastProcessorBlockEntity.INGREDIENT_INDEX), powerData);
                this.blastProcessingData = this.powerData.behavior().value().getBlastProcessingPreviewData(world, pos, (DeepslateBlastProcessorBlockEntity) world.getBlockEntity(pos), recipeInput);
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

        ExplosiveCatalystDefinitionRecipeInput catalystInput = new ExplosiveCatalystDefinitionRecipeInput(ingredientInventory.getItem(DeepslateBlastProcessorBlockEntity.CATALYST_INDEX));


        if (!world.isClientSide && player instanceof ServerPlayer serverPlayer) {
            if (world.getBlockEntity(pos) instanceof DeepslateBlastProcessorBlockEntity blastProcessor) {
                ExplosiveCatalystData newPowerData = ExplosiveCatalystDefinitionRecipeLogic.computeExplosiveCatalystData(world, pos, blastProcessor, catalystInput);
                BlastProcessingRecipeData newBlastProcessingData = newPowerData.behavior().value().getBlastProcessingPreviewData(world, pos, blastProcessor, new BlastProcessingRecipeInput(ingredientInventory.getItem(DeepslateBlastProcessorBlockEntity.INGREDIENT_INDEX), newPowerData));

                // Make sure we've changed something before sending an update packet
                if (!newPowerData.equals(powerData) || !newBlastProcessingData.equals(this.blastProcessingData)) {
                    this.powerData = newPowerData;
                    this.blastProcessingData = newBlastProcessingData;

                    ServerPlayNetworking.send(serverPlayer, new BlastProcessorScreenSyncPacket(
                                    blastProcessingData.explosionPowerMin(),
                                    blastProcessingData.explosionPowerMax(),
                                    blastProcessingData.outputStacks(),
                                    powerData.explosionPower(),
                                    powerData.producesFire()
                            )
                    );
                }
            }
        }

        // yonk the display stacks
        ItemStack[] displayStacks = blastProcessingData.outputStacks();

        // update display inventory
        for (int i = 0; i < resultInventory.getContainerSize(); i++) {
            // set slot to display stack if possible, otherwise clear it
            resultInventory.setItem(i, i < displayStacks.length ? displayStacks[i] : ItemStack.EMPTY);
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
                    if (this.slots.get(i).hasItem() || !this.slots.get(i).mayPlace(originalStack)) {
                        continue;
                    }
                    ItemStack filteredStack = originalStack.split(DeepslateBlastProcessorBlockEntity.MAX_HELD_STACK_COUNT);
                    this.slots.get(i).setByPlayer(filteredStack);
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
        if (ingredientInventory instanceof DeepslateBlastProcessorBlockEntity blastProcessor) {
            blastProcessor.removeScreenHandler(this);
        }
    }
}
