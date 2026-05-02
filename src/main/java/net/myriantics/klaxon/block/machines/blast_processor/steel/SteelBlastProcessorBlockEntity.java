package net.myriantics.klaxon.block.machines.blast_processor.steel;

import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.myriantics.klaxon.block.machines.blast_processor.AbstractBlastProcessorBlockEntity;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystBehavior;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystContext;
import net.myriantics.klaxon.mechanics.muffling.MufflerStorage;
import net.myriantics.klaxon.networking.KlaxonServerPlayNetworkHandler;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeData;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeInput;
import net.myriantics.klaxon.recipe.explosive_catalyst.ExplosiveCatalystData;
import net.myriantics.klaxon.recipe.explosive_catalyst.ExplosiveCatalystDefinitionRecipeLogic;
import net.myriantics.klaxon.registry.block.KlaxonBlockEntityTypes;
import net.myriantics.klaxon.registry.misc.KlaxonGameRules;
import net.myriantics.klaxon.registry.misc.KlaxonSoundEvents;
import net.myriantics.klaxon.registry.misc.KlaxonWorldEvents;
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
        if (this.level instanceof ServerLevel level) {
            BlockState state = this.getBlockState();
            BlockPos pos = this.getBlockPos();

            if (!this.isEmpty() && state.getBlock() instanceof SteelBlastProcessorBlock block) {
                Direction facing = this.getFacing();
                BlockState aboveState = level.getBlockState(pos.above());
                ExplosiveCatalystContext.Block context = this.getContext();

                ExplosiveCatalystData catalystData = ExplosiveCatalystDefinitionRecipeLogic.computeExplosiveCatalystData(context, this.getCatalystStack());
                ExplosiveCatalystBehavior behavior = catalystData.behavior().value();

                // if its on cooldown just kaboom no matter what
                if (block.isFieryExhaust(aboveState) && !behavior.isNoOp() && catalystData.explosionPower() > 0) {
                    this.selfDestruct(level, pos, context, catalystData);
                } else {
                    BlastProcessingRecipeData processingData = this.getCraftedStacks(new BlastProcessingRecipeInput(this.getIngredientStack(), catalystData));

                    if (catalystData.explosionPower() > 0) {
                        this.catalystPartition.clearContent();
                    }

                    this.storageCache = ItemStorage.SIDED.find(level, pos.relative(facing), facing.getOpposite());
                    this.ejectItems(processingData, catalystData);
                    this.storageCache = null;

                    // self destruct if overload handling failed
                    if (catalystData.explosionPower() > POWERFUL_EXPLOSIVE_THRESHOLD && !block.handleOverload(level, pos, this, catalystData)) {
                        this.selfDestruct(level, pos, context, catalystData);
                    } else if (!this.mufflerStorage.isPresent()) {
                        RandomSource random = level.getRandom();
                        if (catalystData.explosionPower() > 0) {
                            level.playSound(
                                    null,
                                    pos,
                                    KlaxonSoundEvents.BLOCK_STEEL_BLAST_PROCESSOR_ACTIVATE,
                                    SoundSource.BLOCKS,
                                    0.1f + (0.3f * random.nextFloat()),
                                    0.1f + (0.2f * random.nextFloat())
                            );
                        } else {
                            playFailSound(level, pos, random);
                        }
                        level.gameEvent(GameEvent.BLOCK_ACTIVATE, pos, GameEvent.Context.of(this.getBlockState()));
                    }
                }
            } else {
                if (!this.mufflerStorage.isPresent()) {
                    this.playFailSound(level, pos, level.getRandom());
                    level.gameEvent(GameEvent.BLOCK_ACTIVATE, pos, GameEvent.Context.of(this.getBlockState()));
                }
            }
        }
    }

    private void playFailSound(ServerLevel level, BlockPos pos, RandomSource random) {
        level.playSound(
                null,
                pos,
                KlaxonSoundEvents.BLOCK_STEEL_BLAST_PROCESSOR_FAIL,
                SoundSource.BLOCKS,
                0.4f + (0.33f * random.nextFloat()),
                0.3f + (0.4f * random.nextFloat())
        );
    }

    private void selfDestruct(ServerLevel level, BlockPos pos, ExplosiveCatalystContext.Block context, ExplosiveCatalystData powerData) {
        level.destroyBlock(pos, false);
        KlaxonServerPlayNetworkHandler.syncWorldEvent(level, pos, KlaxonWorldEvents.SPAWN_BLOCK_BREAK_PARTICLES);
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
