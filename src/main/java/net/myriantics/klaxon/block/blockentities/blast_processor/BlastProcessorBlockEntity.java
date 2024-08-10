package net.myriantics.klaxon.block.blockentities.blast_processor;

import net.minecraft.block.BlockState;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.myriantics.klaxon.block.KlaxonBlockEntities;
import net.myriantics.klaxon.block.KlaxonBlocks;
import net.myriantics.klaxon.block.customblocks.BlastProcessorBlock;
import net.myriantics.klaxon.recipes.blast_processor.BlastProcessorRecipe;
import net.myriantics.klaxon.recipes.item_explosion_power.ItemExplosionPowerRecipe;
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
        player.sendMessage(Text.literal("is_client: " + player.getWorld().isClient));
        player.sendMessage(Text.literal("bp_maxcountperstack: " + this.getMaxCountPerStack()));
        player.sendMessage(Text.literal("blast_power: " + ItemExplosionPowerHelper.getItemExplosionPower(world, inventory.get(CATALYST_INDEX))));
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
        if(side == Direction.UP || side == Direction.DOWN) {
            return CATALYST_ITEM_SLOTS;
        }
        return PROCESS_ITEM_SLOTS;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return this.isValid(slot, stack);
    }
    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot != CATALYST_INDEX;
    }

    // hi are u a fuel

    // you can still put invalid items in the slots, but they won't do anything
    // could be changed

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return this.getStack(slot).isEmpty();
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

                    if (explosionPower < explosionPowerMin) {
                        detonate(explosionPower, producesFire);
                        ejectItem(processItem);
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
                        detonate(explosionPower, producesFire);
                        ejectCraftingResult(blstProcMatch.get().getOutput(world.getRegistryManager()));
                        sendDebugMessage("RECIPE_SUCCESS");
                        // PRESENT RECIPE AND VALID FUEL
                        // EXPLODE - CONSUME FUEL
                        // EJECT RECIPE OUTPUT
                    }

                } else {
                    detonate(explosionPower, producesFire);
                    ejectItem(processItem);
                    sendDebugMessage("MISSING_RECIPE");
                    // VALID FUEL BUT MISSING RECIPE
                    // CONSUME FUEL AND DISPENSE ITEM
                    // dispense item with velocity correspondent to explosion power
                }
            } else {
                sendDebugMessage("MISSING_FUEL");
                if (!processItem.isEmpty()) {
                    ejectItem(processItem);
                } else if (!catalystItem.isEmpty()) {
                    ejectItem(catalystItem);
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

    // yoinked from dispenser code
    private void ejectItem(ItemStack itemStack) {
        if (world == null) {
            return;
        }
        Direction direction = world.getBlockState(pos).get(BlastProcessorBlock.FACING);
        ItemStack ejectedItem = itemStack.split(1);
        ItemDispenserBehavior.spawnItem(world, ejectedItem, 0, direction, getOutputLocation(direction));
        markDirty();
    }

    private void ejectCraftingResult(ItemStack craftingResult) {
        if (world == null) {
            return;
        }
        Direction direction = world.getBlockState(pos).get(BlastProcessorBlock.FACING);
        ItemDispenserBehavior.spawnItem(world, craftingResult, 0, direction, getOutputLocation(direction));
        markDirty();
    }

    private Position getOutputLocation(Direction direction) {
        if (world == null ) {
            return pos.toCenterPos();
        }
        double d = pos.getX() + 0.7 * (double)direction.getOffsetX();
        double e = pos.getY() + 0.7 * (double)direction.getOffsetY();
        double f = pos.getZ() + 0.7 * (double)direction.getOffsetZ();
        return new PositionImpl(d, e, f);
    }

    private void sendDebugMessage(String message) {
        if (world != null && !world.isClient) {
            world.getServer().sendMessage(Text.literal(message));
        }
    }
}
