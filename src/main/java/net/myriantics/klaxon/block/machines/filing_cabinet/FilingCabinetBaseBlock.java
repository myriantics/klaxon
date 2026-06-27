package net.myriantics.klaxon.block.machines.filing_cabinet;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.myriantics.klaxon.util.KlaxonCodecUtils;
import org.jetbrains.annotations.Nullable;
import snownee.jade.addon.debug.BlockStatesProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

public class FilingCabinetBaseBlock extends BaseEntityBlock {

    @SuppressWarnings("unchecked")
    private static final MapCodec<FilingCabinetBaseBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Properties.CODEC.fieldOf("properties").forGetter(Block::properties),
            KlaxonCodecUtils.BLOCK_HOLDER_CODEC.fieldOf("extended_drawer_block").forGetter(i -> (Holder<Block>) (Object) i.extendedDrawerBlockHolder)
    ).apply(instance, FilingCabinetBaseBlock::new));
    public static final EnumProperty<FrontAndTop> ORIENTATION = BlockStateProperties.ORIENTATION;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    private final Holder<FilingCabinetDrawerBlock> extendedDrawerBlockHolder;
    public static final int EXTENSION_DELAY_TICKS = 4;

    @SuppressWarnings("unchecked")
    public FilingCabinetBaseBlock(Properties properties, Holder<Block> extendedDrawerBlockHolder) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(ORIENTATION, FrontAndTop.NORTH_UP)
                .setValue(POWERED,  false)
        );
        if (extendedDrawerBlockHolder.value() instanceof FilingCabinetDrawerBlock) {
            this.extendedDrawerBlockHolder = (Holder<FilingCabinetDrawerBlock>) (Object) extendedDrawerBlockHolder;
        } else {
            throw new AssertionError("Provided drawer block holder is not valid! Was + [" + extendedDrawerBlockHolder.value().getClass().getSimpleName() + "]");
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ORIENTATION, POWERED);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
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
        if (this.isOpen(level, state, pos) && level.getBlockEntity(pos) instanceof FilingCabinetBlockEntity blockEntity) {
            player.openMenu(blockEntity);
        } else {
            level.scheduleTick(pos, this, EXTENSION_DELAY_TICKS);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void onExplosionHit(BlockState state, Level level, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> dropConsumer) {
        if (explosion.canTriggerBlocks() && !state.getValue(POWERED)) {
            if (this.isOpen(level, state, pos)) {
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

        boolean powered = level.hasNeighborSignal(pos);
        if (powered != state.getValue(POWERED)) {
            level.setBlockAndUpdate(pos, state.setValue(POWERED, powered));
            if (powered != this.isOpen(level, state, pos)) {
                if (powered) {
                    level.scheduleTick(pos, this, EXTENSION_DELAY_TICKS);
                } else {
                    this.retractDrawer(level, state, pos);
                }
            }
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        if (!this.isOpen(level, state, pos)) {
            this.extendDrawer(level, state, pos);
        }
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof FilingCabinetBlockEntity blockEntity ? blockEntity.getAnalogSignalStrength() : 0;
    }

    // todo: add restrictions on this
    public boolean extendDrawer(Level level, BlockState state, BlockPos pos) {
        FrontAndTop orientation = state.getValue(ORIENTATION);
        BlockPos drawerPos = this.findDrawerPos(state, pos);
        BlockState stateWhereWeWantToPutDrawer = level.getBlockState(drawerPos);
        BlockState proposedDrawerState = this.getDrawerBlock().defaultBlockState().setValue(FilingCabinetDrawerBlock.ORIENTATION, orientation).setValue(FilingCabinetDrawerBlock.WATERLOGGED, stateWhereWeWantToPutDrawer.getFluidState().is(Fluids.WATER));
        level.setBlockAndUpdate(drawerPos, proposedDrawerState);

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
        return true;
    }

    public boolean retractDrawer(Level level, BlockState state, BlockPos pos) {
        if (this.isOpen(level, state, pos)) {
            if (!level.isClientSide()) {
                BlockPos drawerPos = this.findDrawerPos(state, pos);
                BlockState drawerState = level.getBlockState(drawerPos);
                level.setBlock(drawerPos, drawerState.getFluidState().createLegacyBlock(), (Block.UPDATE_MOVE_BY_PISTON | Block.UPDATE_ALL_IMMEDIATE));
            }

            return true;
        }
        return false;
    }

    public boolean isOpen(Level level, BlockState state, BlockPos pos) {
        BlockPos drawerPos = this.findDrawerPos(state, pos);
        BlockState drawerState = level.getBlockState(drawerPos);
        return drawerState.getBlock() instanceof FilingCabinetDrawerBlock drawerBlock && drawerBlock.isCompatibleWithBase(drawerState, state);
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
