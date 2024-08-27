package net.myriantics.klaxon.block.blockentities.blast_processor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.myriantics.klaxon.block.KlaxonBlockEntities;
import net.myriantics.klaxon.block.KlaxonBlocks;
import net.myriantics.klaxon.block.customblocks.BlastProcessorBlock;
import net.myriantics.klaxon.networking.KlaxonS2CPacketSender;
import net.myriantics.klaxon.recipes.blast_processing.BlastProcessingInator;
import net.myriantics.klaxon.recipes.blast_processing.BlastProcessorOutputState;
import net.myriantics.klaxon.util.BlockDirectionHelper;
import net.myriantics.klaxon.util.ImplementedInventory;
import net.myriantics.klaxon.util.ItemExplosionPowerHelper;
import org.jetbrains.annotations.Nullable;

import static net.myriantics.klaxon.block.customblocks.BlastProcessorBlock.FACING;
import static net.myriantics.klaxon.block.customblocks.BlastProcessorBlock.LIT;

public class BlastProcessorBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory, SidedInventory {
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
    public static final int PROCESS_ITEM_INDEX = 1;
    public static final int CATALYST_INDEX = 0;
    private static final int[] PROCESS_ITEM_SLOTS = new int[]{PROCESS_ITEM_INDEX};
    private static final int[] CATALYST_ITEM_SLOTS = new int[]{CATALYST_INDEX};
    public static final int MaxItemStackCount = 1;
    public static final double MAXIMUM_CONTAINED_EXPLOSION_POWER = 0.5;

    private BlastProcessorScreenHandler screenHandler;

    public BlastProcessorBlockEntity(BlockPos pos, BlockState state) {
        super(KlaxonBlockEntities.BLAST_PROCESSOR_BLOCK_ENTITY, pos, state);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        this.screenHandler = new BlastProcessorScreenHandler(syncId, playerInventory, this, ScreenHandlerContext.create(world, pos));
        screenHandler.onContentChanged(this);
        return screenHandler;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, this.inventory);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.inventory);
        markDirty();
    }

    public int size() {
        return 2;
    }

    public void onRedstoneImpulse() {
        craft();
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
            Direction blockFacing = world.getBlockState(pos).get(Properties.FACING);
            if (dir == BlockDirectionHelper.getLeft(blockFacing) || dir == BlockDirectionHelper.getRight(blockFacing)) {
                for (int i = 0; i < CATALYST_ITEM_SLOTS.length; i++) {
                    if (slot == CATALYST_ITEM_SLOTS[i]) {
                        return true;
                    }
                }
            }

            if(dir == Direction.UP || dir == Direction.DOWN) {
                for (int i = 0; i < PROCESS_ITEM_SLOTS.length; i++) {
                    if (slot == PROCESS_ITEM_SLOTS[i]) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        if (slot == PROCESS_ITEM_INDEX) {
            return getStack(slot).isEmpty();
        } else if (slot == CATALYST_INDEX) {
            return getStack(slot).isEmpty() && ItemExplosionPowerHelper.isValidCatalyst(world, stack);
        } else {
            return false;
        }
    }

    private void craft() {
        if (world != null) {
            if (!inventory.isEmpty()) {
                SimpleInventory blastProcessorRecipeInventory = new SimpleInventory(size());

                for (int i = 0; i < inventory.size(); i++) {
                    blastProcessorRecipeInventory.setStack(i, inventory.get(i));
                }

                BlastProcessingInator inator = new BlastProcessingInator(world, blastProcessorRecipeInventory);

                BlastProcessorOutputState outputState = inator.getOutputState();
                double explosionPower = inator.getExplosionPower();
                boolean producesFire = inator.producesFire();

                detonate(outputState, explosionPower, producesFire);

                // really ought to make this better but its fine rn
                switch (outputState) {
                    case MISSING_RECIPE, UNDERPOWERED, MISSING_FUEL -> {
                        ejectItem(getStack(PROCESS_ITEM_INDEX));
                        removeStack(PROCESS_ITEM_INDEX);
                        if (explosionPower <= 0) {
                            ejectItem(getStack(CATALYST_INDEX));
                            removeStack(CATALYST_INDEX);
                        } else {
                            removeStack(CATALYST_INDEX);
                        }
                    }
                    case OVERPOWERED -> {
                        clear();
                    }
                    case SUCCESS -> {
                        clear();
                        ejectItem(inator.getResult());
                    }
                }
            }
            world.syncWorldEvent(WorldEvents.DISPENSER_DISPENSES, pos, 0);
            world.syncWorldEvent(WorldEvents.DISPENSER_ACTIVATED, pos, world.getBlockState(pos).get(FACING).getId());
        }
    }

    public void updateBlockState(@Nullable BlockState appendedState) {
        if (world != null && world.getBlockState(pos).getBlock() instanceof BlastProcessorBlock blastProcessorBlock) {
            blastProcessorBlock.updateBlockState(world, pos, appendedState);
        }
    }


    @Override
    public void markDirty() {
        if (this.screenHandler != null && this.screenHandler.player.currentScreenHandler.syncId == this.screenHandler.syncId) {
            screenHandler.onContentChanged(this);
        }
        updateBlockState(null);
        if (world != null && !world.isClient) {
            KlaxonS2CPacketSender.sendFastInputSyncData(world, pos, inventory);
        }
        super.markDirty();
    }

    private void detonate(BlastProcessorOutputState outputState, double explosionPower, boolean producesFire) {
        if (world != null) {
            BlockState activeBlockState = world.getBlockState(pos);
            if (activeBlockState.getBlock().equals(KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR)) {
                if (explosionPower > 0.0) {
                    Direction direction = activeBlockState.get(FACING);
                    Position position = this.getOutputLocation(direction);

                    removeStack(CATALYST_INDEX);
                    world.createExplosion(null, position.getX(), position.getY(), position.getZ(), (float) explosionPower, producesFire, World.ExplosionSourceType.BLOCK);
                    world.updateNeighbors(pos, KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR);
                }
            }
        }
    }

    private void ejectItem(ItemStack itemStack) {
        if (world == null || itemStack.isEmpty()) {
            return;
        }
        Direction direction = world.getBlockState(pos).get(BlastProcessorBlock.FACING);
        ItemDispenserBehavior.spawnItem(world, itemStack, 8, direction, getOutputLocation(direction));
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
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        BlastProcessingInator inator = screenHandler.getInator();
        buf.writeDouble(inator.getExplosionPower());
        buf.writeDouble(inator.getExplosionPowerMin());
        buf.writeDouble(inator.getExplosionPowerMax());
        buf.writeBoolean(inator.producesFire());
        buf.writeEnumConstant(inator.getOutputState());
    }

    @Environment(EnvType.CLIENT)
    public void syncInventory(DefaultedList<ItemStack> stacks) {
        for (int i = 0; i < size(); i++) {
            this.setStack(i, stacks.get(i));
        }
    }
}
