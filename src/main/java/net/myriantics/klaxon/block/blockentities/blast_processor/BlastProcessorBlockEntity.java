package net.myriantics.klaxon.block.blockentities.blast_processor;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.myriantics.klaxon.block.KlaxonBlockEntities;
import net.myriantics.klaxon.block.customblocks.BlastProcessorBlock;
import net.myriantics.klaxon.recipes.blast_processor.BlastProcessorRecipe;
import net.myriantics.klaxon.recipes.item_explosion_power.ItemExplosionPowerRecipe;
import net.myriantics.klaxon.util.ImplementedInventory;
import net.myriantics.klaxon.util.KlaxonTags;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class BlastProcessorBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, ImplementedInventory, SidedInventory {
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
    public static final int PROCESS_ITEM_INDEX = 0;
    public static final int CATALYST_INDEX = 1;
    private static final int[] PROCESS_ITEM_SLOTS = new int[]{PROCESS_ITEM_INDEX};
    private static final int[] CATALYST_ITEM_SLOTS = new int[]{CATALYST_INDEX};
    public static final int MaxItemStackCount = 1;

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
        player.sendMessage(Text.literal("blast_power: " + getItemExplosionPower()));
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

    public void tick(World world, BlockPos pos, BlockState state1) {

    }

    private Optional<ItemExplosionPowerRecipe> getExplosionPowerData() {
        RecipeType<ItemExplosionPowerRecipe> type = ItemExplosionPowerRecipe.Type.INSTANCE;
        SimpleInventory simpleInventory = new SimpleInventory(1);
        simpleInventory.addStack(inventory.get(CATALYST_INDEX));

        Optional<ItemExplosionPowerRecipe> match = world.getRecipeManager().getFirstMatch(type, simpleInventory, world);

        return match;
    }

    private float getItemExplosionPower() {
        Optional<ItemExplosionPowerRecipe> match = getExplosionPowerData();
        if (match.isEmpty()) {
            return 0.0F;
        }
        return match.get().getExplosionPower();
    }

    public void onRedstoneImpulse() {
        world.getServer().sendMessage(Text.literal("shit"));
        detonate();

        //world.setBlockState(pos, state.with(BlastChamberBlock.LIT, true));
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
        /*return (slot == PROCESS_ITEM_INDEX || (slot == CATALYST_INDEX && isValidCatalyst(stack)))
                && this.getStack(slot).isEmpty()
                && stack.getCount() == 1;*/
        // gotta love hacky fixes (maxcountperstack wasnt working)
        return this.isValid(slot, stack);
    }
    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot != CATALYST_INDEX;
    }

    // hi are u a fuel

    private boolean isValidCatalyst(ItemStack stack) {
        return stack.isIn(KlaxonTags.Items.BLAST_CHAMBER_FUEL_REGULAR) ||
                stack.isIn(KlaxonTags.Items.BLAST_CHAMBER_FUEL_SUPER) ||
                stack.isIn(KlaxonTags.Items.BLAST_CHAMBER_FUEL_HYPER);
    }

    // you can still put invalid items in the slots, but they won't do anything
    // could be changed

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return this.getStack(slot).isEmpty();
    }

    private void craft(World world, BlockPos pos) {
        RecipeType<BlastProcessorRecipe> type = BlastProcessorRecipe.Type.INSTANCE;
        double explosionPower = getItemExplosionPower();
        SimpleInventory simpleInventory = new SimpleInventory(inventory.get(PROCESS_ITEM_INDEX), inventory.get(CATALYST_INDEX));

        Optional<BlastProcessorRecipe> match = world.getRecipeManager().getFirstMatch(type, simpleInventory, world);

        if (match.isEmpty() || explosionPower < match.get().getExplosionPowerMin()) {
            eject(inventory.get(PROCESS_ITEM_INDEX));
        }

        if (explosionPower >= match.get().getExplosionPowerMax()) {

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

    private void detonate() {
        RecipeType<ItemExplosionPowerRecipe> type = ItemExplosionPowerRecipe.Type.INSTANCE;
        SimpleInventory simpleInventory = new SimpleInventory(1);
        simpleInventory.addStack(inventory.get(CATALYST_INDEX).copy());

        Optional<ItemExplosionPowerRecipe> match = world.getRecipeManager().getFirstMatch(type, simpleInventory, world);

        float explosionPower;

        if (match.isEmpty()) {
             explosionPower = 20.0F;
        } else {
            explosionPower = match.get().getExplosionPower();
        }

        if (true) {
            world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), explosionPower, World.ExplosionSourceType.BLOCK);
            this.setStack(CATALYST_INDEX, ItemStack.EMPTY);
            this.markDirty();
        }
    }

    private void eject(ItemStack itemStack) {

    }
}
