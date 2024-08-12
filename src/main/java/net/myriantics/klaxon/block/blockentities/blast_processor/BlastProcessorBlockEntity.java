package net.myriantics.klaxon.block.blockentities.blast_processor;

import net.minecraft.block.BlockState;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.myriantics.klaxon.block.KlaxonBlockEntities;
import net.myriantics.klaxon.block.KlaxonBlocks;
import net.myriantics.klaxon.block.customblocks.BlastProcessorBlock;
import net.myriantics.klaxon.recipes.blast_processor.BlastProcessorRecipe;
import net.myriantics.klaxon.recipes.item_explosion_power.ItemExplosionPowerRecipe;
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
    public static final double MAXIMUM_CONTAINED_EXPLOSION_POWER = 1.0;

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
        return new BlastProcessorScreenHandler(syncId, playerInventory, this);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        //this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        Inventories.readNbt(nbt, this.inventory);
        this.markDirty();
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.inventory);
        this.markDirty();
    }

    public int size() {
        return 2;
    }

    public void onRedstoneImpulse() {
        craft(world);
        markDirty();
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
            return this.getStack(slot).isEmpty();
        } else if (slot == 1) {
            return this.getStack(slot).isEmpty() && ItemExplosionPowerHelper.isValidCatalyst(world, stack);
        } else {
            return false;
        }
    }

    private void craft(World world) {
        if (world != null) {
            ItemStack processItem = inventory.get(PROCESS_ITEM_INDEX);
            ItemStack catalystItem = inventory.get(CATALYST_INDEX);

            RecipeManager recipeManager = world.getRecipeManager();


            Optional<ItemExplosionPowerRecipe> explPowMatch = ItemExplosionPowerHelper.getExplosionPowerData(world, catalystItem);


            // is there fuel present
            if (explPowMatch.isPresent() && explPowMatch.get().getExplosionPower() > 0) {

                RecipeType<BlastProcessorRecipe> blstProcType = BlastProcessorRecipe.Type.INSTANCE;
                SimpleInventory blstProcInventory = new SimpleInventory(processItem.copy());

                Optional<BlastProcessorRecipe> blstProcMatch = recipeManager.getFirstMatch(blstProcType, blstProcInventory, world);

                double explosionPower = explPowMatch.get().getExplosionPower();
                boolean producesFire = explPowMatch.get().producesFire();


                // is there a recipe (EDGE CASE WILL PRODUCE RECIPE CONFLICTS BETWEEN BLAST PROCESSING RECIPES OF THE SAME ITEM)
                // WILL NEED TO FIX LATER
                if (blstProcMatch.isPresent()) {

                    double explosionPowerMin = blstProcMatch.get().getExplosionPowerMin();
                    double explosionPowerMax = blstProcMatch.get().getExplosionPowerMax();
                    boolean requiresFire = blstProcMatch.get().requiresFire();

                    if (explosionPower < explosionPowerMin) {
                        detonate(explosionPower, producesFire);
                        ejectItem(processItem.split(1));
                        sendDebugMessage("LOW_POWERED");
                        // PRESENT RECIPE BUT LOW-POWERED FUEL
                        // EXPLODE - CONSUME FUEL
                        // DISPENSE ITEM WITH VELOCITY BASED OFF OF FUEL EXPL. POWER
                    } else if(explosionPower > explosionPowerMax) {
                        detonate(explosionPower, producesFire);
                        destroyItem(PROCESS_ITEM_INDEX);
                        sendDebugMessage("OVERPOWERED");
                        // PRESENT RECIPE BUT OVERPOWERED FUEL
                        // EXPLODE - CONSUME FUEL
                        // DESTROY ITEM
                    } else {
                        if (requiresFire == producesFire || producesFire) {
                            detonate(explosionPower, producesFire);
                            ejectItem(blstProcMatch.get().getOutput(world.getRegistryManager()));
                            sendDebugMessage("RECIPE_SUCCESS");
                            // PRESENT RECIPE AND VALID FUEL
                            // EXPLODE - CONSUME FUEL
                            // EJECT RECIPE OUTPUT
                        } else {
                            detonate(explosionPower, producesFire);
                            ejectItem(processItem.split(1));
                            sendDebugMessage("INVALID_MISSING_FIRE");
                            // INVALID RECIPE
                            // REQUIRES FIRE
                            // CONSUME FUEL AND EJECT PROCESS ITEM
                        }
                    }

                } else {
                    detonate(explosionPower, producesFire);
                    ejectItem(processItem.split(1));
                    sendDebugMessage("MISSING_RECIPE");
                    // VALID FUEL BUT MISSING RECIPE
                    // CONSUME FUEL AND DISPENSE ITEM
                    // dispense item with velocity correspondent to explosion power
                }
            } else {
                sendDebugMessage("MISSING_FUEL");
                if (!processItem.isEmpty()) {
                    ejectItem(processItem.split(1));
                } else if (!catalystItem.isEmpty()) {
                    ejectItem(processItem.split(1));
                }
                // NO FUEL / INVALID FUEL
                // NEEDS TO CHECK FOR PROCESSING ITEM AND FUEL AND EJECT EITHER
                // dispense item normally - NO FUEL
            }
        }
    }


    @Override
    public void markDirty() {
        if (world != null) {
            if (inventory.get(PROCESS_ITEM_INDEX).isEmpty()) {
                world.setBlockState(pos, world.getBlockState(pos).with(BlastProcessorBlock.HATCH_OPEN, true));
            } else {
                world.setBlockState(pos, world.getBlockState(pos).with(BlastProcessorBlock.HATCH_OPEN, false));
            }

            if (inventory.get(CATALYST_INDEX).isEmpty()) {
                world.setBlockState(pos, world.getBlockState(pos).with(BlastProcessorBlock.FUELED, false));
            } else {
                world.setBlockState(pos, world.getBlockState(pos).with(BlastProcessorBlock.FUELED, true));
            }
        }
        super.markDirty();
    }

    private void detonate(double explosionPower, boolean producesFire) {
        setStack(CATALYST_INDEX, ItemStack.EMPTY);
        markDirty();
        if (world == null || explosionPower <= MAXIMUM_CONTAINED_EXPLOSION_POWER) {
            return;
        }

        Direction direction = world.getBlockState(pos).get(BlastProcessorBlock.FACING);

        Position position = this.getOutputLocation(direction);

        if (position == null) {
            position = new PositionImpl(pos.getX(), pos.getY(), pos.getZ());
        }

        world.createExplosion(null, position.getX(), position.getY(), position.getZ(), (float) explosionPower, producesFire, World.ExplosionSourceType.BLOCK);
        world.setBlockState(pos, world.getBlockState(pos).with(BlastProcessorBlock.LIT, true));
        sendDebugMessage("explosionPower: " + explosionPower + ", " + producesFire);
        sendDebugMessage("x: " + position.getX() + ", y: " + position.getY() + ", z: " + position.getZ());
        // noticed pressure plate hanging around midair in some cases after an explosion - this should fix that
        world.updateNeighbor(pos, KlaxonBlocks.DEEPSLATE_BLAST_PROCESSOR, pos);
    }

    private void destroyItem(int index) {
        setStack(index, ItemStack.EMPTY);
        markDirty();
    }

    private void ejectItem(ItemStack itemStack) {
        if (world == null) {
            return;
        }
        Direction direction = world.getBlockState(pos).get(BlastProcessorBlock.FACING);
        ItemDispenserBehavior.spawnItem(world, itemStack, 0, direction, getOutputLocation(direction));
        markDirty();
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

    private void sendDebugMessage(String message) {
        if (world != null && !world.isClient) {
            world.getServer().sendMessage(Text.literal(message));
        }
    }
}
