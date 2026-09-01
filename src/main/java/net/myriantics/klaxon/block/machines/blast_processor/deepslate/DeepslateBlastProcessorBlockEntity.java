package net.myriantics.klaxon.block.machines.blast_processor.deepslate;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.myriantics.klaxon.block.machines.blast_processor.AbstractBlastProcessorBlockEntity;
import net.myriantics.klaxon.entity.entities.mob.ominous_deepslate_blast_processor.OminousDeepslateBlastProcessorEntity;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystBehavior;
import net.myriantics.klaxon.mechanics.explosive_catalyst.context.ExplosiveCatalystContext;
import net.myriantics.klaxon.networking.s2c.BlastProcessorMenuPowerSyncPacket;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeData;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeInput;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystData;
import net.myriantics.klaxon.registry.block.KlaxonBlockEntityTypes;
import net.myriantics.klaxon.registry.entity.KlaxonEntityTypes;
import net.myriantics.klaxon.registry.misc.KlaxonGameRules;
import net.myriantics.klaxon.registry.misc.KlaxonSoundEvents;
import net.myriantics.klaxon.tag.klaxon.KlaxonExplosiveCatalystBehaviorTags;
import net.myriantics.klaxon.util.BlockDirectionHelper;
import net.myriantics.klaxon.util.container.ContainerPartition;
import org.apache.commons.lang3.builder.Diff;
import org.jetbrains.annotations.Nullable;

public class DeepslateBlastProcessorBlockEntity extends AbstractBlastProcessorBlockEntity implements ExtendedScreenHandlerFactory<BlastProcessorMenuPowerSyncPacket> {

    private static final ResourceKey<LootTable> OMINOUS_DEEPSLATE_BLAST_PROCESSOR_LOOT_TABLE = KlaxonEntityTypes.OMINOUS_DEEPSLATE_BLAST_PROCESSOR.value().getDefaultLootTable();
    private static final ContainerData EMPTY = new SimpleContainerData(0);

    protected DeepslateBlastProcessorBlockEntity(BlockEntityType<DeepslateBlastProcessorBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public DeepslateBlastProcessorBlockEntity(BlockPos pos, BlockState state) {
        this(KlaxonBlockEntityTypes.DEEPSLATE_BLAST_PROCESSOR.value(), pos, state);
    }

    @Override
    protected AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
        DeepslateBlastProcessorMenu screenHandler = new DeepslateBlastProcessorMenu(syncId, playerInventory, this, EMPTY, ContainerLevelAccess.create(level, worldPosition));
        screenHandler.slotsChanged(this);
        return screenHandler;
    }

    @Override
    protected void initPartitions(PartitionBuilder partitions) {
        this.ingredientPartition = partitions.partition(1, 1);
        this.catalystPartition = partitions.partition(1, 1);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return super.canTakeItemThroughFace(slot, stack, side) && !this.level.hasNeighborSignal(this.worldPosition);
    }

    @Override
    public Direction getFacing() {
        return this.getBlockState().getValue(DeepslateBlastProcessorBlock.FACING);
    }

    public void redstoneTrigger() {
        if (level != null && level instanceof ServerLevel serverLevel) {

            // default to false so that it shows no particles when dispensing nothing
            boolean shouldRunDispenserEffects = false;

            if (this.getCatalystStack().is(Items.OMINOUS_BOTTLE)) {
                // takes your bottle and gives you ODBP loot table on peaceful
                if (serverLevel.getDifficulty() == Difficulty.PEACEFUL) {
                    Direction facing = this.getFacing();
                    LootParams lootParams = new LootParams.Builder(serverLevel)
                            .withParameter(LootContextParams.ORIGIN, this.worldPosition.getCenter())
                            .create(LootContextParamSets.CHEST);
                    serverLevel.getServer().reloadableRegistries().get().registryOrThrow(Registries.LOOT_TABLE).getOptional(OMINOUS_DEEPSLATE_BLAST_PROCESSOR_LOOT_TABLE).ifPresent(table -> {
                        table.getRandomItems(lootParams, stack -> this.ejectItem(stack, facing));
                    });
                    this.ejectItem(this.getIngredientStack(), facing);
                    this.clearContent();
                    serverLevel.destroyBlock(this.worldPosition, false);
                } else {
                    OminousDeepslateBlastProcessorEntity entity = new OminousDeepslateBlastProcessorEntity(this.level, this.worldPosition, this.getCatalystStack(), this.getFacing());
                    serverLevel.addFreshEntity(entity);
                    this.clearContent();
                    serverLevel.setBlockAndUpdate(this.worldPosition, Blocks.AIR.defaultBlockState());
                }
                RandomSource randomSource = serverLevel.getRandom();
                this.level.playSound(null, this.worldPosition, KlaxonSoundEvents.BLOCK_DEEPSLATE_BLAST_PROCESSOR_CRUNCH_BOTTLE, SoundSource.BLOCKS, 0.4f + randomSource.nextFloat() * 0.3f, 0.5f + randomSource.nextFloat() * 0.2f);
                this.level.gameEvent(GameEvent.BLOCK_ACTIVATE, this.worldPosition, GameEvent.Context.of(this.level.getBlockState(this.worldPosition)));
                return;
            }

            if (!this.isEmpty()) {
                ExplosiveCatalystContext context = this.getContext(serverLevel);

                Pair<ExplosiveCatalystData, Holder<ExplosiveCatalystBehavior>> dataHolderPair = this.findEffectiveCatalystData(serverLevel, context);
                ExplosiveCatalystData catalystData = dataHolderPair.getFirst();
                Holder<ExplosiveCatalystBehavior> behaviorHolder = dataHolderPair.getSecond();

                // transform catalystData if needed
                BlastProcessingRecipeData processingData = this.getCraftedStacks(new BlastProcessingRecipeInput(
                        this.getIngredientStack(),
                        this.adaptEffectiveDataForCrafting(serverLevel, catalystData, behaviorHolder, context),
                        serverLevel.getRandom()
                ));

                // clear catalyst and do explosion effect if power is greater than 0
                if (catalystData.explosionPower() > 0) {
                    this.catalystPartition.clearContent();
                    behaviorHolder.value().createExplosion(context, this.getExplosionOutputLocation(), catalystData, this.level.getGameRules().getBoolean(KlaxonGameRules.BLAST_PROCESSOR_EXPLOSIONS_MODIFY_WORLD));
                }

                // eject recipe results
                this.ejectItems(processingData, catalystData);

                shouldRunDispenserEffects = !behaviorHolder.is(KlaxonExplosiveCatalystBehaviorTags.DOES_NOT_RUN_DISPENSER_EFFECTS);
            }

            // if this has been exploded, dont run these
            if (shouldRunDispenserEffects && !this.isRemoved()) {
                level.gameEvent(GameEvent.BLOCK_ACTIVATE, worldPosition, GameEvent.Context.of(level.getBlockState(worldPosition)));
                level.levelEvent(LevelEvent.SOUND_DISPENSER_DISPENSE, worldPosition, 0);

                // display particles if not front obstructed
                if (!DeepslateBlastProcessorBlock.isFrontObstructed(level, worldPosition)) {
                    level.levelEvent(LevelEvent.PARTICLES_SHOOT_SMOKE, worldPosition, this.getFacing().get3DDataValue());
                }
            }
        }
    }

    public void updateBlockState(@Nullable BlockState appendedState) {
        if (level != null && level.getBlockState(worldPosition).getBlock() instanceof DeepslateBlastProcessorBlock blastProcessorBlock) {
            blastProcessorBlock.updateBlockState(level, worldPosition, appendedState);
        }
    }

    @Override
    public void setChanged() {
        updateBlockState(null);
        super.setChanged();
    }

    public Position getExplosionOutputLocation() {
        return getItemOutputLocation(0.6);
    }

    @Override
    public Position getItemOutputLocation(Direction facing) {
        return getItemOutputLocation(0.7);
    }

    private Position getItemOutputLocation(double offset) {
        Direction facing = this.getFacing();
        Position centerPos = worldPosition.getCenter();
        double x = centerPos.x();
        double y = centerPos.y() - 0.3125;
        double z = centerPos.z();

        if (facing != null) {
            switch (facing) {
                case NORTH -> z -= offset;
                case SOUTH -> z += offset;
                case EAST -> x += offset;
                case WEST -> x -= offset;
            }
        }


        return new Vec3(x, y, z);
    }

    @Override
    public BlastProcessorMenuPowerSyncPacket getScreenOpeningData(ServerPlayer player) {

        ExplosiveCatalystData explosiveCatalystData = this.getEffectiveCatalystData();
        if (explosiveCatalystData == null) {
            explosiveCatalystData = ExplosiveCatalystData.ZERO;
        }
        BlastProcessingRecipeData blastProcessingRecipeData = this.getDisplayStacks(new BlastProcessingRecipeInput(this.getIngredientStack(), explosiveCatalystData, player.getRandom()));
        if (blastProcessingRecipeData == null) {
            blastProcessingRecipeData = BlastProcessingRecipeData.ZERO;
        }

        return new BlastProcessorMenuPowerSyncPacket(
                blastProcessingRecipeData.explosionPowerMin(),
                blastProcessingRecipeData.explosionPowerMax(),
                explosiveCatalystData.explosionPower(),
                explosiveCatalystData.producesFire()
        );
    }

    @Override
    protected ContainerPartition getAccessForDirection(@Nullable Direction side) {
        Direction facing = this.getFacing();
        if (side == BlockDirectionHelper.getLeft(facing) || side == BlockDirectionHelper.getRight(facing)) { // catalyst is only accessible from the sides on this
            return this.catalystPartition;
        } else if (side != facing) { // no front access - ingredient storage is default otherwise
            return this.ingredientPartition;
        } else {
            return ContainerPartition.EMPTY;
        }
    };
}
