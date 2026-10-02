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
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundBlockEventPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;

import java.util.Objects;
import java.util.Optional;

public class IndustrialShredderTopBlockEntity extends BaseIndustrialShredderBlockEntity implements KlaxonEnergyStorageProvider {

    private static final AABB SUCK_AABB = Block.box(0, 0, 0, 16, EntityType.ITEM.getHeight() * 16, 16).toAabbs().getFirst();
    private static final float SHREDDING_PARTICLE_VELOCITY_SCALAR = 0.3f;
    protected static final int INTAKE_INTERACTION_COOLDOWN_TICKS = 4;
    protected static final int MAX_COUNT_FOR_INTAKE_OPERATION = 4;
    public static final int DEFAULT_SHREDDING_TIME = 100;

    public static final int SHREDDING_INPUT_PARTITION_SIZE = 1;
    public static final int CONTAINER_DATA_ACCESS_SIZE = 2;
    public static final int DATA_SHREDDING_PROGRESS = 0;
    public static final int DATA_SHREDDING_TOTAL_TIME = 1;

    protected @Nullable IndustrialShredderBottomBlockEntity counterpartCache = null;
    ContainerPartition shreddingInputPartition;
    protected EnergyStorage energyStorage = new SimpleEnergyStorage(1000, 32, 32);
    protected @Nullable Storage<ItemVariant> aboveStorageCache = null;
    protected boolean cacheInitialized = false;

    protected int intakeInteractionCooldownTicks = 0;
    protected int shreddingProgress = 0;
    protected int shreddingTotalTime = 0;
    protected NonNullList<ItemStack> jammedStacks = NonNullList.withSize(9, ItemStack.EMPTY);
    protected final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case DATA_SHREDDING_PROGRESS -> IndustrialShredderTopBlockEntity.this.shreddingProgress;
                case DATA_SHREDDING_TOTAL_TIME -> IndustrialShredderTopBlockEntity.this.shreddingTotalTime;
                default -> throw new IllegalArgumentException();
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case DATA_SHREDDING_PROGRESS -> IndustrialShredderTopBlockEntity.this.shreddingProgress = value;
                case DATA_SHREDDING_TOTAL_TIME -> IndustrialShredderTopBlockEntity.this.shreddingTotalTime = value;
            }
        }

        @Override
        public int getCount() {
            return CONTAINER_DATA_ACCESS_SIZE;
        }
    };

    private final RecipeManager.CachedCheck<IndustrialShreddingRecipeInput, ? extends IndustrialShreddingRecipe> quickCheck;

    public IndustrialShredderTopBlockEntity(BlockPos pos, BlockState state) {
        this(KlaxonBlockEntityTypes.INDUSTRIAL_SHREDDER_TOP.value(), KlaxonRecipeTypes.INDUSTRIAL_SHREDDING.value(), pos, state);
    }

    protected IndustrialShredderTopBlockEntity(BlockEntityType<?> type, RecipeType<? extends IndustrialShreddingRecipe> recipeType, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        this.quickCheck = RecipeManager.createCheck(recipeType);
    }

    @Override
    protected IndustrialShredderTopBlockEntity getTop() throws IllegalStateException {
        return this;
    }

    @Override
    protected IndustrialShredderBottomBlockEntity getBottom() throws IllegalStateException {
        @Nullable IndustrialShredderBottomBlockEntity cached = (IndustrialShredderBottomBlockEntity) this.getCounterpart();
        if (cached == null) {
            throw new IllegalStateException();
        } else {
            return cached;
        }
    }

    @Override
    protected ContainerPartition getAutomationAccessiblePartition() {
        return this.shreddingInputPartition;
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
        this.shreddingInputPartition = partitions.partition(SHREDDING_INPUT_PARTITION_SIZE, (blockEntity, firstOpenSlot, nextClosedSlot) -> new ContainerPartition(blockEntity, firstOpenSlot, nextClosedSlot) {
            @Override
            public void setItem(int slot, ItemStack stack) {
                ItemStack oldStack = this.getItem(slot);
                boolean canSafelyForgoRecomputingRecipeData = !oldStack.isEmpty() && ItemStack.isSameItemSameComponents(oldStack, stack);
                super.setItem(slot, stack);
                if (!canSafelyForgoRecomputingRecipeData) {
                    IndustrialShredderTopBlockEntity.this.resetShreddingStats();
                    IndustrialShredderTopBlockEntity.this.setChanged();
                }
                if (IndustrialShredderTopBlockEntity.this.getBlockState().getValue(IndustrialShredderTopBlock.ACTIVE) != IndustrialShredderTopBlockEntity.this.isActive()) {
                    IndustrialShredderTopBlockEntity.this.updateActiveState(IndustrialShredderTopBlockEntity.this.level);
                }
            }
        });
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return null;
    }

    protected boolean isIntakeOnCooldown() {
        return this.intakeInteractionCooldownTicks > 0;
    }

    protected boolean isActive() {
        return this.shreddingProgress > 0;
    }

    protected boolean isObstructed() {
        return this.getBlockState().getValue(IndustrialShredderTopBlock.OBSTRUCTED);
    }

    public void clientDisplayTick(Level level, BlockPos pos, BlockState state) {
        RandomSource random = level.getRandom();
        int particlesPerTick = random.nextInt(1, 3);
        Vec3 center = new Vec3(pos.getX() + 0.5, pos.getY() + (17f/16), pos.getZ() + 0.5);
        ItemStack shreddingStack = this.shreddingInputPartition.getFirstNonEmptyStack();
        Direction.Axis facingAxis = state.getValue(IndustrialShredderTopBlock.FACING).getAxis();
        if (shreddingStack.isEmpty()) {
            return;
        }
        for (int i = 0; i < particlesPerTick; i++) {
            this.spawnShreddingParticle(level, facingAxis, center, random, shreddingStack, SHREDDING_PARTICLE_VELOCITY_SCALAR);
        }
    }


    public void spawnShreddingParticle(Level level, Direction.Axis facingAxis, Vec3 center, RandomSource random, ItemStack stack, float verticalScalar) {
        if (level == null || !level.isClientSide()) {
            return;
        }

        double facingOffset = ((random.nextFloat() * 2) - 1) * 7f/16;
        double verticalMotion = (0.3f + (random.nextFloat() * 0.7f)) * ((8f / 16) / 20);
        double perpendicularMotion = random.nextBoolean() ? 7f/16 : -7f/16;
        double randomFacingMotion = ((random.nextFloat() * 2) - 1) * ((2f / 16) / 20);

        double originX;
        double originZ;
        double motionX;
        double motionZ;
        if (facingAxis == Direction.Axis.X) {
            originX = center.x + facingOffset;
            originZ = center.z;
            motionX = randomFacingMotion;
            motionZ = perpendicularMotion;
        } else {
            originX = center.x;
            originZ = center.z + facingOffset;
            motionX = perpendicularMotion;
            motionZ = randomFacingMotion;
        }

        level.addParticle(
                new ItemParticleOption(ParticleTypes.ITEM, stack),
                originX, center.y + (0.05f / 16), originZ,
                motionX * SHREDDING_PARTICLE_VELOCITY_SCALAR, verticalMotion * verticalScalar, motionZ * SHREDDING_PARTICLE_VELOCITY_SCALAR
        );
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (this.level == null) {
            return;
        }

        if (!this.cacheInitialized) {
            this.aboveStorageCache = ItemStorage.SIDED.find(level, pos.above(), Direction.DOWN);
        }

        boolean wasActiveAtTickStart = this.isActive();
        boolean changed = false;

        ItemStack inputStack = this.shreddingInputPartition.getFirstNonEmptyStack();

        if (this.isIntakeOnCooldown()) {
            this.intakeInteractionCooldownTicks--;
            changed = true;
        } else if (this.aboveStorageCache != null) {
            if (this.aboveStorageCache.supportsExtraction()) {
                try (Transaction tx = Transaction.openOuter()) {
                    int totalIntakeCount = 0;
                    for (StorageView<ItemVariant> view : this.aboveStorageCache.nonEmptyViews()) {
                        ItemVariant resource = view.getResource();
                        int intakeCount = Math.toIntExact(this.shreddingInputPartition.getStorage().insert(
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
            this.intakeInteractionCooldownTicks = INTAKE_INTERACTION_COOLDOWN_TICKS;
        } else if (!this.isObstructed()) {
            int totalIntakeCount = 0;
            DamageSource shredding = this.level.damageSources().source(KlaxonDamageTypes.SHREDDING);
            for (Entity entity : level.getEntities((Entity) null, SUCK_AABB.move(this.worldPosition).move(0, 1, 0), entity -> entity.getY() == this.worldPosition.getY() + 1 && !entity.isIgnoringBlockTriggers())) {
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

            if (this.shreddingProgress >= this.shreddingTotalTime) {
                this.resetShreddingStats();
                if (recipeHolder != null) {
                    this.handleRecipeCompletion(recipeHolder, input);
                }

                this.sendBlockEventRightNow((ServerLevel) level, pos, IndustrialShredderTopBlock.ITEM_CONSUMPTION_EVENT_ID, 0);
                // TODO: add generic shred complete sound.
                level.playSound(
                        null,
                        pos,
                        inputStack.getItem() instanceof BlockItem blockItem ? blockItem.getBlock().defaultBlockState().getSoundType().getBreakSound() : SoundEvents.EMPTY,
                        SoundSource.BLOCKS
                );

                // still eat the input stack even if no recipe
                inputStack.shrink(1);
            }
            // we gotta do it like this because unbreaking and the like exists.
            if ((recipeHolder == null || recipeHolder.value().delegatesShreddingTimeToItemDurability()) && inputStack.isDamageableItem()) {
                inputStack.hurtAndBreak(1, (ServerLevel) level, null, (item) -> {
                    this.resetShreddingStats();
                    if (recipeHolder != null) {
                        this.handleRecipeCompletion(recipeHolder, input);
                    }
                    this.sendBlockEventRightNow((ServerLevel) level, pos, IndustrialShredderTopBlock.ITEM_CONSUMPTION_EVENT_ID, 0);
                    level.playSound(null, pos, SoundEvents.ITEM_BREAK, SoundSource.BLOCKS);
                });
                this.shreddingProgress = inputStack.getDamageValue();
            } else {
                this.shreddingProgress++;
            }
            changed = true;
        }

        if (wasActiveAtTickStart != this.isActive()) {
            changed = true;
            this.updateActiveState(level);
        }

        if (changed) {
            this.setChanged();
            this.resyncData(level);
        }
    }

    protected void handleRecipeCompletion(@NotNull RecipeHolder<? extends IndustrialShreddingRecipe> recipeHolder, IndustrialShreddingRecipeInput input) {
        ItemStack[] assembledStacks = recipeHolder.value().properlyAssemble(input, this.level.registryAccess());

        Storage<ItemVariant> counterpartStorage = Objects.requireNonNull(this.getCounterpart()).getAutomationAccessiblePartition().getStorage();

        try (Transaction tx = Transaction.openOuter()) {
            for (ItemStack stack : assembledStacks) {
                if (stack.isEmpty()) {
                    continue;
                }

                ItemVariant variant = ItemVariant.of(stack);
                long insertedCount = counterpartStorage.insert(variant, stack.getCount(), tx);
                if (stack.getCount() - insertedCount != 0) {
                    stack.shrink(Math.toIntExact(insertedCount));
                    this.addJammedStack(stack);
                }
            }
            tx.commit();
        }
    }

    protected void sendBlockEventRightNow(ServerLevel serverLevel, BlockPos pos, int id, int params) {
        serverLevel.getServer()
                .getPlayerList()
                .broadcast(
                        null,
                        pos.getX(),
                        pos.getY(),
                        pos.getZ(),
                        64.0,
                        serverLevel.dimension(),
                        new ClientboundBlockEventPacket(pos, this.getBlockState().getBlock(), id, params)
                );
    }

    protected void updateActiveState(Level level) {
        level.setBlockAndUpdate(this.worldPosition, this.getBlockState().setValue(IndustrialShredderTopBlock.ACTIVE, this.isActive()));
    }

    protected void resyncData(Level level) {
        level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), (Block.UPDATE_ALL_IMMEDIATE));
    }

    protected boolean areEntityInteractionsBlockedByState(Level level, BlockPos pos, BlockState state) {
        return state.isFaceSturdy(level, pos, Direction.DOWN) && !state.is(KlaxonBlockTags.DOES_NOT_BLOCK_INDUSTRIAL_SHREDDER_ENTITY_INTERACTION);
    }

    public int tryInsert(ItemStack stack, int previouslyInserted, Transaction tx) {
        if (stack.isEmpty()) {
            return 0;
        }

        ItemVariant variant = ItemVariant.of(stack);
        int inserted = Math.toIntExact(this.shreddingInputPartition.getStorage().insert(variant, Math.min(stack.getCount(), MAX_COUNT_FOR_INTAKE_OPERATION - previouslyInserted), tx));
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

    protected void resetShreddingStats() {
        ItemStack inputStack = this.shreddingInputPartition.getFirstNonEmptyStack();
        IndustrialShreddingRecipeInput recipeInput = new IndustrialShreddingRecipeInput(inputStack, Objects.requireNonNull(this.level).getRandom());
        Optional<? extends RecipeHolder<? extends IndustrialShreddingRecipe>> match = this.quickCheck.getRecipeFor(recipeInput, level);
        if (match.isPresent() && !match.get().value().delegatesShreddingTimeToItemDurability()) {
            this.shreddingProgress = 0;
            this.shreddingTotalTime = match.get().value().getTotalShreddingTime();
        } else if (inputStack.isDamageableItem()) {
            this.shreddingProgress = inputStack.getDamageValue();
            this.shreddingTotalTime = inputStack.getMaxDamage();
        } else {
            this.shreddingProgress = 0;
            this.shreddingTotalTime = DEFAULT_SHREDDING_TIME;
        }
    }

    protected Direction getFacing() {
        return this.getBlockState().getValue(IndustrialShredderTopBlock.FACING);
    }

    @Override
    public @Nullable EnergyStorage getEnergyStorageForSide(@Nullable Direction direction) {
        return direction == this.getFacing().getOpposite() ? this.energyStorage : null;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = this.saveCustomOnly(registries);
        tag.remove(KlaxonNBTIds.SHREDDING_TIME);
        tag.remove(KlaxonNBTIds.SHREDDING_TIME_TOTAL);
        tag.remove(KlaxonNBTIds.COOLDOWN_TICKS);
        tag.remove(KlaxonNBTIds.JAMMED_STACKS);
        return tag;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.shreddingTotalTime = tag.getInt(KlaxonNBTIds.SHREDDING_TIME_TOTAL);
        this.shreddingProgress = Math.clamp(tag.getInt(KlaxonNBTIds.SHREDDING_TIME), 0, this.shreddingTotalTime);
        this.intakeInteractionCooldownTicks = Math.clamp(tag.getInt(KlaxonNBTIds.COOLDOWN_TICKS), 0, INTAKE_INTERACTION_COOLDOWN_TICKS);
        if (this.level != null && tag.contains(KlaxonNBTIds.JAMMED_STACKS)) {
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
