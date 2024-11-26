package net.myriantics.klaxon.block.blockentities.blast_processor;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;

import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.*;
import net.minecraft.world.WorldEvents;
import net.myriantics.klaxon.api.behavior.BlastProcessorBehavior;
import net.myriantics.klaxon.block.KlaxonBlockEntities;
import net.myriantics.klaxon.block.customblocks.DeepslateBlastProcessorBlock;
import net.myriantics.klaxon.networking.packets.BlastProcessorScreenSyncPacket;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeData;
import net.myriantics.klaxon.recipe.item_explosion_power.ItemExplosionPowerData;
import net.myriantics.klaxon.util.BlockDirectionHelper;
import net.myriantics.klaxon.util.ImplementedInventory;
import org.jetbrains.annotations.Nullable;

import static net.myriantics.klaxon.block.customblocks.DeepslateBlastProcessorBlock.BEHAVIORS;
import static net.myriantics.klaxon.block.customblocks.DeepslateBlastProcessorBlock.FACING;

public class DeepslateBlastProcessorBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory<BlastProcessorScreenSyncPacket>, ImplementedInventory, SidedInventory {
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
    public static final int PROCESS_ITEM_INDEX = 1;
    public static final int CATALYST_INDEX = 0;
    private static final int[] PROCESS_ITEM_SLOTS = new int[]{PROCESS_ITEM_INDEX};
    private static final int[] CATALYST_ITEM_SLOTS = new int[]{CATALYST_INDEX};
    public static final int MaxItemStackCount = 1;
    public static final double MAXIMUM_CONTAINED_EXPLOSION_POWER = 0.5;

    private DeepslateBlastProcessorScreenHandler screenHandler;

    public DeepslateBlastProcessorBlockEntity(BlockPos pos, BlockState state) {
        super(KlaxonBlockEntities.DEEPSLATE_BLAST_PROCESSOR_BLOCK_ENTITY, pos, state);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        this.screenHandler = new DeepslateBlastProcessorScreenHandler(syncId, playerInventory, this, ScreenHandlerContext.create(world, pos));
        screenHandler.onContentChanged(this);
        return screenHandler;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
    }

    public int size() {
        return 2;
    }

    @Override
    public int getMaxCountPerStack() {
        return MaxItemStackCount;
    }

    @Override
    public boolean canTransferTo(Inventory hopperInventory, int slot, ItemStack stack) {
        return ImplementedInventory.super.canTransferTo(hopperInventory, slot, stack);
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        if (world != null) {
            Direction blockFacing = world.getBlockState(pos).get(Properties.FACING);
            if (side == BlockDirectionHelper.getLeft(blockFacing) || side == BlockDirectionHelper.getRight(blockFacing)) {
                return CATALYST_ITEM_SLOTS;
            }
            if(side == Direction.UP || side == Direction.DOWN) {
                return PROCESS_ITEM_SLOTS;
            }
        }
        return new int[]{-1};
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        // if the slot you want to access is available for the side you're accessing from, check if the item is valid
        // for that stack
        int[] availableSlots = getAvailableSlots(dir);

        if (availableSlots == null || stack.isEmpty()) {
            return false;
        }

        for (int availableSlot : availableSlots) {
            if (slot == availableSlot) {
                return this.isValid(slot, stack);
            }
        }
        return false;
    }



    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        if (world != null && !world.isReceivingRedstonePower(pos)) {
            // get the available slots for the side you're trying to pull from
            for (int checkedSlotIndex : getAvailableSlots(dir)) {
                // if the slot you're trying to pull from is in that array, yeah you can extract
                if (slot == checkedSlotIndex) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        // look through the whole inventory and return true if selected slot is inbounds and empty
        for (int i = 0; i < this.size(); i++) {
            if (slot == i) {
                return getStack(slot).isEmpty();
            }
        }
        return false;
    }

    public void onRedstoneImpulse() {
        if (world != null) {
            if (!inventory.isEmpty()) {

                // calculate behavior to use
                BlastProcessorBehavior blastProcessorBehavior = BEHAVIORS.get(this.inventory.get(CATALYST_INDEX).getItem());

                // inventory bullshit i need to fix someday
                RecipeInput recipeInventory = new RecipeInput() {
                    @Override
                    public ItemStack getStackInSlot(int slot) {
                        return inventory.get(slot);
                    }

                    @Override
                    public int getSize() {
                        return size();
                    }
                };

                // get recipe data
                ItemExplosionPowerData powerData = blastProcessorBehavior.getExplosionPowerData(world, pos, this, recipeInventory);
                BlastProcessingRecipeData processingData = blastProcessorBehavior.getBlastProcessingRecipeData(world, pos, this, recipeInventory, powerData);

                // do explosion effect
                blastProcessorBehavior.onExplosion(world, pos, this, powerData);

                // eject recipe results
                blastProcessorBehavior.ejectItems(world, pos, this, processingData, powerData);
            }

            // if this has been exploded, dont run these
            if (!this.isRemoved()) {
                world.syncWorldEvent(WorldEvents.DISPENSER_DISPENSES, pos, 0);
                world.syncWorldEvent(WorldEvents.DISPENSER_ACTIVATED, pos, world.getBlockState(pos).get(FACING).getId());
            }
        }
    }

    public void updateBlockState(@Nullable BlockState appendedState) {
        if (world != null && world.getBlockState(pos).getBlock() instanceof DeepslateBlastProcessorBlock blastProcessorBlock) {
            blastProcessorBlock.updateBlockState(world, pos, appendedState);
        }
    }


    @Override
    public void markDirty() {
        if (this.screenHandler != null && this.screenHandler.player.currentScreenHandler.syncId == this.screenHandler.syncId) {
            screenHandler.onContentChanged(this);
        }
        updateBlockState(null);
        super.markDirty();
    }

    public Position getOutputLocation(Direction direction) {
        Position centerPos = pos.toCenterPos();
        double x = centerPos.getX();
        double y = centerPos.getY();
        double z = centerPos.getZ();

        switch (direction) {
            case UP -> y += 0.6;
            case DOWN -> y -= 0.6;
            case NORTH -> z -= 0.6;
            case SOUTH -> z += 0.6;
            case EAST -> x += 0.6;
            case WEST -> x -= 0.6;
        }

        return new Vec3d(x, y, z);
    }

    @Override
    public BlastProcessorScreenSyncPacket getScreenOpeningData(ServerPlayerEntity player) {
        BlastProcessingRecipeData blastProcessingRecipeData = screenHandler.getBlastProcessingData();
        ItemExplosionPowerData itemExplosionPowerData = screenHandler.getPowerData();

        return new BlastProcessorScreenSyncPacket(blastProcessingRecipeData.explosionPowerMin(),
                blastProcessingRecipeData.explosionPowerMax(),
                blastProcessingRecipeData.result(),
                blastProcessingRecipeData.outputState(),
                itemExplosionPowerData.explosionPower(),
                itemExplosionPowerData.producesFire());
    }
}
