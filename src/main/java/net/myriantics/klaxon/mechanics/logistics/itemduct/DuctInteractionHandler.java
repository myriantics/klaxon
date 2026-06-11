package net.myriantics.klaxon.mechanics.logistics.itemduct;

import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.NeighborUpdater;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class DuctInteractionHandler {

    private static final float ITEM_DROPPING_HORIZ_OFFSET = 0.5f + (EntityType.ITEM.getWidth() / 2);
    private static final float ITEM_DROPPING_VERTICAL_OFFSET = 0.5f + (EntityType.ITEM.getHeight() / 2);
    private static final float DEFAULT_EJECTED_ITEM_OUTPUT_VELOCITY = 3f/20;

    private final BlockEntity owner;
    @SuppressWarnings("unchecked")
    private final Storage<ItemVariant>[] storages = new Storage[6];
    private final DuctNode[] nodes = new DuctNode[6];
    private byte openDirections;

    public DuctInteractionHandler(BlockEntity owner) {
        this.owner = owner;
    }

    public static DuctInteractionHandler initialize(Level level, BlockPos pos, @Nullable BlockState state, @Nullable BlockEntity blockEntity) {
        if (state == null) {
            state = Objects.requireNonNull(level.getBlockState(pos));
        }
        if (blockEntity == null) {
            blockEntity = Objects.requireNonNull(level.getBlockEntity(pos));
        }
        if (!(state.getBlock() instanceof IDuctNodeBlock nodeBlock)) {
            throw new AssertionError();
        }

        DuctInteractionHandler handler = new DuctInteractionHandler(blockEntity);
        for (Direction face : NeighborUpdater.UPDATE_ORDER) {
            handler.updateDirection(level, pos, face, nodeBlock.isConnectionOpen(state, face));
        }

        return handler;
    }

    public void updateDirection(Level level, BlockPos pos, Direction face, boolean sideOpen) {
        if (!sideOpen) {
            this.openDirections &= (byte) ~(0x01 << face.ordinal());
            this.nodes[face.ordinal()] = null;
            this.storages[face.ordinal()] = null;
            return;
        }

        // open direction for starters
        this.openDirections |= (byte) (0x01 << face.ordinal());

        // check for another duct node
        BlockPos neighborPos = pos.relative(face);
        BlockState neighborState = level.getBlockState(neighborPos);
        BlockEntity neighborBlockEntity = level.getBlockEntity(neighborPos);
        if (neighborState.getBlock() instanceof IDuctNodeBlock nodeBlock && nodeBlock.isConnectionOpen(neighborState, face.getOpposite())) {
            @Nullable DuctNode node = nodeBlock.getNode(level, neighborPos, neighborState, neighborBlockEntity);
            this.nodes[face.ordinal()] = node;
            if (node != null) {
                return;
            }
        } else {
            this.nodes[face.ordinal()] = null;
        }

        // check for a storage
        @Nullable Storage<ItemVariant> storage = ItemStorage.SIDED.find(level, neighborPos, neighborState, neighborBlockEntity, face.getOpposite());
        this.storages[face.ordinal()] = storage;
        if (storage != null) {
            return;
        }
    }

    public boolean isDirectionOpen(Direction direction) {
        return (this.openDirections & 0x01 << direction.ordinal()) != 0;
    }

    public @Nullable Storage<ItemVariant> getStorageForSide(Direction face) {
        return this.storages[face.ordinal()];
    }

    public boolean push(DuctPayload payload, Direction inputFace, int remainingDepth) {
        boolean fullyEmptySuccess = this.pushInternal(payload, inputFace, remainingDepth);
        this.owner.setChanged();
        return fullyEmptySuccess;
    }

    public boolean pushInternal(DuctPayload payload, Direction inputFace, int remainingDepth) {
        Direction priorityMovementDirection = inputFace.getOpposite();

        // try moving in the same direction
        if (this.tryInsert(payload, priorityMovementDirection, remainingDepth)) {
            return true;
        }

        // down is second prio because gravity
        if (inputFace != Direction.DOWN && this.tryInsert(payload, Direction.DOWN, remainingDepth)) {
            return true;
        }

        // pick random horizontal
        RandomSource random = Objects.requireNonNull(this.owner.getLevel()).getRandom();
        int startIndex = inputFace.get2DDataValue() == -1 ? random.nextInt(4) : inputFace.get2DDataValue();
        if (random.nextBoolean()) {
            for (int i = 0; i < 4; i++) {
                Direction selected = Direction.from2DDataValue(startIndex + i);
                if (selected != inputFace && this.tryInsert(payload, selected, remainingDepth)) {
                    return true;
                }
            }
        } else {
            for (int i = 3; i >= 0; i--) {
                Direction selected = Direction.from2DDataValue(startIndex + i);
                if (selected != inputFace && this.tryInsert(payload, selected, remainingDepth)) {
                    return true;
                }
            }
        }

        // if there's no horizontal direction to move, check the up direction
        // this is last priority because gravity
        if (inputFace != Direction.UP && this.tryInsert(payload, Direction.UP, remainingDepth)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean tryInsert(DuctPayload payload, Direction outputFace, int remainingDepth) {
        // make sure direction is actually open
        if (!this.isDirectionOpen(outputFace)) {
            return false;
        }

        // check for nodes next
        @Nullable DuctNode node = this.nodes[outputFace.ordinal()];
        if (node != null) {
            if (node.push(payload, outputFace, remainingDepth)) {
                node.setPayload(payload);
                return true;
            } else {
                return false;
            }
        }

        // we can skip the next ops if payload is null or empty
        if (payload == null || payload.isEmpty()) {
            return true;
        }

        // check for storages
        @Nullable Storage<ItemVariant> storage = this.storages[outputFace.ordinal()];
        if (storage != null) {
            return payload.insert(storage);
        }

        return this.tryDumpPayloadFromFace(Objects.requireNonNull(this.owner.getLevel()), this.owner.getBlockPos(), payload, outputFace);
    }

    private boolean tryDumpPayloadFromFace(Level level, BlockPos ownerPos, DuctPayload payload, Direction outputFace) {
        BlockPos neighborPos = ownerPos.relative(outputFace);
        BlockState neighborState = level.getBlockState(neighborPos);
        if (!canDumpItemEntitiesInto(level, neighborPos, outputFace.getOpposite(), neighborState)) {
            return false;
        }

        Vec3 centerPos = ownerPos.getCenter();
        float ejectedItemOutputVelocity = DEFAULT_EJECTED_ITEM_OUTPUT_VELOCITY;
        if (level instanceof ServerLevel serverLevel && payload != null) {
            for (ItemStack stack : payload.stacks) {
                if (!stack.isEmpty()) {
                    ItemEntity droppedItem = new ItemEntity(
                            serverLevel,
                            centerPos.x,
                            centerPos.y,
                            centerPos.z,
                            stack.copy(),
                            outputFace.getStepX() * ejectedItemOutputVelocity,
                            outputFace.getStepY() * ejectedItemOutputVelocity,
                            outputFace.getStepZ() * ejectedItemOutputVelocity
                    );
                    float width = droppedItem.getBbWidth();
                    float height = droppedItem.getBbHeight();
                    droppedItem.setPos(
                            droppedItem.getX() + (outputFace.getStepX() * (0.5 + width / 2)),
                            droppedItem.getY() + (outputFace.getStepY() * (0.5 + (outputFace == Direction.UP ? 0 : height))),
                            droppedItem.getZ() + (outputFace.getStepZ() * (0.5 + width / 2))
                    );

                    serverLevel.addFreshEntity(droppedItem);
                }
            }
        }

        return true;
    }

    private boolean canDumpItemEntitiesInto(Level level, BlockPos pos, Direction face, BlockState state) {
        return !state.isFaceSturdy(level, pos, face, SupportType.RIGID);
    }

    public String getStatusForDirection(Direction direction) {
        if (!this.isDirectionOpen(direction)) {
            return "CLOSED";
        }

        if (this.storages[direction.ordinal()] != null) {
            return "STORAGE";
        }

        if (this.nodes[direction.ordinal()] != null) {
            return "NODE";
        }

        return "OPEN / DUMP";
    }
}
