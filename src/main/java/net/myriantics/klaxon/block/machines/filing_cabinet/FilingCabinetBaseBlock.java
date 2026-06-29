package net.myriantics.klaxon.block.machines.filing_cabinet;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.redstone.NeighborUpdater;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.myriantics.klaxon.registry.misc.KlaxonSoundEvents;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;
import net.myriantics.klaxon.util.KlaxonCodecUtils;
import net.myriantics.klaxon.util.container.KlaxonStorageUtil;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;

public class FilingCabinetBaseBlock extends BaseEntityBlock {

    @SuppressWarnings("unchecked")
    private static final MapCodec<FilingCabinetBaseBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            propertiesCodec(),
            KlaxonCodecUtils.BLOCK_HOLDER_CODEC.fieldOf("extended_drawer_block").forGetter(i -> (Holder<Block>) (Object) i.extendedDrawerBlockHolder),
            DyeColor.CODEC.fieldOf("dye_color").forGetter(FilingCabinetBaseBlock::getDyeColor)
    ).apply(instance, FilingCabinetBaseBlock::new));
    public static final EnumProperty<FrontAndTop> ORIENTATION = BlockStateProperties.ORIENTATION;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final int EXTENSION_DELAY_TICKS = 4;

    private final Holder<FilingCabinetDrawerBlock> extendedDrawerBlockHolder;
    private final DyeColor color;

    @SuppressWarnings("unchecked")
    public FilingCabinetBaseBlock(Properties properties, Holder<Block> extendedDrawerBlockHolder, DyeColor color) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(ORIENTATION, FrontAndTop.NORTH_UP)
                .setValue(POWERED,  false)
                .setValue(OPEN, false)
        );
        if (extendedDrawerBlockHolder.value() instanceof FilingCabinetDrawerBlock) {
            this.extendedDrawerBlockHolder = (Holder<FilingCabinetDrawerBlock>) (Object) extendedDrawerBlockHolder;
            this.extendedDrawerBlockHolder.value().setFilingCabinetBaseBlock(this);
        } else {
            throw new AssertionError("Provided drawer block holder is not valid! Was + [" + extendedDrawerBlockHolder.value().getClass().getSimpleName() + "]");
        }
        this.color = color;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ORIENTATION, POWERED, OPEN);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    public DyeColor getDyeColor() {
        return this.color;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FilingCabinetBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        RandomSource random = level.getRandom();

        // can always open from back for qol
        if ((hitResult.getDirection().getOpposite().equals(state.getValue(ORIENTATION).front()) || this.isOpen(state)) && level.getBlockEntity(pos) instanceof FilingCabinetBlockEntity blockEntity) {
            player.openMenu(blockEntity);
            level.playSound(null, pos, KlaxonSoundEvents.BLOCK_FILING_CABINET_SEARCH, SoundSource.BLOCKS, (random.nextFloat() * 0.3f) + 0.4f, (random.nextFloat() * 0.3f) + 0.4f);
        } else {
            if (this.canDrawerExtend(level, pos, state)) {
                level.scheduleTick(pos, this, EXTENSION_DELAY_TICKS);
            } else {
                // super F
                level.playSound(null, pos, KlaxonSoundEvents.BLOCK_FILING_CABINET_RATTLE, SoundSource.BLOCKS, (random.nextFloat() * 0.3f) + 0.4f, (random.nextFloat() * 0.3f) + 0.4f);
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected void onExplosionHit(BlockState state, Level level, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> dropConsumer) {
        if (explosion.canTriggerBlocks() && !state.getValue(POWERED)) {
            if (this.isOpen(state)) {
                this.retractDrawer(level, state, pos);
            } else {
                level.scheduleTick(pos, this, EXTENSION_DELAY_TICKS);
            }
        }

        super.onExplosionHit(state, level, pos, explosion, dropConsumer);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);

        Direction facing = state.getValue(ORIENTATION).front();
        boolean powered = this.getNeighborSignal(level, pos, facing);

        if (powered != state.getValue(POWERED)) {
            level.setBlockAndUpdate(pos, state.setValue(POWERED, powered));
            if (powered != this.isOpen(state)) {
                if (powered) {
                    level.scheduleTick(pos, this, EXTENSION_DELAY_TICKS);
                } else {
                    this.retractDrawer(level, state, pos);
                }
            }
        }
    }

    protected boolean getNeighborSignal(SignalGetter signalGetter, BlockPos pos, Direction facing) {
        for (Direction direction2 : Direction.values()) {
            if (direction2 != facing && signalGetter.hasSignal(pos.relative(direction2), direction2)) {
                return true;
            }
        }

        return signalGetter.hasSignal(pos, Direction.DOWN);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        if (!this.isOpen(state)) {
            this.extendDrawer(level, state, pos);
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        Containers.dropContentsOnDestroy(state, newState, level, pos);
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof FilingCabinetBlockEntity blockEntity ? blockEntity.getAnalogSignalStrength() : 0;
    }

    public void extendDrawer(Level level, BlockState state, BlockPos pos) {
        FrontAndTop orientation = state.getValue(ORIENTATION);
        BlockPos drawerPos = this.findDrawerPos(state, pos);
        BlockState stateWhereWeWantToPutDrawer = level.getBlockState(drawerPos);
        RandomSource random = level.getRandom();
        if (this.canDrawerReplace(level, drawerPos, stateWhereWeWantToPutDrawer)) {
            level.setBlockAndUpdate(pos, state.setValue(OPEN, true));
            BlockState proposedDrawerState = this.getDrawerBlock().defaultBlockState().setValue(FilingCabinetDrawerBlock.ORIENTATION, orientation).setValue(FilingCabinetDrawerBlock.WATERLOGGED, stateWhereWeWantToPutDrawer.getFluidState().is(Fluids.WATER));
            level.destroyBlock(drawerPos, true);
            level.setBlockAndUpdate(drawerPos, proposedDrawerState);

            level.gameEvent(GameEvent.BLOCK_OPEN, pos, GameEvent.Context.of(state));
            level.playSound(null, pos, KlaxonSoundEvents.BLOCK_FILING_CABINET_EXTEND, SoundSource.BLOCKS, (random.nextFloat() * 0.3f) + 0.4f, (random.nextFloat() * 0.3f) + 0.4f);

            Direction.Axis movementAxis = orientation.front().getAxis();
            VoxelShape shape = proposedDrawerState.getShape(level, drawerPos).move(drawerPos.getX(), drawerPos.getY(), drawerPos.getZ());

            double x = movementAxis == Direction.Axis.X ? 1 : 0;
            double y = movementAxis == Direction.Axis.Y ? 1 : 0;
            double z = movementAxis == Direction.Axis.Z ? 1 : 0;

            for (Entity entity : level.getEntities(null, shape.bounds())) {
                if (entity.getPistonPushReaction().equals(PushReaction.IGNORE)) {
                    continue;
                }
                double diff = Shapes.collide(movementAxis, entity.getBoundingBox().move(x, y, z), List.of(shape), -1.0);
                entity.addDeltaMovement(new Vec3(x + (x * diff), y + (y * diff), z + (z * diff)));
            }
        } else {
            if (level.getBlockEntity(pos) instanceof FilingCabinetBlockEntity blockEntity) {
                @Nullable Storage<ItemVariant> storage = KlaxonStorageUtil.findStorage(level, drawerPos, orientation.front().getOpposite());
                if (storage != null) {
                    blockEntity.transferStacks(storage);
                    level.playSound(null, pos, KlaxonSoundEvents.BLOCK_FILING_CABINET_EJECT, SoundSource.BLOCKS, (random.nextFloat() * 0.3f) + 0.4f, (random.nextFloat() * 0.3f) + 0.7f);
                }
            }
        }
    }

    public boolean retractDrawer(Level level, BlockState state, BlockPos pos) {
        if (this.isOpen(state)) {
            if (!level.isClientSide()) {
                BlockPos drawerPos = this.findDrawerPos(state, pos);
                BlockState drawerState = level.getBlockState(drawerPos);
                if (!drawerState.is(this.extendedDrawerBlockHolder.value())) {
                    return false;
                }
                RandomSource random = level.getRandom();
                level.playSound(null, pos, KlaxonSoundEvents.BLOCK_FILING_CABINET_RETRACT, SoundSource.BLOCKS, (random.nextFloat() * 0.3f) + 0.4f, (random.nextFloat() * 0.3f) + 0.7f);
                level.gameEvent(GameEvent.BLOCK_CLOSE, pos, GameEvent.Context.of(state));
                level.setBlock(drawerPos, drawerState.getFluidState().createLegacyBlock(), (Block.UPDATE_MOVE_BY_PISTON | Block.UPDATE_ALL_IMMEDIATE));
                level.setBlockAndUpdate(pos, state.setValue(OPEN, false));
            }

            return true;
        }
        return false;
    }

    public boolean canDrawerExtend(Level level, BlockPos pos, BlockState state) {
        if (state.getValue(OPEN)) {
            return false;
        }
        BlockPos desiredExtensionPos = this.findDrawerPos(state, pos);
        BlockState obstructionState = level.getBlockState(desiredExtensionPos);
        return this.canDrawerReplace(level, desiredExtensionPos, obstructionState) || ItemStorage.SIDED.find(level, desiredExtensionPos, obstructionState, null, state.getValue(ORIENTATION).front().getOpposite()) != null;
    }

    public boolean canDrawerReplace(Level level, BlockPos pos, BlockState state) {
        if (state.is(KlaxonBlockTags.FILING_CABINET_DRAWER_REPLACEABLE_DENYLIST)) {
            return false;
        }
        if (state.is(KlaxonBlockTags.FILING_CABINET_DRAWER_REPLACEABLE_ALLOWLIST)) {
            return true;
        }
        final PushReaction pushReaction = state.getPistonPushReaction();
        if (pushReaction == PushReaction.BLOCK) {
            return false;
        }
        if (state.getBlock() instanceof GameMasterBlock) {
            return false;
        }
        if (pushReaction == PushReaction.DESTROY) {
            return true;
        }
        if (state.getCollisionShape(level, pos).isEmpty()) {
            return true;
        }
        if (state.canBeReplaced()) {
            return true;
        }
        return state.getDestroySpeed(level, pos) == 0;
    }

    public boolean isOpen(BlockState state) {
        return state.getValue(OPEN);
    }

    protected BlockPos findDrawerPos(BlockState state, BlockPos pos) {
        return pos.relative(state.getValue(ORIENTATION).front());
    }

    public FilingCabinetDrawerBlock getDrawerBlock() {
        return this.extendedDrawerBlockHolder.value();
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getNearestLookingDirection().getOpposite();

        // NOT jacked from crafter block AT ALL idk what youre talking about

        Direction direction2 = switch (direction) {
            case DOWN -> context.getHorizontalDirection().getOpposite();
            case UP -> context.getHorizontalDirection();
            case NORTH, SOUTH, WEST, EAST -> Direction.UP;
        };
        return this.defaultBlockState()
                .setValue(ORIENTATION, FrontAndTop.fromFrontAndTop(direction, direction2))
                .setValue(POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos()));
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(ORIENTATION, rotation.rotation().rotate(state.getValue(ORIENTATION)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(ORIENTATION, mirror.rotation().rotate(state.getValue(ORIENTATION)));
    }
}
