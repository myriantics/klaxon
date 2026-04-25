package net.myriantics.klaxon.block.machines.blast_processor.deepslate;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.myriantics.klaxon.block.machines.blast_processor.AbstractBlastProcessorBlockEntity;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystBehavior;
import net.myriantics.klaxon.mechanics.explosive_catalyst.ExplosiveCatalystContext;
import net.myriantics.klaxon.networking.s2c.BlastProcessorScreenSyncPacket;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeData;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeInput;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystData;
import net.myriantics.klaxon.recipe.explosive_catalyst_definition.ExplosiveCatalystDefinitionRecipeLogic;
import net.myriantics.klaxon.registry.block.KlaxonBlockEntityTypes;
import net.myriantics.klaxon.registry.misc.KlaxonGameRules;
import net.myriantics.klaxon.tag.klaxon.KlaxonExplosiveCatalystBehaviorTags;
import net.myriantics.klaxon.util.BlockDirectionHelper;
import net.myriantics.klaxon.util.container.SlotsWrapperContainer;
import org.jetbrains.annotations.Nullable;

import static net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlock.HORIZONTAL_FACING;
import static net.myriantics.klaxon.block.machines.blast_processor.deepslate.DeepslateBlastProcessorBlock.isFrontObstructed;

public class DeepslateBlastProcessorBlockEntity extends AbstractBlastProcessorBlockEntity implements ExtendedScreenHandlerFactory<BlastProcessorScreenSyncPacket>, WorldlyContainer {

    protected DeepslateBlastProcessorBlockEntity(BlockEntityType<DeepslateBlastProcessorBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public DeepslateBlastProcessorBlockEntity(BlockPos pos, BlockState state) {
        this(KlaxonBlockEntityTypes.DEEPSLATE_BLAST_PROCESSOR.value(), pos, state);
    }

    @Override
    protected AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
        DeepslateBlastProcessorScreenHandler screenHandler = new DeepslateBlastProcessorScreenHandler(syncId, playerInventory, this, ContainerLevelAccess.create(level, worldPosition));
        screenHandler.slotsChanged(this);
        return screenHandler;
    }

    @Override
    protected int initStackLimitForSlot(int slot) {
        return 1;
    }


    public int getContainerSize() {
        return 2;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return super.canTakeItemThroughFace(slot, stack, side) && !this.level.hasNeighborSignal(this.worldPosition);
    }

    public void redstoneTrigger() {
        if (level != null && !level.isClientSide) {

            // default to false so that it shows no particles when dispensing nothing
            boolean shouldRunDispenserEffects = false;

            if (!this.isEmpty()) {
                BlockPos pos = this.getBlockPos();
                ExplosiveCatalystContext.Block context = this.getContext();

                // compute blast processor behavior
                ExplosiveCatalystData data = ExplosiveCatalystDefinitionRecipeLogic.computeExplosiveCatalystData(context, this.getCatalystStack());

                Holder<ExplosiveCatalystBehavior> behavior = data.behavior();

                // transform data if needed
                BlastProcessingRecipeData processingData = this.getBlastProcessingRecipeData(level, worldPosition, new BlastProcessingRecipeInput(this.getIngredientStack(), data));

                // clear catalyst and do explosion effect if power is greater than 0
                if (data.explosionPower() > 0) {
                    this.removeItemNoUpdate(CATALYST_INDEX);
                    behavior.value().createExplosion(context, this.getExplosionOutputLocation(level.getBlockState(pos).getValue(HORIZONTAL_FACING)), data, this.level.getGameRules().getBoolean(KlaxonGameRules.BLAST_PROCESSOR_EXPLOSIONS_MODIFY_WORLD));
                }

                // eject recipe results
                this.ejectItems(level, worldPosition, processingData, data);

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

    public Position getExplosionOutputLocation(Direction facing) {
        return getItemOutputLocation(facing, 0.6);
    }

    @Override
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

        // default values if world is null
        ExplosiveCatalystData explosiveCatalystData = ExplosiveCatalystData.ZERO;
        BlastProcessingRecipeData blastProcessingRecipeData = BlastProcessingRecipeData.ZERO;

        // if we have a world, actually yoink the proper values.
        if (level != null) {
            explosiveCatalystData = ExplosiveCatalystDefinitionRecipeLogic.computeExplosiveCatalystData(this.getContext(), this.getCatalystStack());

            blastProcessingRecipeData = this.getBlastProcessingPreviewData(level, worldPosition, new BlastProcessingRecipeInput(this.getIngredientStack(), explosiveCatalystData));
        }

        return new BlastProcessorScreenSyncPacket(blastProcessingRecipeData.explosionPowerMin(),
                blastProcessingRecipeData.explosionPowerMax(),
                blastProcessingRecipeData.outputStacks(),
                explosiveCatalystData.explosionPower(),
                explosiveCatalystData.producesFire());
    }

    @Override
    protected SlotsWrapperContainer getAccessForDirection(@Nullable Direction side) {
        Direction facing = this.getBlockState().getValue(HORIZONTAL_FACING);
        if (side == BlockDirectionHelper.getLeft(facing) || side == BlockDirectionHelper.getRight(facing)) { // catalyst is only accessible from the sides on this
            return this.catalystContainer;
        } else if (side != facing) { // no front access - ingredient storage is default otherwise
            return this.ingredientContainer;
        } else {
            return SlotsWrapperContainer.EMPTY;
        }
    };
}
