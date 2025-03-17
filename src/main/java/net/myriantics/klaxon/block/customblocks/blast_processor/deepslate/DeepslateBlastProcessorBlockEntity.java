package net.myriantics.klaxon.block.customblocks.blast_processor.deepslate;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.*;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;
import net.myriantics.klaxon.api.behavior.BlastProcessorBehavior;
import net.myriantics.klaxon.registry.KlaxonBlockEntities;
import net.myriantics.klaxon.networking.packets.BlastProcessorScreenSyncPacket;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeData;
import net.myriantics.klaxon.recipe.item_explosion_power.ItemExplosionPowerData;
import net.myriantics.klaxon.util.BlockDirectionHelper;
import net.myriantics.klaxon.util.ImplementedInventory;
import org.jetbrains.annotations.Nullable;

import static net.myriantics.klaxon.block.customblocks.blast_processor.deepslate.DeepslateBlastProcessorBlock.*;

public class DeepslateBlastProcessorBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory, SidedInventory {
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
    public static final int INGREDIENT_INDEX = 0;
    public static final int CATALYST_INDEX = 1;
    private static final int[] INGREDIENT_ITEM_SLOTS = new int[]{INGREDIENT_INDEX};
    private static final int[] CATALYST_ITEM_SLOTS = new int[]{CATALYST_INDEX};
    public static final int MaxItemStackCount = 1;

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
            // if it's the sides, you can insert into fuel
            Direction blockFacing = world.getBlockState(pos).get(HORIZONTAL_FACING);
            if (side == BlockDirectionHelper.getLeft(blockFacing) || side == BlockDirectionHelper.getRight(blockFacing)) {
                return CATALYST_ITEM_SLOTS;
            }
            // if it's not the front, you can access input
            if (side != BlockDirectionHelper.getFront(blockFacing)) {
                return INGREDIENT_ITEM_SLOTS;
            }
        }
        return new int[] {};
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        // if the slot you want to access is available for the side you're accessing from, check if the item is valid
        // for that stack
        int[] availableSlots = getAvailableSlots(dir);

        if (availableSlots == null || stack.isEmpty() || availableSlots[0] == -1) {
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
            int[] availableSlots = getAvailableSlots(dir);

            // null protection go brrr
            if (availableSlots == null || availableSlots[0] == -1) {
                return false;
            }

            for (int checkedSlotIndex : availableSlots) {
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

        if (world != null && !world.isClient) {

            // default to true so that it shows the particles when dispensing nothing
            boolean shouldRunDispenserEffects = true;

            if (!this.isEmpty()) {

                // calculate behavior to use
                BlastProcessorBehavior blastProcessorBehavior = BEHAVIORS.get(this.inventory.get(CATALYST_INDEX).getItem());

                // inventory. yay.
                Inventory recipeInventory = this;

                // get recipe data
                ItemExplosionPowerData powerData = blastProcessorBehavior.getExplosionPowerData(world, pos, this, recipeInventory);
                BlastProcessingRecipeData processingData = blastProcessorBehavior.getBlastProcessingRecipeData(world, pos, this, recipeInventory, powerData);

                // do explosion effect
                blastProcessorBehavior.onExplosion(world, pos, this, powerData);

                // eject recipe results
                blastProcessorBehavior.ejectItems(world, pos, this, processingData, powerData);

                shouldRunDispenserEffects = blastProcessorBehavior.shouldRunDispenserEffects(world, pos, this, recipeInventory);
            }

            // if this has been exploded, dont run these
            if (shouldRunDispenserEffects && !this.isRemoved()) {
                world.emitGameEvent(GameEvent.BLOCK_ACTIVATE, pos, GameEvent.Emitter.of(world.getBlockState(pos)));
                world.syncWorldEvent(WorldEvents.DISPENSER_DISPENSES, pos, 0);

                // display particles if not front obstructed
                if (!isFrontObstructed(world, pos)) {
                    world.syncWorldEvent(WorldEvents.DISPENSER_ACTIVATED, pos, world.getBlockState(pos).get(HORIZONTAL_FACING).getId());
                }
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

    public Position getExplosionOutputLocation(Direction direction) {
        return getItemOutputLocation(direction, 0.6);
    }

    public Position getItemOutputLocation(Direction direction) {
        return getItemOutputLocation(direction, 0.7);
    }

    public Position getItemOutputLocation(Direction direction, double offset) {
        Position centerPos = pos.toCenterPos();
        double x = centerPos.getX();
        double y = centerPos.getY();
        double z = centerPos.getZ();

        switch (direction) {
            case UP -> y += 0.7;
            case DOWN -> y -= 0.7;
            case NORTH -> z -= 0.7;
            case SOUTH -> z += 0.7;
            case EAST -> x += 0.7;
            case WEST -> x -= 0.7;
        }

        return new Vec3d(x, y, z);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf buf) {
        BlastProcessingRecipeData blastProcessingRecipeData = screenHandler.getBlastProcessingData();
        ItemExplosionPowerData itemExplosionPowerData = screenHandler.getPowerData();

        buf.writeDouble(blastProcessingRecipeData.explosionPowerMin());
        buf.writeDouble(blastProcessingRecipeData.explosionPowerMax());
        buf.writeItemStack(blastProcessingRecipeData.result());
        buf.writeDouble(itemExplosionPowerData.explosionPower());
        buf.writeBoolean(itemExplosionPowerData.producesFire());
    }
}
