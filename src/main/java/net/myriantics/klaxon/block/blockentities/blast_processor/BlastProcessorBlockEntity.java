package net.myriantics.klaxon.block.blockentities.blast_processor;

import net.minecraft.block.BlockState;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.myriantics.klaxon.block.KlaxonBlockEntities;
import net.myriantics.klaxon.block.KlaxonBlocks;
import net.myriantics.klaxon.block.customblocks.BlastProcessorBlock;
import net.myriantics.klaxon.recipes.blast_processing.BlastProcessorRecipe;
import net.myriantics.klaxon.util.BlockDirectionHelper;
import net.myriantics.klaxon.util.ImplementedInventory;
import net.myriantics.klaxon.util.ItemExplosionPowerHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class BlastProcessorBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, ImplementedInventory, SidedInventory {
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
    public static final int PROCESS_ITEM_INDEX = 0;
    public static final int CATALYST_INDEX = 1;
    private static final int[] PROCESS_ITEM_SLOTS = new int[]{PROCESS_ITEM_INDEX};
    private static final int[] CATALYST_ITEM_SLOTS = new int[]{CATALYST_INDEX};
    public static final int MaxItemStackCount = 1;
    public static final double MAXIMUM_CONTAINED_EXPLOSION_POWER = 0.5;

    public BlastProcessorBlockEntity(BlockPos pos, BlockState state) {
        super(KlaxonBlockEntities.BLAST_CHAMBER_BLOCK_ENTITY, pos, state);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new BlastProcessorScreenHandler(syncId, playerInventory, this, ScreenHandlerContext.create(world, pos));
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
        craft(world);
        updateBlockState();
        /*if (world != null && !world.isClient) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBlockPos(pos);
            for (ItemStack stack : inventory) {
                buf.writeItemStack(stack);
            }
            for (ServerPlayerEntity playerEntity : PlayerLookup.tracking(this)) {
                ServerPlayNetworking.send(playerEntity, KlaxonMessages.FAST_INPUT_SYNC_S2C, buf);
            }
            world.getServer().sendMessage(Text.literal("sent packet"));
        }*/
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
            if(side == Direction.UP) {
                return PROCESS_ITEM_SLOTS;
            }
        }
        return null;
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
        return slot != CATALYST_INDEX;
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        if (slot == 0) {
            return getStack(slot).isEmpty();
        } else if (slot == 1) {
            return getStack(slot).isEmpty() && ItemExplosionPowerHelper.isValidCatalyst(world, stack);
        } else {
            return false;
        }
    }

    private void craft(World world) {
        if (world != null) {

            SimpleInventory blastProcessorRecipeInventory = new SimpleInventory(size());

            for (ItemStack itemStack : inventory) {
                blastProcessorRecipeInventory.addStack(itemStack);
            }

            RecipeType<BlastProcessorRecipe> blastProcessorRecipeType = BlastProcessorRecipe.Type.INSTANCE;
            Optional<BlastProcessorRecipe> blastProcessorMatch = world.getRecipeManager().getFirstMatch(blastProcessorRecipeType, blastProcessorRecipeInventory, world);

            BlastProcessorRecipe.outputState outputState;
            double explosionPower = 0;
            boolean producesFire = false;

            if (blastProcessorMatch.isEmpty()) {
                outputState = BlastProcessorRecipe.outputState.MISSING_RECIPE;
            } else {
                outputState = blastProcessorMatch.get().getOutputState();
                explosionPower = blastProcessorMatch.get().getExplosionPower();
                producesFire = blastProcessorMatch.get().producesFire();
            }

            detonate(explosionPower, producesFire);

            switch (outputState) {
                case MISSING_RECIPE, UNDERPOWERED, MISSING_FIRE, MISSING_FUEL -> {
                    for (ItemStack itemStack : inventory) {
                        dispense(itemStack);
                    }
                }
                case OVERPOWERED -> {
                    clear();
                }
                case SUCCESS -> {
                    dispense(blastProcessorMatch.get().getOutput(world.getRegistryManager()));
                }
            }

        }
    }

    public void updateBlockState() {
        BlastProcessorBlock blastProcessorBlock = (BlastProcessorBlock) world.getBlockState(pos).getBlock();
        blastProcessorBlock.updateBlockState(world, pos, null);
    }


    @Override
    public void markDirty() {
        updateBlockState();
        super.markDirty();
    }

    private void detonate(double explosionPower, boolean producesFire) {
        removeStack(CATALYST_INDEX);
        if (world == null || explosionPower <= MAXIMUM_CONTAINED_EXPLOSION_POWER) {
            return;
        }

        Direction direction = world.getBlockState(pos).get(BlastProcessorBlock.FACING);
        Position position = this.getOutputLocation(direction);

        world.createExplosion(null, position.getX(), position.getY(), position.getZ(), (float) explosionPower, producesFire, World.ExplosionSourceType.BLOCK);
        world.setBlockState(pos, world.getBlockState(pos).with(BlastProcessorBlock.LIT, true));
        // noticed pressure plate hanging around midair in some cases after an explosion - this should fix that
        world.updateNeighbor(pos, KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR, pos);
    }

    private void dispense(ItemStack itemStack) {
        if (world == null || itemStack.isEmpty()) {
            return;
        }
        ItemStack splitStack = itemStack.split(1);
        Direction direction = world.getBlockState(pos).get(BlastProcessorBlock.FACING);
        ItemDispenserBehavior.spawnItem(world, splitStack, 0, direction, getOutputLocation(direction));
    }

    private Position getOutputLocation(Direction direction) {
        Position centerPos = pos.toCenterPos();
        double x = centerPos.getX();
        double y = centerPos.getY();
        double z = centerPos.getZ();

        switch (direction) {
            case UP -> y += 0.55;
            case DOWN -> y -= 0.55;
            case NORTH -> z -= 0.55;
            case SOUTH -> z += 0.55;
            case EAST -> x += 0.55;
            case WEST -> x -= 0.55;
        }

        return new Vec3d(x, y, z);
    }

    public void sendDebugMessage(String message) {
        if (world != null && world.getServer() != null) {
            world.getServer().sendMessage(Text.literal(message));
        }
    }
}
