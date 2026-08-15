package net.myriantics.klaxon.block.machines.energy.generators.turbine;

import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.myriantics.klaxon.mechanics.turbine_generator.boost.TurbineGeneratorBoostInstance;
import net.myriantics.klaxon.mechanics.turbine_generator.boost.TurbineGeneratorBoostManager;
import net.myriantics.klaxon.mechanics.turbine_generator.power_source.StaticTurbineGeneratorPowerSource;
import net.myriantics.klaxon.registry.KlaxonRegistries;
import net.myriantics.klaxon.registry.block.KlaxonBlockEntityTypes;
import net.myriantics.klaxon.registry.misc.KlaxonNBTIds;
import net.myriantics.klaxon.util.storage.energy.KlaxonEnergyStorageProvider;
import net.myriantics.klaxon.util.storage.item.ContainerPartition;
import net.myriantics.klaxon.util.storage.item.KlaxonBaseContainerBlockEntity;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;

import java.util.Set;
import java.util.stream.Collectors;

public class TurbineGeneratorBlockEntity extends KlaxonBaseContainerBlockEntity implements KlaxonEnergyStorageProvider {

    public static final int MAX_POWER_SOURCE_RANGE = 32;
    protected long storedPower = 0;
    protected long velocity = 0;

    protected StaticTurbineGeneratorPowerSource powerSource = null;
    protected final TurbineGeneratorBoostManager boostManager = new TurbineGeneratorBoostManager(this);

    public static final float ACCELERATION_FACTOR = 0.4f;

    protected final EnergyStorage generatedPowerStorage = new TurbineGeneratorEnergyStorage();
    protected @Nullable EnergyStorage targetStorageCache;
    private boolean initialized = false;
    private int coupledTicks = 0;

    protected ContainerPartition turbinePartition;

    protected TurbineGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public TurbineGeneratorBlockEntity(BlockPos pos, BlockState state) {
        this(KlaxonBlockEntityTypes.TURBINE_GENERATOR.value(), pos, state);
    }

    @Override
    protected void initPartitions(PartitionBuilder partitions) {
        this.turbinePartition = partitions.partition(1, 1);
    }

    @Override
    protected Component getDefaultName() {
        return null;
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return null;
    }

    public Direction getFacing() {
        return this.getBlockState().getValue(TurbineGeneratorBlock.FACING);
    }

    public ItemStack getTurbineStack() {
        return this.turbinePartition.getFirstNonEmptyStack();
    }

    public void setTurbineStack(ItemStack newTurbineStack) {
        this.turbinePartition.setItem(0, newTurbineStack);
    }

    public boolean hasTurbine() {
        return !this.getTurbineStack().isEmpty();
    }

    public int getComparatorSignalStrength() {
        ItemStack turbineStack = this.getTurbineStack();
        if (turbineStack.isEmpty()) {
            return 0;
        }
        return ((turbineStack.getMaxDamage() - turbineStack.getDamageValue()) * 14 / turbineStack.getMaxDamage()) + 1;
    }

    public void serverTick(ServerLevel level, BlockPos pos, BlockState state) {
        // initialize storage when loading world
        if (!this.initialized) {
            Direction facing = this.getFacing();
            this.targetStorageCache = EnergyStorage.SIDED.find(level, pos.relative(facing.getOpposite()), facing);
            this.initialized = true;
        }

        // if we don't have a turbine, reset stored power and velocity then no-op
        if (!this.hasTurbine()) {
            if (this.storedPower != 0 || this.velocity != 0) {
                this.storedPower = 0;
                this.velocity = 0;
                this.setChanged();
            }
            return;
        }

        // tick boosts
        this.boostManager.tick();

        // 4000 = 500 * 8

        // reassess power sources every 10 ticks unless coupled

        if (this.isCoupled()) {
            this.coupledTicks--;
        } else if (level.getServer().getTickCount() % 10 == 0) {
            this.reassessPowerSource(level, pos, state);
        }

        // update velocity
        long targetVelocity = this.getTargetVelocity();
        if (targetVelocity != this.velocity) {
            double progress = (targetVelocity - this.velocity) * ACCELERATION_FACTOR;
            this.velocity += progress > 0
                    ? Math.max((int) progress, 1)
                    : Math.min((int) progress, -1);
        }

        // move generated power
        long exportedPower = 0;
        try (Transaction tx = Transaction.openOuter()) {
            exportedPower = EnergyStorageUtil.move(this.generatedPowerStorage, this.targetStorageCache, this.generatedPowerStorage.getCapacity(), tx);
        }

        long remainder = Math.max(this.velocity - exportedPower, 0);

        this.storedPower = this.velocity;
        this.velocity = this.velocity - ((this.velocity - remainder) / 20);
        this.setChanged();
    }

    protected void reassessPowerSource(ServerLevel level, BlockPos pos, BlockState state) {
        Direction facing = this.getFacing();

        Set<StaticTurbineGeneratorPowerSource> powerSources = level.registryAccess().lookupOrThrow(KlaxonRegistries.STATIC_TURBINE_GENERATOR_POWER_SOURCE).listElements().map(Holder::value).collect(Collectors.toSet());
        for (int i = 0; i < MAX_POWER_SOURCE_RANGE; i++) {

            // trim power sources that are out of range
            final int distance = i;
            powerSources.removeIf(source -> !source.isWithinRange(distance));

            // don't bother checking for power sources if we have no more valid ones in the registry
            if (powerSources.isEmpty()) {
                break;
            }

            // shift blockpos and check blockstate
            BlockPos offsetPos = pos.relative(facing, i + 1);
            BlockInWorld block = new BlockInWorld(level, offsetPos, false);
            if (this.stateConductsAirflow(level, offsetPos, block.getState())) {
                continue;
            }

            // if we can't conduct airflow, check if we've hit a power source.
            for (StaticTurbineGeneratorPowerSource source : powerSources) {
                if (source.test(block, facing)) {
                    this.setPowerSource(source);
                    return;
                }
            }

            // if all attempts to find a power source from the nonconductive block failed, clear power source.
            this.setPowerSource(null);
        }
    }

    protected void setPowerSource(@Nullable StaticTurbineGeneratorPowerSource source) {
        this.powerSource = source;
    }

    protected long getTargetVelocity() {
        return this.boostManager.modify(this.powerSource == null ? 0 : this.powerSource.getTargetVelocity());
    }

    public void additionBoost(ResourceLocation boostRl, long boostAmount, int boostDuration) {
        this.boostManager.additionBoost(boostRl, boostAmount, boostDuration);
    }

    public void multiplicationBoost(ResourceLocation boostRl, double boostMultiplier, int boostDuration) {
        this.boostManager.multiplicationBoost(boostRl, boostMultiplier, boostDuration);
    }

    public void coupleForTicks(int ticks) {
        this.setPowerSource(null);
        this.coupledTicks = ticks;
    }

    public void decouple() {
        this.coupledTicks = 0;
    }

    public boolean isCoupled() {
        return this.coupledTicks > 0;
    }

    protected boolean stateConductsAirflow(Level level, BlockPos pos, BlockState state) {
        return state.isAir();
    }

    protected boolean stateObstructsAirflow(Level level, BlockPos pos, BlockState state) {
        return false;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putLong(KlaxonNBTIds.VELOCITY, this.velocity);
        tag.putLong(KlaxonNBTIds.STORED_POWER, this.storedPower);
        tag.putInt(KlaxonNBTIds.COUPLED_TICKS, this.coupledTicks);
        if (this.powerSource instanceof StaticTurbineGeneratorPowerSource staticPowerSource) {
            tag.put(KlaxonNBTIds.STATIC_POWER_SOURCE, staticPowerSource.save(new CompoundTag(), registries));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.velocity = Math.max(tag.getLong(KlaxonNBTIds.VELOCITY), 0);
        this.storedPower = Math.max(tag.getLong(KlaxonNBTIds.STORED_POWER), 0);
        this.coupledTicks = Math.max(tag.getInt(KlaxonNBTIds.COUPLED_TICKS), 0);
        if (tag.contains(KlaxonNBTIds.STATIC_POWER_SOURCE) && StaticTurbineGeneratorPowerSource.load(tag.getCompound(KlaxonNBTIds.STATIC_POWER_SOURCE), registries) instanceof StaticTurbineGeneratorPowerSource powerSource) {
            this.powerSource = powerSource;
        }
    }

    public void setTargetStorage(EnergyStorage energyStorage) {
        this.targetStorageCache = energyStorage;
    }

    @Override
    public @Nullable EnergyStorage getEnergyStorageForSide(@Nullable Direction direction) {
        return direction == null || direction == this.getFacing() ? this.generatedPowerStorage : null;
    }

    private final class TurbineGeneratorEnergyStorage extends SnapshotParticipant<Long> implements EnergyStorage {

        @Override
        protected Long createSnapshot() {
            return TurbineGeneratorBlockEntity.this.storedPower;
        }

        @Override
        protected void readSnapshot(Long snapshot) {
            TurbineGeneratorBlockEntity.this.storedPower = snapshot;
        }

        @Override
        public long insert(long maxAmount, TransactionContext transaction) {
            return 0;
        }

        @Override
        public long extract(long maxAmount, TransactionContext transaction) {
            StoragePreconditions.notNegative(maxAmount);

            long extracted = Math.min(maxAmount, this.getAmount());

            if (extracted > 0) {
                updateSnapshots(transaction);
                TurbineGeneratorBlockEntity.this.storedPower -= extracted;
                return extracted;
            }

            return 0;
        }

        @Override
        public long getAmount() {
            return TurbineGeneratorBlockEntity.this.storedPower;
        }

        @Override
        public long getCapacity() {
            return TurbineGeneratorBlockEntity.this.storedPower;
        }

        @Override
        public boolean supportsInsertion() {
            return false;
        }
    }
}
