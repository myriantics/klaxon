package net.myriantics.klaxon.block.machines.filing_cabinet;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.windcharge.AbstractWindCharge;
import net.minecraft.world.entity.projectile.windcharge.WindCharge;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.myriantics.klaxon.item.equipment.tools.WrenchItem;
import net.myriantics.klaxon.registry.misc.KlaxonSoundEvents;
import net.myriantics.klaxon.util.KlaxonVoxelShapeHelper;
import org.jetbrains.annotations.Nullable;

public class FilingCabinetDrawerBlock extends Block implements SimpleWaterloggedBlock, WorldlyContainerHolder, WrenchItem.WrenchPickupOffsettor {

    private static final MapCodec<FilingCabinetDrawerBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Block.propertiesCodec(),
            DyeColor.CODEC.fieldOf("dye_color").forGetter(FilingCabinetDrawerBlock::getDyeColor)
    ).apply(instance, FilingCabinetDrawerBlock::new));

    public static final EnumProperty<FrontAndTop> ORIENTATION = BlockStateProperties.ORIENTATION;

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final VoxelShape[] DRAWER_SHAPES = KlaxonVoxelShapeHelper.northUpDefaultFrontAndTopRotated(1d/16, 1d/16, 4d/16, 15d/16, 12d/16, 16d/16);

    private static final VoxelShape[] WALL_SHAPES = KlaxonVoxelShapeHelper.northUpDefaultFrontAndTopRotated(1d/16, 1d/16, 2d/16, 15d/16, 14d/16, 4d/16);
    private static final VoxelShape[] SHAPES = KlaxonVoxelShapeHelper.arrayUnion(DRAWER_SHAPES, WALL_SHAPES);
    private final DyeColor color;

    private Block filingCabinetBaseBlock = null;
    public FilingCabinetDrawerBlock(Properties properties, DyeColor color) {
        super(properties);
        registerDefaultState(this.stateDefinition.any()
                .setValue(ORIENTATION, FrontAndTop.NORTH_UP)
                .setValue(WATERLOGGED, false)
        );
        this.color = color;
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ORIENTATION, WATERLOGGED);
    }

    public DyeColor getDyeColor() {
        return this.color;
    }

    void setFilingCabinetBaseBlock(Block filingCabinetBaseBlock) {
        if (this.filingCabinetBaseBlock == null) {
            this.filingCabinetBaseBlock = filingCabinetBaseBlock;
        } else {
            throw new AssertionError("Tried to register [" + filingCabinetBaseBlock + "] as the base block for [" + this + "], which has already been assigned to [" + this.filingCabinetBaseBlock + "]!");
        }
    }

    @Override
    protected @Nullable MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        BlockPos attachedPos = this.findAttachedPosition(pos, state);
        BlockState attachedState = level.getBlockState(attachedPos);
        if (this.isCompatibleWithBase(state, attachedState)) {
            return attachedState.getMenuProvider(level, attachedPos);
        } else {
            return null;
        }
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        BlockPos filingCabinetBasePosition = this.findAttachedPosition(pos, state);
        if (filingCabinetBasePosition.equals(neighborPos) && !this.isCompatibleWithBase(state, neighborState)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        BlockPos filingCabinetBasePosition = this.findAttachedPosition(pos, state);
        BlockState filingCabinetBaseState = level.getBlockState(filingCabinetBasePosition);
        if (!this.isCompatibleWithBase(state, filingCabinetBaseState)) {
            level.destroyBlock(pos, false);
            return InteractionResult.SUCCESS;
        }

        // we know we're compatible with base from here on out

        FrontAndTop orientation = state.getValue(ORIENTATION);
        AABB wallShape = WALL_SHAPES[orientation.ordinal()].move(pos.getX(), pos.getY(), pos.getZ()).bounds();
        Vec3 hitPos = hitResult.getLocation();
        if (wallShape.inflate(0.005).contains(hitPos)) {
            if (((FilingCabinetBaseBlock) filingCabinetBaseState.getBlock()).retractDrawer(level, filingCabinetBaseState, filingCabinetBasePosition)) {
                return InteractionResult.SUCCESS;
            }
        } else if (level.getBlockEntity(filingCabinetBasePosition) instanceof FilingCabinetBlockEntity blockEntity) {
            player.openMenu(blockEntity);
            RandomSource random = level.getRandom();
            level.playSound(null, pos, KlaxonSoundEvents.BLOCK_FILING_CABINET_SEARCH, SoundSource.BLOCKS, (random.nextFloat() * 0.3f) + 0.4f, (random.nextFloat() * 0.3f) + 0.4f);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }

    @Override
    protected void attack(BlockState state, Level level, BlockPos pos, Player player) {
        super.attack(state, level, pos, player);
        if (level.isClientSide()) {
            return;
        }
        BlockPos attachedPosition = this.findAttachedPosition(pos, state);
        BlockState attachedState = level.getBlockState(attachedPosition);
        Direction drawerFacing = state.getValue(ORIENTATION).front();
        if (this.isCompatibleWithBase(state, attachedState)) {
            if (drawerFacing.getOpposite().equals(player.getNearestViewDirection()) || drawerFacing.getOpposite().equals(player.getDirection())) {
                // default retract sound is played in this method
                ((FilingCabinetBaseBlock) attachedState.getBlock()).retractDrawer(level, attachedState, attachedPosition);
            } else {
                RandomSource random = level.getRandom();
                // rattling sound plays alongside maybe particles to indicate that player is pushing the wrong direction
                level.playSound(null, pos, KlaxonSoundEvents.BLOCK_FILING_CABINET_RATTLE, SoundSource.BLOCKS, (random.nextFloat() * 0.3f) + 0.4f, (random.nextFloat() * 0.3f) + 0.4f);
            }
        }
    }

    @Override
    protected void onProjectileHit(Level level, BlockState state, BlockHitResult hit, Projectile projectile) {
        super.onProjectileHit(level, state, hit, projectile);
        if (projectile instanceof AbstractWindCharge) {
            return;
        }
        BlockPos attachedPosition = this.findAttachedPosition(hit.getBlockPos(), state);
        BlockState attachedState = level.getBlockState(attachedPosition);
        FrontAndTop orientation = state.getValue(ORIENTATION);
        Direction drawerFacing = orientation.front();
        if (this.isCompatibleWithBase(state, attachedState) && !attachedState.getValue(FilingCabinetBaseBlock.POWERED)) {
            if (drawerFacing.equals(hit.getDirection())) {
                // default retract sound is played in this method
                if (!level.isClientSide()) {
                    projectile.setDeltaMovement(Vec3.ZERO);
                    projectile.hurtMarked = true;
                    ((FilingCabinetBaseBlock) attachedState.getBlock()).retractDrawer(level, attachedState, attachedPosition);
                }
            } else {
                RandomSource random = level.getRandom();
                // rattling sound plays alongside maybe particles to indicate that player is pushing the wrong direction
                level.playSound(null, hit.getBlockPos(), KlaxonSoundEvents.BLOCK_FILING_CABINET_RATTLE, SoundSource.BLOCKS, (random.nextFloat() * 0.3f) + 0.4f, (random.nextFloat() * 0.3f) + 0.4f);
            }
        }
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && player.getAbilities().instabuild) {
            BlockPos basePos = this.findAttachedPosition(pos, state);
            BlockState baseState = level.getBlockState(basePos);
            if (this.isCompatibleWithBase(state, baseState)) {
                level.destroyBlock(basePos, false);
            }
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        FluidState fluidState = state.getFluidState();
        if (fluidState != oldState.getFluidState()) {
            level.scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(level));
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        super.onRemove(state, level, pos, newState, movedByPiston);
        if (state.is(newState.getBlock())) {
            if (newState.getValue(WATERLOGGED) && !state.getValue(WATERLOGGED)) {
                level.scheduleTick(pos, newState.getFluidState().getType(), newState.getFluidState().getType().getTickDelay(level));
            }
        } else {
            level.updateNeighbourForOutputSignal(pos, this);
            if (!movedByPiston) {
                BlockPos basePos = this.findAttachedPosition(pos, state);
                BlockState baseState = level.getBlockState(basePos);
                if (this.isCompatibleWithBase(state, baseState)) {
                    level.destroyBlock(basePos, true);
                }
            }
        }
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        @Nullable FilingCabinetBlockEntity blockEntity = this.findFilingCabinetBlockEntity(level, pos, state);
        if (blockEntity != null) {
            BlockPos filingCabinetBasePosition = this.findAttachedPosition(pos, state);
            BlockState filingCabinetBaseState = level.getBlockState(filingCabinetBasePosition);

            // delegate to filing cabinet base
            if (this.isCompatibleWithBase(state, filingCabinetBaseState)) {
                return filingCabinetBaseState.getAnalogOutputSignal(level, filingCabinetBasePosition);
            }
        }

        return 0;
    }

    protected BlockPos findAttachedPosition(BlockPos pos, BlockState state) {
        return pos.relative(state.getValue(ORIENTATION).front().getOpposite());
    }

    protected boolean isCompatibleWithBase(BlockState drawerState, BlockState baseState) {
        return baseState.is(this.filingCabinetBaseBlock) && baseState.hasProperty(FilingCabinetBaseBlock.ORIENTATION) && baseState.getValue(FilingCabinetBaseBlock.ORIENTATION) == drawerState.getValue(ORIENTATION);
    }

    public @Nullable FilingCabinetBlockEntity findFilingCabinetBlockEntity(LevelAccessor level, BlockPos drawerPos, BlockState drawerState) {
        BlockPos potentialBasePosition = this.findAttachedPosition(drawerPos, drawerState);
        BlockState cabinetBaseState = level.getBlockState(potentialBasePosition);

        if (this.isCompatibleWithBase(drawerState, cabinetBaseState) && level.getBlockEntity(potentialBasePosition) instanceof FilingCabinetBlockEntity blockEntity) {
            return blockEntity;
        }
        return null;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return this.isCompatibleWithBase(state, level.getBlockState(this.findAttachedPosition(pos, state)));
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return new ItemStack(this.filingCabinetBaseBlock);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES[state.getValue(ORIENTATION).ordinal()];
    }

    @Override
    public @Nullable WorldlyContainer getContainer(BlockState state, LevelAccessor level, BlockPos pos) {
        return this.findFilingCabinetBlockEntity(level, pos, state);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(ORIENTATION, rotation.rotation().rotate(state.getValue(ORIENTATION)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(ORIENTATION, mirror.rotation().rotate(state.getValue(ORIENTATION)));
    }

    @Override
    public BlockPos getOffsetPickupPosition(Level level, BlockPos pos, BlockState state) {
        return this.findAttachedPosition(pos, state);
    }

    @Override
    public boolean shouldOffset(Level level, BlockPos pos, BlockState state) {
        BlockPos attachedPos = this.findAttachedPosition(pos, state);
        BlockState attachedState = level.getBlockState(attachedPos);
        return this.isCompatibleWithBase(state, attachedState);
    }
}
