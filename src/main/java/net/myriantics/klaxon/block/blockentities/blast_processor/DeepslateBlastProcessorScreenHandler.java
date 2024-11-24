package net.myriantics.klaxon.block.blockentities.blast_processor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.api.behavior.BlastProcessorBehavior;
import net.myriantics.klaxon.block.customblocks.DeepslateBlastProcessorBlock;
import net.myriantics.klaxon.networking.packets.BlastProcessorScreenSyncPacket;
import net.myriantics.klaxon.recipes.blast_processing.BlastProcessingRecipeData;
import net.myriantics.klaxon.recipes.blast_processing.BlastProcessingOutputState;
import net.myriantics.klaxon.recipes.item_explosion_power.ItemExplosionPowerData;

public class DeepslateBlastProcessorScreenHandler extends ScreenHandler {
    private final Inventory ingredientInventory;
    private final SimpleInventory outputInventory;

    private ItemExplosionPowerData powerData;

    private BlastProcessingRecipeData blastProcessingData;

    public ScreenHandlerContext context;

    public PlayerEntity player;

    public double explosionPower;

    public double explosionPowerMin;
    public double explosionPowerMax;
    public boolean producesFire;
    public BlastProcessingOutputState outputState;

    // client constructor
        public DeepslateBlastProcessorScreenHandler(int syncId, PlayerInventory playerInventory, BlastProcessorScreenSyncPacket packetData) {
        this(syncId, playerInventory, new SimpleInventory(2), ScreenHandlerContext.EMPTY);

            setRecipeData(packetData.explosionPower(),
                    packetData.explosionPowerMin(),
                    packetData.explosionPowerMax(),
                    packetData.producesFire(),
                    packetData.outputState());
    }


    // server constructor

    public DeepslateBlastProcessorScreenHandler(int syncId, PlayerInventory playerInventory, Inventory blockEntityInventory, ScreenHandlerContext context) {
        super(KlaxonCommon.BLAST_PROCESSOR_SCREEN_HANDLER, syncId);
        checkSize(blockEntityInventory, 2);
        this.ingredientInventory = blockEntityInventory;
        this.context = context;
        this.player = playerInventory.player;
        this.outputInventory = new SimpleInventory(9);
        blockEntityInventory.onOpen(playerInventory.player);

        if (!player.getWorld().isClient) {
            BlastProcessorBehavior blastProcessorBehavior = DeepslateBlastProcessorBlock.BEHAVIORS.get(ingredientInventory.getStack(DeepslateBlastProcessorBlockEntity.CATALYST_INDEX).getItem());

            this.context.run((world, pos) -> {
                SimpleInventory recipeInventory = new SimpleInventory(blockEntityInventory.size());

                for (int i = 0; i < blockEntityInventory.size(); i++) {
                    recipeInventory.setStack(i, blockEntityInventory.getStack(i));
                }

                this.powerData = blastProcessorBehavior.getExplosionPowerData(world, pos, (DeepslateBlastProcessorBlockEntity) world.getBlockEntity(pos), recipeInventory);
                this.blastProcessingData = blastProcessorBehavior.getBlastProcessingRecipeData(world, pos, (DeepslateBlastProcessorBlockEntity) world.getBlockEntity(pos), recipeInventory, powerData);
            });
        }


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
            updateResult(world, pos, player, outputInventory);
        });
    }

    public void updateResult(World world, BlockPos pos, PlayerEntity player, SimpleInventory resultInventory) {

        BlastProcessorBehavior blastProcessorBehavior = DeepslateBlastProcessorBlock.BEHAVIORS.get(ingredientInventory.getStack(DeepslateBlastProcessorBlockEntity.CATALYST_INDEX).getItem());

        SimpleInventory recipeInventory = new SimpleInventory(ingredientInventory.size());

        for (int i = 0; i < ingredientInventory.size(); i++) {
            recipeInventory.setStack(i, ingredientInventory.getStack(i));
        }


        if (!world.isClient && player instanceof ServerPlayerEntity serverPlayer) {
            if (world.getBlockEntity(pos) instanceof DeepslateBlastProcessorBlockEntity blastProcessor) {
                this.powerData = blastProcessorBehavior.getExplosionPowerData(world, pos, blastProcessor, recipeInventory);
                this.blastProcessingData = blastProcessorBehavior.getBlastProcessingRecipeData(world, pos, blastProcessor, recipeInventory, powerData);
            }
            ServerPlayNetworking.send(serverPlayer, new BlastProcessorScreenSyncPacket(
                    blastProcessingData.explosionPowerMin(),
                    blastProcessingData.explosionPowerMax(),
                    blastProcessingData.result(),
                    blastProcessingData.outputState(),
                    powerData.explosionPower(),
                    powerData.producesFire()
                    )
            );
        }

        resultInventory.setStack(0, blastProcessingData.result());
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
                    ItemStack filteredStack = originalStack.split(DeepslateBlastProcessorBlockEntity.MaxItemStackCount);
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
    public void setRecipeData(double explosionPower, double explosionPowerMin, double explosionPowerMax, boolean producesFire, BlastProcessingOutputState outputState) {
        this.explosionPower = explosionPower;
        this.explosionPowerMin = explosionPowerMin;
        this.explosionPowerMax = explosionPowerMax;
        this.producesFire = producesFire;
        this.outputState = outputState;
    }

    public BlastProcessingRecipeData getBlastProcessingData() {
        return blastProcessingData;
    }

    public ItemExplosionPowerData getPowerData() {
        return powerData;
    }

    public Inventory getIngredientInventory() {
        return ingredientInventory;
    }
}