package net.myriantics.klaxon.block.machines.blast_processor.steel;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
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
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeData;
import net.myriantics.klaxon.recipe.blast_processing.BlastProcessingRecipeInput;
import net.myriantics.klaxon.recipe.explosive_catalyst.ExplosiveCatalystData;
import net.myriantics.klaxon.recipe.explosive_catalyst.ExplosiveCatalystDefinitionRecipeLogic;
import net.myriantics.klaxon.registry.block.KlaxonBlockEntityTypes;
import net.myriantics.klaxon.registry.misc.KlaxonGameRules;
import net.myriantics.klaxon.util.container.SlotsWrapperContainer;
import org.jetbrains.annotations.Nullable;

public class SteelBlastProcessorBlockEntity extends AbstractBlastProcessorBlockEntity {

    private static final int MUFFLER_INDEX = 2;
    private static final float POWERFUL_EXPLOSIVE_THRESHOLD = 4.0f;

    protected SteelBlastProcessorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public SteelBlastProcessorBlockEntity(BlockPos pos, BlockState blockState) {
        super(KlaxonBlockEntityTypes.STEEL_BLAST_PROCESSOR.value(), pos, blockState);
    }

    @Override
    protected int initStackLimitForSlot(int slot) {
        return switch (slot) {
            case INGREDIENT_INDEX -> 4;
            case CATALYST_INDEX, MUFFLER_INDEX -> 1;
            default -> -1;
        };
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return null;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (this.level != null && this.getBlockState().getBlock() instanceof SteelBlastProcessorBlock steelBlastProcessorBlock) {
            steelBlastProcessorBlock.updateState(this.level, this.worldPosition, this);
        }
    }

    @Override
    public void redstoneTrigger() {
        Level level = this.level;
        if (level != null && !level.isClientSide()) {
            BlockState state = this.getBlockState();

            if (!this.isEmpty() && state.getBlock() instanceof SteelBlastProcessorBlock block) {
                BlockPos pos = this.getBlockPos();
                ExplosiveCatalystContext.Block context = this.getContext();

                ExplosiveCatalystData powerData = ExplosiveCatalystDefinitionRecipeLogic.computeExplosiveCatalystData(context, this.getCatalystStack());
                ExplosiveCatalystBehavior behavior = powerData.behavior().value();

                // if its on cooldown just kaboom no matter what
                if (block.isExhaustIgnited(level, pos) && !behavior.isNoOp()) {
                    this.selfDestruct(level, pos, context, powerData);
                } else {
                    BlastProcessingRecipeData processingData = this.getBlastProcessingRecipeData(level, pos, new BlastProcessingRecipeInput(this.getIngredientStack(), powerData));

                    if (powerData.explosionPower() > 0) {
                        this.removeItemNoUpdate(CATALYST_INDEX);
                    }

                    this.ejectItems(level, pos, processingData, powerData);

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
    protected SlotsWrapperContainer getAccessForDirection(@Nullable Direction side) {
        Direction facing = this.getBlockState().getValue(SteelBlastProcessorBlock.HORIZONTAL_FACING);
        if (side == facing.getOpposite() || side == Direction.DOWN) { // if back or down do catalyst
            return this.catalystContainer;
        } else if (side != facing) {
            return this.ingredientContainer;
        } else {
            return SlotsWrapperContainer.EMPTY;
        }
    }

    @Override
    public int getContainerSize() {
        return 3;
    }

    public void setMuffler(ItemStack newMufflerStack) {
        this.setItem(MUFFLER_INDEX, newMufflerStack);
    }

    public ItemStack getMuffler() {
        return this.getItem(MUFFLER_INDEX);
    }
}
