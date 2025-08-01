package net.myriantics.klaxon.block.customblocks.machines.blast_processor.deepslate;

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
import net.myriantics.klaxon.api.behavior.blast_processor_catalyst.BlastProcessorCatalystBehavior;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeInput;
import net.myriantics.klaxon.recipe.blast_processor_behavior.BlastProcessorBehaviorRecipeLogic;
import net.myriantics.klaxon.recipe.item_explosion_power.ExplosiveCatalystRecipeInput;
import net.myriantics.klaxon.util.PermissionsHelper;
import net.myriantics.klaxon.networking.s2c.BlastProcessorScreenSyncPacket;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeData;
import net.myriantics.klaxon.recipe.item_explosion_power.ItemExplosionPowerData;
import net.myriantics.klaxon.registry.misc.KlaxonScreenHandlers;

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

    // client constructor
        public DeepslateBlastProcessorScreenHandler(int syncId, PlayerInventory playerInventory, BlastProcessorScreenSyncPacket packetData) {
        this(syncId, playerInventory, new SimpleInventory(2), ScreenHandlerContext.EMPTY);

            setRecipeData(packetData.explosionPower(),
                    packetData.explosionPowerMin(),
                    packetData.explosionPowerMax(),
                    packetData.producesFire());
    }


    // server constructor
    public DeepslateBlastProcessorScreenHandler(int syncId, PlayerInventory playerInventory, Inventory blockEntityInventory, ScreenHandlerContext context) {
        super(KlaxonScreenHandlers.BLAST_PROCESSOR_SCREEN_HANDLER, syncId);
        checkSize(blockEntityInventory, 2);
        this.ingredientInventory = blockEntityInventory;
        this.context = context;
        this.player = playerInventory.player;
        this.outputInventory = new SimpleInventory(9);
        blockEntityInventory.onOpen(playerInventory.player);

        if (!player.getWorld().isClient) {
            this.context.run((world, pos) -> {
                ExplosiveCatalystRecipeInput catalystInput = new ExplosiveCatalystRecipeInput(blockEntityInventory.getStack(DeepslateBlastProcessorBlockEntity.CATALYST_INDEX));

                BlastProcessorCatalystBehavior blastProcessorBehavior = BlastProcessorBehaviorRecipeLogic.computeBehavior(world, catalystInput);
                this.powerData = blastProcessorBehavior.getExplosionPowerData(world, pos, (DeepslateBlastProcessorBlockEntity) world.getBlockEntity(pos), catalystInput);

                BlastProcessingRecipeInput recipeInput = new BlastProcessingRecipeInput(ingredientInventory.getStack(DeepslateBlastProcessorBlockEntity.INGREDIENT_INDEX), powerData);
                this.blastProcessingData = blastProcessorBehavior.getBlastProcessingRecipeData(world, pos, (DeepslateBlastProcessorBlockEntity) world.getBlockEntity(pos), recipeInput);
            });
        }

        // ingredient slot
        this.addSlot(new Slot(ingredientInventory, 0, 35, 53 - 36) {
            @Override
            public int getMaxItemCount() {
                return 1;
            }
        });

        // catalyst slot
        this.addSlot(new Slot(ingredientInventory, 1, 35, 53) {
            @Override
            public int getMaxItemCount() {
                return 1;
            }

            // don't allow players to modify catalyst slot - protection put in for blanketcon

            @Override
            public boolean canInsert(ItemStack stack) {
                return super.canInsert(stack) && PermissionsHelper.canModifyWorld(player);
            }

            @Override
            public boolean canTakeItems(PlayerEntity playerEntity) {
                return super.canTakeItems(playerEntity) && PermissionsHelper.canModifyWorld(player);
            }
        });

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

        onContentChanged(blockEntityInventory);
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        this.context.run((world, pos) -> {
            updateResult(world, pos, player, outputInventory);
        });
    }

    public void updateResult(World world, BlockPos pos, PlayerEntity player, SimpleInventory resultInventory) {

        ExplosiveCatalystRecipeInput catalystInput = new ExplosiveCatalystRecipeInput(ingredientInventory.getStack(DeepslateBlastProcessorBlockEntity.CATALYST_INDEX));

        BlastProcessorCatalystBehavior blastProcessorBehavior = BlastProcessorBehaviorRecipeLogic.computeBehavior(world, catalystInput);

        if (!world.isClient && player instanceof ServerPlayerEntity serverPlayer) {
            if (world.getBlockEntity(pos) instanceof DeepslateBlastProcessorBlockEntity blastProcessor) {
                ItemExplosionPowerData newPowerData = blastProcessorBehavior.getExplosionPowerData(world, pos, blastProcessor, catalystInput);
                BlastProcessingRecipeData newBlastProcessingData = blastProcessorBehavior.getBlastProcessingRecipeData(world, pos, blastProcessor, new BlastProcessingRecipeInput(ingredientInventory.getStack(DeepslateBlastProcessorBlockEntity.INGREDIENT_INDEX), newPowerData));

                // Make sure we've changed something before sending an update packet
                if (!newPowerData.equals(powerData) || !newBlastProcessingData.equals(this.blastProcessingData)) {
                    this.powerData = newPowerData;
                    this.blastProcessingData = newBlastProcessingData;

                    ServerPlayNetworking.send(serverPlayer, new BlastProcessorScreenSyncPacket(
                                    blastProcessingData.explosionPowerMin(),
                                    blastProcessingData.explosionPowerMax(),
                                    blastProcessingData.result(),
                                    powerData.explosionPower(),
                                    powerData.producesFire()
                            )
                    );
                }
            }
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

        if (slot.hasStack()) {
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
    public void setRecipeData(double explosionPower, double explosionPowerMin, double explosionPowerMax, boolean producesFire) {
        this.explosionPower = explosionPower;
        this.explosionPowerMin = explosionPowerMin;
        this.explosionPowerMax = explosionPowerMax;
        this.producesFire = producesFire;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        if (ingredientInventory instanceof DeepslateBlastProcessorBlockEntity blastProcessor) {
            blastProcessor.removeScreenHandler(this);
        }
    }
}
