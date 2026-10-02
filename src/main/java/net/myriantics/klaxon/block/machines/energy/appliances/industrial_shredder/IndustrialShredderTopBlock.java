package net.myriantics.klaxon.block.machines.energy.appliances.industrial_shredder;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.myriantics.klaxon.registry.block.KlaxonBlockStateProperties;
import net.myriantics.klaxon.tag.klaxon.KlaxonBlockTags;
import org.jetbrains.annotations.Nullable;

public class IndustrialShredderTopBlock extends BaseIndustrialShredderBlock {

    // Indicates whether the shredder top is obstructed or not - determines entity interaction and particle emission
    public static final BooleanProperty OBSTRUCTED = KlaxonBlockStateProperties.OBSTRUCTED;
    // Indicates whether the shredder is actively running or not.
    public static final BooleanProperty ACTIVE = KlaxonBlockStateProperties.ACTIVE;
    public static final DirectionProperty FACING = BaseIndustrialShredderBlock.FACING;

    public static final int ITEM_CONSUMPTION_EVENT_ID = 1;
    private static final float ITEM_CONSUMPTION_PARTICLE_VELOCITY_VERTICAL_SCALAR = 6.7f;

    protected Holder<Block> bottomBlock = null;

    public IndustrialShredderTopBlock(Properties properties) {
        super(properties, Part.TOP);

        registerDefaultState(this.defaultBlockState()
                .setValue(OBSTRUCTED, false)
                .setValue(ACTIVE, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ACTIVE, OBSTRUCTED);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }

    void setBottomBlock(Holder<Block> holder) {
        if (holder.value() instanceof IndustrialShredderBottomBlock) {
            this.bottomBlock = holder;
        } else {
            throw new IllegalArgumentException("Attempted to set Industrial Shredder Top Block's stored reference of its Bottom Block to an invalid value.");
        }
    }

    @Override
    protected Holder<? extends Block> getCounterpartBlock() {
        return this.bottomBlock;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new IndustrialShredderTopBlockEntity(pos, state);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);

        if (!level.isClientSide() && neighborPos.equals(pos.above())) {
            boolean obstructed;
            BlockState neighborState = level.getBlockState(neighborPos);
            @Nullable Storage<ItemVariant> aboveStorage = ItemStorage.SIDED.find(level, neighborPos, neighborState, null, Direction.DOWN);
            if (aboveStorage == null) {
                obstructed = this.doesStateObstructTop(level, neighborPos, neighborState);
            } else {
                if (level.getBlockEntity(pos) instanceof IndustrialShredderTopBlockEntity topBlockEntity) {
                    topBlockEntity.aboveStorageCache = aboveStorage;
                }
                obstructed = true;
            }
            if (obstructed != state.getValue(OBSTRUCTED)) {
                level.setBlockAndUpdate(pos, state.setValue(OBSTRUCTED, obstructed));
            }
        }
    }

    @Override
    protected boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
        return switch (id) {
            case ITEM_CONSUMPTION_EVENT_ID -> {
                if (level.isClientSide() && !state.getValue(OBSTRUCTED) && level.getBlockEntity(pos) instanceof IndustrialShredderTopBlockEntity blockEntity) {
                    ItemStack shreddedStack = blockEntity.shreddingInputPartition.getFirstNonEmptyStack();
                    if (!shreddedStack.isEmpty()) {
                        Direction.Axis axis = state.getValue(FACING).getAxis();
                        Vec3 center = new Vec3(pos.getX() + 0.5, pos.getY() + (17.5f/16), pos.getZ() + 0.5);
                        RandomSource random = level.getRandom();
                        for (int i = 0; i < random.nextInt(8, 12); i++) {
                            blockEntity.spawnShreddingParticle(level, axis, center, random, shreddedStack, ITEM_CONSUMPTION_PARTICLE_VELOCITY_VERTICAL_SCALAR);
                        }
                    }
                }
                yield true;
            }
            default -> super.triggerEvent(state, level, pos, id, param);
        };
    }

    protected boolean doesStateObstructTop(Level level, BlockPos pos, BlockState state) {
        return state.isFaceSturdy(level, pos, Direction.DOWN) && !state.is(KlaxonBlockTags.DOES_NOT_BLOCK_INDUSTRIAL_SHREDDER_ENTITY_INTERACTION);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (hitResult.getDirection() == Direction.UP && !stack.isEmpty() && level.getBlockEntity(pos) instanceof IndustrialShredderTopBlockEntity top) {
            try (Transaction tx = Transaction.openOuter()) {
                if (top.tryInsert(stack, 0, tx) > 0) {
                    if (level.isClientSide()) {
                        tx.abort();
                    } else {
                        tx.commit();
                    }
                    return ItemInteractionResult.SUCCESS;
                } else {
                    tx.abort();
                }
            }
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof IndustrialShredderTopBlockEntity blockEntity
                ? AbstractContainerMenu.getRedstoneSignalFromContainer(blockEntity.shreddingInputPartition)
                : 0;
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) {
            if (state.getValue(ACTIVE) && !state.getValue(OBSTRUCTED)) {
                return (level1, blockPos, blockState, blockEntity) -> {
                    if (blockEntity instanceof IndustrialShredderTopBlockEntity shredderTopBlockEntity) {
                        shredderTopBlockEntity.clientDisplayTick(level, blockPos, blockState);
                    }
                };
            }
            return null;
        }

        return (level1, blockPos, blockState, blockEntity) -> {
            if (blockEntity instanceof IndustrialShredderTopBlockEntity shredderTop) {
                shredderTop.serverTick(level, blockPos, blockState);
            }
        };
    }

    public enum Status implements StringRepresentable {
        IDLE("idle"),
        RUNNING("running"),
        JAMMED("jammed");

        private final String stringRepresentation;

        Status(String stringRepresentation) {
            this.stringRepresentation = stringRepresentation;
        }

        public boolean isRunning() {
            return this == RUNNING;
        }

        @Override
        public String getSerializedName() {
            return this.stringRepresentation;
        }
    }
}
