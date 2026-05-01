package net.myriantics.klaxon.block.machines.blast_processor.steel;

import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.myriantics.klaxon.block.machines.blast_processor.AbstractBlastProcessorBlockEntity;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystBehavior;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystContext;
import net.myriantics.klaxon.mechanics.muffling.MufflerStorage;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeData;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeInput;
import net.myriantics.klaxon.recipe.explosive_catalyst.ExplosiveCatalystData;
import net.myriantics.klaxon.recipe.explosive_catalyst.ExplosiveCatalystDefinitionRecipeLogic;
import net.myriantics.klaxon.registry.block.KlaxonBlockEntityTypes;
import net.myriantics.klaxon.registry.misc.KlaxonGameRules;
import net.myriantics.klaxon.util.container.ContainerPartition;
import org.jetbrains.annotations.Nullable;

public class SteelBlastProcessorBlockEntity extends AbstractBlastProcessorBlockEntity {

    private static final float POWERFUL_EXPLOSIVE_THRESHOLD = 4.0f;

    private final MufflerStorage mufflerStorage = new MufflerStorage() {
        @Override
        public void onChanged() {
            SteelBlastProcessorBlockEntity.this.updateMufflerState();
        }
    };

    private Storage<ItemVariant> storageCache = null;

    protected SteelBlastProcessorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public SteelBlastProcessorBlockEntity(BlockPos pos, BlockState blockState) {
        super(KlaxonBlockEntityTypes.STEEL_BLAST_PROCESSOR.value(), pos, blockState);
    }

    @Override
    protected void initPartitions(PartitionBuilder partitions) {
        this.ingredientPartition = partitions.partition(1, 4);
        this.catalystPartition = partitions.partition(1, 1);
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return null;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (this.level != null && this.getBlockState().getBlock() instanceof SteelBlastProcessorBlock steelBlastProcessorBlock) {
            steelBlastProcessorBlock.updateMuffler(this.level, this.worldPosition, this);
        }
    }

    @Override
    public Direction getFacing() {
        return this.getBlockState().getValue(SteelBlastProcessorBlock.FACING);
    }

    @Override
    public void redstoneTrigger() {
        Level level = this.level;
        if (level != null && !level.isClientSide()) {
            BlockState state = this.getBlockState();

            if (!this.isEmpty() && state.getBlock() instanceof SteelBlastProcessorBlock block) {
                BlockPos pos = this.getBlockPos();
                Direction facing = this.getFacing();
                ExplosiveCatalystContext.Block context = this.getContext();

                ExplosiveCatalystData powerData = ExplosiveCatalystDefinitionRecipeLogic.computeExplosiveCatalystData(context, this.getCatalystStack());
                ExplosiveCatalystBehavior behavior = powerData.behavior().value();

                // if its on cooldown just kaboom no matter what
                if (block.isExhaustIgnited(level, pos) && !behavior.isNoOp()) {
                    this.selfDestruct(level, pos, context, powerData);
                } else {
                    BlastProcessingRecipeData processingData = this.getCraftedStacks(new BlastProcessingRecipeInput(this.getIngredientStack(), powerData));

                    if (powerData.explosionPower() > 0) {
                        this.catalystPartition.clearContent();
                    }

                    this.storageCache = ItemStorage.SIDED.find(level, pos.relative(facing), facing.getOpposite());
                    this.ejectItems(processingData, powerData);
                    this.storageCache = null;

                    // self destruct if overload handling failed
                    if (powerData.explosionPower() > POWERFUL_EXPLOSIVE_THRESHOLD && !block.handleOverload(level, pos, this, powerData)) {
                        this.selfDestruct(level, pos, context, powerData);
                    }
                }
            }
        }
    }

    private void selfDestruct(Level level, BlockPos pos, ExplosiveCatalystContext.Block context, ExplosiveCatalystData powerData) {
        level.removeBlock(pos, false);
        powerData.behavior().value().createExplosion(context, pos.getCenter(), powerData, level.getGameRules().getBoolean(KlaxonGameRules.BLAST_PROCESSOR_EXPLOSIONS_MODIFY_WORLD));
    }

    @Override
    protected void ejectItem(ItemStack stack, Direction facing) {
        if (this.storageCache != null) {
            try (Transaction tx = Transaction.openOuter()) {
                int count = stack.getCount();
                int inserted = Math.toIntExact(this.storageCache.insert(ItemVariant.of(stack), count, tx));

                if (inserted > 0) {
                    tx.commit();
                    stack.setCount(count - inserted);
                } else {
                    tx.abort();
                }
            }
        }
        super.ejectItem(stack, facing);
    }

    protected boolean insertIntoCachedStorage(ItemStack stack) {
        if (this.storageCache != null) {

        }

        return false;
    }

    public boolean cushionsExplosionWithoutExhaust(float explosionPower) {
        return explosionPower <= POWERFUL_EXPLOSIVE_THRESHOLD;
    }

    @Override
    public Position getItemOutputLocation(Direction facing) {
        Vec3 vec = this.worldPosition.getBottomCenter().add(0, 4d /16, 0);
        Vec3i normal = facing.getNormal();
        return vec.add(normal.getX() * 0.75, normal.getY() * 0.75, normal.getZ() * 0.75);
    }

    @Override
    protected ContainerPartition getAccessForDirection(@Nullable Direction side) {
        Direction facing = this.getFacing();
        if (side == facing.getOpposite() || side == Direction.DOWN) { // if back or down do catalyst
            return this.catalystPartition;
        } else if (side != facing) {
            return this.ingredientPartition;
        } else {
            return ContainerPartition.EMPTY;
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.mufflerStorage.load(tag, registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        this.mufflerStorage.save(tag, registries);
    }

    public void setMuffler(ItemStack newMufflerStack) {
        this.mufflerStorage.set(newMufflerStack);
        this.setChanged();
    }

    protected void updateMufflerState() {
        if (this.level != null && this.getBlockState().getBlock() instanceof SteelBlastProcessorBlock block) {
            block.updateMuffler(this.level, this.getBlockPos(), this);
        }
    }

    public ItemStack getMuffler() {
        return this.mufflerStorage.get();
    }
}
