package net.myriantics.klaxon.block.machines.duct.segment;

import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.redstone.NeighborUpdater;
import net.myriantics.klaxon.KlaxonCommon;
import net.myriantics.klaxon.block.machines.duct.BaseDuctComponentBlockEntity;
import net.myriantics.klaxon.mechanics.logistics.itemduct.DuctNode;
import net.myriantics.klaxon.mechanics.logistics.itemduct.DuctPayload;
import net.myriantics.klaxon.registry.block.KlaxonBlockEntityTypes;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class DuctSegmentBlockEntity extends BaseDuctComponentBlockEntity implements DuctNode {

    private static final Direction[] HORIZONTAL = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
    private static final Direction[] X_AXIS = new Direction[]{Direction.EAST, Direction.WEST};
    private static final Direction[] Z_AXIS = new Direction[]{Direction.NORTH, Direction.SOUTH};

    private final HashMap<Direction, Storage<ItemVariant>> dir2Storage = new HashMap<>(6);
    private final HashMap<Direction, DuctNode> dir2Node = new HashMap<>(6);
    private final HashSet<Direction> openDirections = new HashSet<>(6);

    protected DuctSegmentBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public DuctSegmentBlockEntity(BlockPos pos, BlockState state) {
        super(KlaxonBlockEntityTypes.DUCT_SEGMENT.value(), pos, state);
    }

    public void updateDirections(Direction changed, @Nullable Storage<ItemVariant> storage, boolean status) {
        if (status) {
            this.openDirections.add(changed);
        } else {
            this.openDirections.remove(changed);
        }

        if (status && this.level != null) {
            BlockPos neighborPos = this.worldPosition.relative(changed);
            @Nullable BlockEntity neighborBlockEntity = this.level.getBlockEntity(neighborPos);
            if (neighborBlockEntity instanceof DuctNode node) {
                this.dir2Node.put(changed, node);
            } else {
                this.dir2Storage.put(changed, storage == null ? ItemStorage.SIDED.find(this.level, neighborPos, null, neighborBlockEntity, changed.getOpposite()) : storage);
            }
        } else {
            this.dir2Node.remove(changed);
            this.dir2Storage.remove(changed);
        }
    }

    @Override
    public @Nullable Direction findNextDirection(Direction movementDirection) {
        Direction originDirection = movementDirection.getOpposite();

        // try moving in the same direction
        if (this.openDirections.contains(movementDirection)) {
            return movementDirection;
        }

        // down is second prio because gravity
        if (originDirection != Direction.DOWN && this.openDirections.contains(Direction.DOWN)) {
            return Direction.DOWN;
        }

        // check other axis but not the up direction
        Direction[] possibleMovementDirections = Arrays.stream(switch (movementDirection.getAxis()) {
            case X -> Z_AXIS;
            case Y -> HORIZONTAL;
            case Z -> X_AXIS;
        }).filter(direction -> direction != originDirection && this.openDirections.contains(direction)).toArray(Direction[]::new);

        // if there's no horizontal direction to move, check the up direction
        // this is last priority because gravity
        if (possibleMovementDirections.length == 0) {
            if (originDirection != Direction.UP && this.openDirections.contains(Direction.UP)) {
                return Direction.UP;
            } else {
                return null;
            }
        } else {
            return this.level == null ? null : possibleMovementDirections[this.level.getRandom().nextInt(possibleMovementDirections.length)];
        }
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
    }

    @Override
    public boolean push(Direction inputDirection, int depth) {
        if (depth == 0) {
            return false;
        } else {
            @Nullable DuctPayload payload = this.getPayload();
            @Nullable Direction nextDirection = this.findNextDirection(inputDirection);
            if (nextDirection == null) {
                return false;
            }
            @Nullable DuctNode nextNode = this.dir2Node.get(nextDirection);
            if (nextNode != null && nextNode.push(inputDirection, depth - 1)) {
                nextNode.setPayload(payload);
                this.clearPayload();
                return true;
            }
            if (payload == null) {
                return true;
            }

            @Nullable Storage<ItemVariant> storage = this.dir2Storage.get(nextDirection);
            if (storage != null && payload.insert(storage)) {
                this.clearPayload();
                return true;
            }
            this.dumpPayloadOnSide(nextDirection);
            return true;
        }
    }

    @Override
    public void setPayload(@Nullable DuctPayload payload) {
        super.setPayload(payload);
    }
}
