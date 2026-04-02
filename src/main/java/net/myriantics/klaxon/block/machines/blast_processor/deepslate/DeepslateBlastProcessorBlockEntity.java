package net.myriantics.klaxon.block.machines.blast_processor.deepslate;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.myriantics.klaxon.block.machines.blast_processor.AbstractBlastProcessorBlockEntity;
import net.myriantics.klaxon.mechanics.explosive_catalyst.AbstractExplosiveCatalystBehavior;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystContext;
import net.myriantics.klaxon.networking.s2c.BlastProcessorScreenSyncPacket;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeData;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeInput;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystData;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystDefinitionRecipeInput;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystDefinitionRecipeLogic;
import net.myriantics.klaxon.registry.block.KlaxonBlockEntities;
import net.myriantics.klaxon.registry.misc.KlaxonGameRules;
import net.myriantics.klaxon.tag.klaxon.KlaxonExplosiveCatalystBehaviorTags;
import net.myriantics.klaxon.util.BlockDirectionHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import static net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlock.HORIZONTAL_FACING;
import static net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlock.isFrontObstructed;

public class DeepslateBlastProcessorBlockEntity extends AbstractBlastProcessorBlockEntity implements ExtendedScreenHandlerFactory<BlastProcessorScreenSyncPacket>, WorldlyContainer {
    private NonNullList<ItemStack> inventory = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
    public static final int INGREDIENT_INDEX = 0;
    public static final int CATALYST_INDEX = 1;
    private static final int[] INGREDIENT_ITEM_SLOTS = new int[]{INGREDIENT_INDEX};
    private static final int[] CATALYST_ITEM_SLOTS = new int[]{CATALYST_INDEX};
    public static final int MAX_HELD_STACK_COUNT = 1;

    private final ArrayList<DeepslateBlastProcessorScreenHandler> activeScreenHandlers = new ArrayList<>();

    public DeepslateBlastProcessorBlockEntity(BlockPos pos, BlockState state) {
        super(KlaxonBlockEntities.DEEPSLATE_BLAST_PROCESSOR_BLOCK_ENTITY.value(), pos, state);
        this.inventory = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
    }

    @Override
    protected AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
        DeepslateBlastProcessorScreenHandler screenHandler = new DeepslateBlastProcessorScreenHandler(syncId, playerInventory, this, ContainerLevelAccess.create(level, worldPosition));
        screenHandler.slotsChanged(this);
        activeScreenHandlers.add(screenHandler);
        return screenHandler;
    }

    public void removeScreenHandler(DeepslateBlastProcessorScreenHandler screenHandler) {
        activeScreenHandlers.remove(screenHandler);
    }

    public void updateAllActiveScreenHandlers() {
        for (DeepslateBlastProcessorScreenHandler screenHandler : activeScreenHandlers) {
            screenHandler.slotsChanged(this);
        }
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable(getBlockState().getBlock().getDescriptionId());
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> inventory) {
        this.inventory = inventory;
    }

    public int getContainerSize() {
        return 2;
    }

    @Override
    public int getMaxStackSize() {
        return MAX_HELD_STACK_COUNT;
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        if (level != null) {
            // if it's the sides, you can insert into fuel
            Direction blockFacing = level.getBlockState(worldPosition).getValue(HORIZONTAL_FACING);
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
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction dir) {
        // if the slot you want to access is available for the side you're accessing from, check if the item is valid
        // for that stack
        int[] availableSlots = getSlotsForFace(dir);

        if (availableSlots == null || stack.isEmpty() || availableSlots[0] == -1) {
            return false;
        }

        for (int availableSlot : availableSlots) {
            if (slot == availableSlot) {
                return this.canPlaceItem(slot, stack);
            }
        }
        return false;
    }



    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        if (level != null && !level.hasNeighborSignal(worldPosition)) {

            // get the available slots for the side you're trying to pull from
            int[] availableSlots = getSlotsForFace(dir);

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
    public boolean canPlaceItem(int slot, ItemStack stack) {
        // look through the whole inventory and return true if selected slot is inbounds and empty
        for (int i = 0; i < this.getContainerSize(); i++) {
            if (slot == i) {
                return getItem(slot).isEmpty();
            }
        }
        return false;
    }

    public void onRedstoneImpulse() {
        if (level != null && !level.isClientSide) {

            // default to true so that it shows the particles when dispensing nothing
            boolean shouldRunDispenserEffects = true;

            if (!this.isEmpty()) {
                BlockPos pos = this.getBlockPos();
                ExplosiveCatalystContext.Block context = this.getContext();

                // compute blast processor behavior
                ExplosiveCatalystData data = ExplosiveCatalystDefinitionRecipeLogic.computeExplosiveCatalystData(context, this.getCatalystStack());

                Holder<AbstractExplosiveCatalystBehavior> behavior = data.behavior();

                // transform data if needed
                BlastProcessingRecipeData processingData = this.getBlastProcessingRecipeData(level, worldPosition, this, new BlastProcessingRecipeInput(inventory.get(INGREDIENT_INDEX), data));

                // clear catalyst and do explosion effect
                this.removeItemNoUpdate(CATALYST_INDEX);
                behavior.value().createExplosion(context, this.getExplosionOutputLocation(level.getBlockState(pos).getValue(HORIZONTAL_FACING)), data, this.level.getGameRules().getBoolean(KlaxonGameRules.BLAST_PROCESSOR_EXPLOSIONS_MODIFY_WORLD));

                // eject recipe results
                this.ejectItems(level, worldPosition, this, processingData, data);

                shouldRunDispenserEffects = !behavior.is(KlaxonExplosiveCatalystBehaviorTags.DOES_NOT_RUN_DISPENSER_EFFECTS);
            }

            // if this has been exploded, dont run these
            if (shouldRunDispenserEffects && !this.isRemoved()) {
                level.gameEvent(GameEvent.BLOCK_ACTIVATE, worldPosition, GameEvent.Context.of(level.getBlockState(worldPosition)));
                level.levelEvent(LevelEvent.SOUND_DISPENSER_DISPENSE, worldPosition, 0);

                // display particles if not front obstructed
                if (!isFrontObstructed(level, worldPosition)) {
                    level.levelEvent(LevelEvent.PARTICLES_SHOOT_SMOKE, worldPosition, level.getBlockState(worldPosition).getValue(HORIZONTAL_FACING).get3DDataValue());
                }
            }
        }
    }

    @Override
    public ItemStack getCatalystStack() {
        return this.getItem(CATALYST_INDEX);
    }

    public void updateBlockState(@Nullable BlockState appendedState) {
        if (level != null && level.getBlockState(worldPosition).getBlock() instanceof DeepslateBlastProcessorBlock blastProcessorBlock) {
            blastProcessorBlock.updateBlockState(level, worldPosition, appendedState);
        }
    }


    @Override
    public void setChanged() {
        updateAllActiveScreenHandlers();
        updateBlockState(null);
        super.setChanged();
    }

    public Position getExplosionOutputLocation(Direction facing) {
        return getItemOutputLocation(facing, 0.6);
    }

    public Position getItemOutputLocation(Direction facing) {
        return getItemOutputLocation(facing, 0.7);
    }

    private Position getItemOutputLocation(@Nullable Direction direction, double offset) {
        Position centerPos = worldPosition.getCenter();
        double x = centerPos.x();
        double y = centerPos.y() - 0.3125;
        double z = centerPos.z();

        if (direction != null) {
            switch (direction) {
                case NORTH -> z -= offset;
                case SOUTH -> z += offset;
                case EAST -> x += offset;
                case WEST -> x -= offset;
            }
        }


        return new Vec3(x, y, z);
    }

    @Override
    public BlastProcessorScreenSyncPacket getScreenOpeningData(ServerPlayer player) {
        ExplosiveCatalystDefinitionRecipeInput recipeInventory = new ExplosiveCatalystDefinitionRecipeInput(this);

        // default values if world is null
        ExplosiveCatalystData explosiveCatalystData = ExplosiveCatalystData.ZERO;
        BlastProcessingRecipeData blastProcessingRecipeData = BlastProcessingRecipeData.ZERO;

        // if we have a world, actually yoink the proper values.
        if (level != null) {
            explosiveCatalystData = ExplosiveCatalystDefinitionRecipeLogic.computeExplosiveCatalystData(this.getContext(), this.getCatalystStack());

            blastProcessingRecipeData = this.getBlastProcessingPreviewData(level, worldPosition, this, new BlastProcessingRecipeInput(inventory.get(INGREDIENT_INDEX), explosiveCatalystData));
        }

        return new BlastProcessorScreenSyncPacket(blastProcessingRecipeData.explosionPowerMin(),
                blastProcessingRecipeData.explosionPowerMax(),
                blastProcessingRecipeData.outputStacks(),
                explosiveCatalystData.explosionPower(),
                explosiveCatalystData.producesFire());
    }



    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.loadAdditional(nbt, registryLookup);
        this.inventory = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(nbt)) {
            ContainerHelper.loadAllItems(nbt, this.inventory, registryLookup);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.saveAdditional(nbt, registryLookup);
        if (!this.trySaveLootTable(nbt)) {
            ContainerHelper.saveAllItems(nbt, this.inventory, registryLookup);
        }
    }
}
