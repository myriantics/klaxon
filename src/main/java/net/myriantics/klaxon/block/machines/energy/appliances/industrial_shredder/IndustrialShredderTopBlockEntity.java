package net.myriantics.klaxon.block.machines.energy.appliances.industrial_shredder;

import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.myriantics.klaxon.recipe.shredding.industrial.IndustrialShreddingRecipe;
import net.myriantics.klaxon.recipe.shredding.industrial.IndustrialShreddingRecipeInput;
import net.myriantics.klaxon.registry.block.KlaxonBlockEntityTypes;
import net.myriantics.klaxon.registry.dynamic.KlaxonDamageTypes;
import net.myriantics.klaxon.registry.misc.KlaxonNBTIds;
import net.myriantics.klaxon.registry.recipe.KlaxonRecipeTypes;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;
import net.myriantics.klaxon.util.KlaxonItemStackHelper;
import net.myriantics.klaxon.util.storage.energy.KlaxonEnergyStorageProvider;
import net.myriantics.klaxon.util.storage.item.ContainerPartition;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;

import java.util.Objects;

public class IndustrialShredderTopBlockEntity extends BaseIndustrialShredderBlockEntity implements KlaxonEnergyStorageProvider {

    private static final AABB SUCK_AABB = Block.box(0, 0, 0, 16, EntityType.ITEM.getHeight() * 16, 16).toAabbs().getFirst();
    protected static final int INTAKE_INTERACTION_COOLDOWN_TICKS = 8;
    protected static final int MAX_COUNT_FOR_INTAKE_OPERATION = 4;

    protected @Nullable IndustrialShredderBottomBlockEntity counterpartCache = null;
    protected ContainerPartition shreddingInput;
    protected EnergyStorage energyStorage = new SimpleEnergyStorage(1000, 32, 32);
    protected @Nullable Storage<ItemVariant> aboveStorageCache = null;
    protected boolean cacheInitialized = false;

    protected int intakeInteractionCooldownTicks = 0;
    protected int shreddingProgress = 0;
    protected int shreddingTotalTime = 0;
    protected NonNullList<ItemStack> jammedStacks = NonNullList.withSize(9, ItemStack.EMPTY);

    private final RecipeManager.CachedCheck<IndustrialShreddingRecipeInput, ? extends IndustrialShreddingRecipe> quickCheck;

    public IndustrialShredderTopBlockEntity(BlockPos pos, BlockState state) {
        this(KlaxonBlockEntityTypes.INDUSTRIAL_SHREDDER_TOP.value(), KlaxonRecipeTypes.INDUSTRIAL_SHREDDING.value(), pos, state);
    }

    protected IndustrialShredderTopBlockEntity(BlockEntityType<?> type, RecipeType<? extends IndustrialShreddingRecipe> recipeType, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        this.quickCheck = RecipeManager.createCheck(recipeType);
    }

    @Override
    protected ContainerPartition getAutomationAccessiblePartition() {
        return this.shreddingInput;
    }

    @Override
    protected @Nullable BaseIndustrialShredderBlockEntity getCounterpart() {
        if (this.counterpartCache == null) {
            if (this.level != null && this.level.getBlockEntity(this.worldPosition.below()) instanceof IndustrialShredderBottomBlockEntity be) {
                return this.counterpartCache = be;
            }
        }

        return this.counterpartCache;
    }

    @Override
    protected void initPartitions(PartitionBuilder partitions) {
        this.shreddingInput = partitions.partition(1, (blockEntity, firstOpenSlot, nextClosedSlot) -> new ContainerPartition(blockEntity, firstOpenSlot, nextClosedSlot) {
            @Override
            public void setItem(int slot, ItemStack stack) {
                ItemStack oldStack = this.getFirstNonEmptyStack();
                boolean canSafelyForgoRecomputingRecipeData = !oldStack.isEmpty() && ItemStack.isSameItemSameComponents(oldStack, stack);
                super.setItem(slot, stack);
                if (!canSafelyForgoRecomputingRecipeData) {
                    IndustrialShredderTopBlockEntity.this.shreddingTotalTime = getTotalShreddingTime(IndustrialShredderTopBlockEntity.this.level);
                    IndustrialShredderTopBlockEntity.this.shreddingProgress = 0;
                    IndustrialShredderTopBlockEntity.this.setChanged();
                }
            }
        });
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return null;
    }

    protected boolean isOnCooldown() {
        return this.intakeInteractionCooldownTicks > 0;
    }

    public void serverTick(Level level, BlockPos blockPos, BlockState blockState) {
        if (this.level == null) {
            return;
        }

        if (!this.cacheInitialized) {
            this.aboveStorageCache = ItemStorage.SIDED.find(level, blockPos.above(), Direction.DOWN);
        }

        boolean changed = false;

        ItemStack inputStack = this.shreddingInput.getFirstNonEmptyStack();

        if (this.isOnCooldown()) {
            this.intakeInteractionCooldownTicks--;
            changed = true;
        } else if (!this.jammedStacks.isEmpty()) {
            // don't do intake ops when jammed
        } else if (this.aboveStorageCache != null) {
            if (this.aboveStorageCache.supportsExtraction()) {
                try (Transaction tx = Transaction.openOuter()) {
                    int totalIntakeCount = 0;
                    for (StorageView<ItemVariant> view : this.aboveStorageCache.nonEmptyViews()) {
                        ItemVariant resource = view.getResource();
                        int intakeCount = Math.toIntExact(this.shreddingInput.getStorage().insert(
                                resource,
                                view.extract(resource, Math.min(MAX_COUNT_FOR_INTAKE_OPERATION, MAX_COUNT_FOR_INTAKE_OPERATION - totalIntakeCount), tx),
                                tx
                        ));
                        totalIntakeCount += intakeCount;
                    }
                    if (totalIntakeCount > 0) {
                        tx.commit();
                        changed = true;
                    } else {
                        tx.abort();
                    }
                }
            }
        } else if (!this.areEntityInteractionsBlockedByState(level, blockPos.above(), level.getBlockState(blockPos.above()))) {
            int totalIntakeCount = 0;
            DamageSource shredding = this.level.damageSources().source(KlaxonDamageTypes.SHREDDING);
            for (Entity entity : level.getEntities((Entity) null, SUCK_AABB.move(this.worldPosition).move(0, 1, 0), entity -> entity.getY() == this.worldPosition.getY() + 1)) {
                if (entity instanceof ItemEntity itemEntity && inputStack.getCount() < inputStack.getMaxStackSize()) {
                    try (Transaction tx = Transaction.openOuter()) {
                        ItemStack entityStack = itemEntity.getItem();
                        int intakeCount = this.tryInsert(entityStack, totalIntakeCount, tx);
                        if (intakeCount > 0) {
                            tx.commit();
                            totalIntakeCount += intakeCount;
                            if (entityStack.isEmpty()) {
                                itemEntity.discard();
                            } else {
                                // do this so that the item entity re-syncs its synced data w clients
                                // has to be copied so that update doesnt get culled because its the same instance
                                itemEntity.setItem(entityStack.copy());
                            }
                        } else {
                            tx.abort();
                        }
                    }
                } else {
                    entity.hurt(shredding, 5);
                }
            }
            if (totalIntakeCount > 0) {
                changed = true;
            }
            this.intakeInteractionCooldownTicks = INTAKE_INTERACTION_COOLDOWN_TICKS;
        }


        if (!inputStack.isEmpty()) {
            IndustrialShreddingRecipeInput input = new IndustrialShreddingRecipeInput(inputStack, this.level.getRandom());
            @Nullable RecipeHolder<? extends IndustrialShreddingRecipe> recipeHolder = this.quickCheck.getRecipeFor(input, this.level).orElse(null);

            if (recipeHolder != null) {
                this.shreddingProgress++;
                if (this.shreddingProgress >= this.shreddingTotalTime) {
                    this.shreddingProgress = 0;
                    this.shreddingTotalTime = this.getTotalShreddingTime(this.level);

                    ItemStack[] assembledStacks = recipeHolder.value().properlyAssemble(input, this.level.registryAccess());

                    Storage<ItemVariant> counterpartStorage = Objects.requireNonNull(this.getCounterpart()).getAutomationAccessiblePartition().getStorage();

                    try (Transaction tx = Transaction.openOuter()) {
                        for (ItemStack stack : assembledStacks) {
                            ItemVariant variant = ItemVariant.of(stack);
                            long insertedCount = counterpartStorage.insert(variant, stack.getCount(), tx);
                            if (stack.getCount() - insertedCount != 0) {
                                stack.shrink(Math.toIntExact(insertedCount));
                                this.addJammedStack(stack);
                            }
                        }
                        tx.commit();
                    }

                    inputStack.shrink(1);
                }
                changed = true;
            }
        }

        if (changed) {
            this.setChanged();
        }
    }

    protected boolean areEntityInteractionsBlockedByState(Level level, BlockPos pos, BlockState state) {
        return state.isFaceSturdy(level, pos, Direction.DOWN) && !state.is(KlaxonBlockTags.DOES_NOT_BLOCK_INDUSTRIAL_SHREDDER_ENTITY_INTERACTION);
    }

    public int tryInsert(ItemStack stack, int previouslyInserted, Transaction tx) {
        if (stack.isEmpty()) {
            return 0;
        }

        ItemVariant variant = ItemVariant.of(stack);
        int inserted = Math.toIntExact(this.shreddingInput.getStorage().insert(variant, Math.min(stack.getCount(), MAX_COUNT_FOR_INTAKE_OPERATION - previouslyInserted), tx));
        if (inserted > 0) {
            stack.shrink(inserted);
            return inserted;
        } else {
        }
        return 0;
    }

    protected void addJammedStack(ItemStack stack) {
        KlaxonItemStackHelper.insertAndMerge(this.jammedStacks, stack);
    }

    protected void degradeStackRelativeToShreddingProgress(ItemStack toDegrade, ItemStack beforeDegrading) {

    }

    protected int getTotalShreddingTime(Level level) {
        IndustrialShreddingRecipeInput recipeInput = new IndustrialShreddingRecipeInput(this.shreddingInput.getFirstNonEmptyStack(), Objects.requireNonNull(this.level).getRandom());
        return this.quickCheck
                .getRecipeFor(recipeInput, level)
                .map(recipeHolder -> recipeHolder.value().getTotalShreddingTime())
                .orElse(200);
    }

    protected Direction getFacing() {
        return this.getBlockState().getValue(IndustrialShredderTopBlock.FACING);
    }

    @Override
    public @Nullable EnergyStorage getEnergyStorageForSide(@Nullable Direction direction) {
        return direction == this.getFacing().getOpposite() ? this.energyStorage : null;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.shreddingTotalTime = tag.getInt(KlaxonNBTIds.SHREDDING_TIME_TOTAL);
        this.shreddingProgress = Math.clamp(tag.getInt(KlaxonNBTIds.SHREDDING_TIME), 0, this.shreddingTotalTime);
        this.intakeInteractionCooldownTicks = Math.clamp(tag.getInt(KlaxonNBTIds.COOLDOWN_TICKS), 0, INTAKE_INTERACTION_COOLDOWN_TICKS);
        Objects.requireNonNull(this.level);
        if (tag.contains(KlaxonNBTIds.JAMMED_STACKS)) {
            ContainerHelper.loadAllItems(tag.getCompound(KlaxonNBTIds.JAMMED_STACKS), this.jammedStacks, this.level.registryAccess());
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt(KlaxonNBTIds.SHREDDING_TIME, this.shreddingProgress);
        tag.putInt(KlaxonNBTIds.SHREDDING_TIME_TOTAL, this.shreddingTotalTime);
        tag.putInt(KlaxonNBTIds.COOLDOWN_TICKS, this.intakeInteractionCooldownTicks);
        Objects.requireNonNull(this.level);
        tag.put(KlaxonNBTIds.JAMMED_STACKS, ContainerHelper.saveAllItems(new CompoundTag(), this.jammedStacks, this.level.registryAccess()));
    }
}
